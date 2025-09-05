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
RUN groupadd -r appuser && useradd -r -g appuser -s /usr/sbin/nologin appuser

# 작업 디렉토리
WORKDIR /app

# ✅ 오직 고정된 산출물만 복사 (Actions가 생성하는 build/libs/app.jar)
COPY build/libs/app.jar app.jar

# 권한
RUN chown -R appuser:appuser /app
USER appuser

# Spring Boot 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]