FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 安装 Maven
RUN apk add --no-cache maven

# 配置阿里云 Maven 镜像
RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><name>Aliyun Maven</name><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=0 /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]