# Сборка WAR-файла
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Запуск WAR в Tomcat
FROM tomcat:9.0
COPY --from=build /app/target/Person.war /usr/local/tomcat/webapps/ROOT.war

# Удалим ненужные приложения Tomcat
RUN rm -rf /usr/local/tomcat/webapps/examples \
           /usr/local/tomcat/webapps/docs \
           /usr/local/tomcat/webapps/manager \
           /usr/local/tomcat/webapps/host-manager
