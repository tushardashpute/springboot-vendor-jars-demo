FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar /app/app.jar
COPY lib/ /app/lib/
EXPOSE 33333
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp '/app/app.jar:/app/lib/*' org.springframework.boot.loader.JarLauncher"]
