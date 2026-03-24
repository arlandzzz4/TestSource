# [1단계: 빌드 스테이지] - 변경 없음 (캐시 최적화 완벽)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew clean bootWar -x test --no-daemon

# [2단계: 실행 스테이지] - 경량화 및 안정성 극대화
# [변경 이유 1]: 컴파일러가 포함된 JDK 대신 JRE를 사용하여 이미지 크기를 약 40% 이상 줄이고 보안 취약점 노출을 최소화합니다.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# [변경 이유 2]: Alpine 리눅스의 기본 시간대(UTC)를 서울(KST)로 강제 동기화하여 DB 시간 불일치 장애를 예방합니다.
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# [변경 이유 3]: Gradle 빌드 시 -plain.war가 생성될 경우 COPY 명령어 에러가 발생하므로, 정규식을 보완하여 메인 war만 복사하도록 강제합니다.
# (만약 에러가 난다면 build.gradle에서 plain.war 생성을 끄는 것이 가장 좋습니다)
COPY --from=build /build/build/libs/*-SNAPSHOT.war app.war
# 혹은 COPY --from=build /build/build/libs/sisa-*.war app.war (프로젝트명 명시)

EXPOSE 8080

# [변경 이유 4]: Alpine 환경에서 JVM이 컨테이너의 가용 메모리를 잘못 인식해 OOM(Out of Memory)으로 뻗는 것을 막기 위해 MaxRAMPercentage 옵션을 부여합니다.
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75.0 -Djasypt.encryptor.password=${JASYPT_KEY} -jar app.war"]