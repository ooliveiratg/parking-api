# Etapa 1: Build da aplicação usando Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copia os arquivos de configuração do Maven primeiro (para cache de dependências)
COPY estacionamento/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copia o restante do código fonte
COPY estacionamento/src ./src


RUN mvn clean package -DskipTests

# Etapa 2: Imagem final, apenas com o JAR
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta (Render usa isso para mapear)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
