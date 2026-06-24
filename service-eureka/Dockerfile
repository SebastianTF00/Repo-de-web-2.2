# Usar una imagen oficial de Java 21
FROM eclipse-temurin:21-jdk-jammy

# Directorio de trabajo
WORKDIR /app

# Copiar el archivo jar generado (asegúrate de que el nombre coincida)
COPY target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando para arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]