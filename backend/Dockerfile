FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

ARG MAVEN_MIRROR_URL=https://repo.huaweicloud.com/repository/maven/

RUN cat <<EOF >/tmp/settings.xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>repo-mirror</id>
      <name>Repository Mirror</name>
      <url>${MAVEN_MIRROR_URL}</url>
      <mirrorOf>*</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF

COPY pom.xml ./
RUN mvn -s /tmp/settings.xml -B -ntp -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -s /tmp/settings.xml -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_OPTS=""

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN addgroup --system shop && adduser --system --ingroup shop shop

COPY --from=build /app/target/*.jar /app/app.jar

RUN chown -R shop:shop /app

USER shop

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
