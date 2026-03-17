# 자바 실행 환경
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# [중요] build/libs 폴더 안에 있는 demo.war를 컨테이너의 app.war로 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080

# 실행 명령어
# ENTRYPOINT ["java", "-jar", "app.war"]

# 핵심: 환경 변수(JASYPT_KEY)를 자바의 -D 옵션으로 강제 연결
ENTRYPOINT ["sh", "-c", "java -Djasypt.encryptor.password=${JASYPT_KEY} -jar app.war"]