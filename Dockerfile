# 실행 전용 런타임 이미지 (JRE 기반으로 슬림하게 유지)
FROM eclipse-temurin:17-jre-jammy

# curl과 tzdata 설치 → healthcheck 및 타임존 관리용
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      curl tzdata \
 && rm -rf /var/lib/apt/lists/*

# JVM과 컨테이너 모두 한국 시간(KST)으로 맞춤
ENV TZ=Asia/Seoul \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75 -Duser.timezone=Asia/Seoul -Dfile.encoding=UTF-8"

# 비루트 계정 추가 (보안 목적)
RUN useradd -r -s /sbin/nologin appuser

# 작업 디렉토리
WORKDIR /app

# 호스트에서 미리 빌드한 jar 파일만 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# jar 파일 권한을 비루트 계정으로 변경
RUN chown -R appuser:appuser /app
USER appuser

# EXPOSE는 문서화용, 실 노출은 docker-compose에서 결정
# EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]