# Используем официальный образ Java
FROM eclipse-temurin:17-jdk-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем .jar-файл
COPY target/*.jar app.jar

# Порт, на котором работает Spring Boot
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
