#############################################
################ Build Stage ################
#############################################
FROM eclipse-temurin:17-jdk-alpine AS BUILDER
WORKDIR /workspace
COPY . .
RUN ./gradlew bootJar

#############################################
################ Final Stage ################
#############################################
FROM eclipse-temurin:17-jre-alpine
COPY --from=BUILDER /workspace/build/libs/*.jar /opt/app/application.jar
RUN addgroup -S spring \
    && adduser -S spring -G spring
USER spring:spring
CMD [ "java", "-jar", "/opt/app/application.jar" ]
