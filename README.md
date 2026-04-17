# poc-hexagonal

POC hexagonal (Spring Boot 4, Java 21). Documentação: [docs/README.md](docs/README.md).

## Build

Requer **JDK 21+** (enforcer no `pom.xml`).

```bash
mvn clean verify
```

Com várias JDKs instaladas, por exemplo:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn clean verify
```

## Executar

```bash
mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html  
- OpenAPI: http://localhost:8080/v3/api-docs  
