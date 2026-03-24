# [1단계: 빌드 스테이지] - 여기서 청소와 빌드를 한 번에!
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /build

# Gradle 래퍼와 설정 파일들을 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 먼저 다운로드 (코드 수정 시에도 라이브러리 다운로드는 생략됨)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사 및 빌드 (리더님이 원하신 '클린' 로직 포함)
COPY src src
# [변경 이유]: clean을 붙여 이전 잔해를 지우고, -x test로 빌드 속도를 확보합니다.
RUN ./gradlew clean bootWar -x test --no-daemon

# [2단계: 실행 스테이지] - 최종 이미지 크기를 줄입니다.
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# [변경 이유]: 1단계 빌드 결과물만 쏙 빼옵니다. 경로 주의!
COPY --from=build /build/build/libs/*.war app.war

EXPOSE 8080

# 핵심: 환경 변수(JASYPT_KEY) 전달 로직 유지
ENTRYPOINT ["sh", "-c", "java -Djasypt.encryptor.password=${JASYPT_KEY} -jar app.war"]