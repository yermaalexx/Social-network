# Етап 1: Збірка
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /application

# Завантаження залежностей (кешування)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копіюємо весь код і збираємо
COPY . .
RUN mvn clean package -DskipTests

# Переіменовуємо скомпільований JAR у просту назву
RUN cp target/*.jar application.jar

# Розпакування шарів Spring Boot
RUN java -Djarmode=layertools -jar application.jar extract

# Етап 2: Кінцевий образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /application

# Копіюємо необхідні файли
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./

# Виставляємо порт
EXPOSE 8080

# Запуск
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.JarLauncher"]