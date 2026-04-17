# poc-hexagonal

POC de **API REST** em **arquitetura hexagonal** com **Spring Boot 4** e **Java 21**: cadastro de clientes com validação de CPF/CEP e **enriquecimento de endereço** via APIs públicas de CEP.

## O que este repositório demonstra

| Tema | Detalhe |
|------|---------|
| **Domínio** | Clientes (CRUD), validações no núcleo, persistência JPA |
| **Portas e adaptadores** | REST de entrada; ViaCEP / BrasilAPI como saída; H2 em desenvolvimento |
| **Resiliência de CEP** | **ViaCEP** como fonte primária; **BrasilAPI v1** como fallback em **falha técnica** (não em “CEP inexistente”). Ver [ADR 0001](docs/adr/0001-viacep-e-alternativas-consulta-cep.md) |
| **Contrato HTTP** | OpenAPI / **Swagger UI** em execução local |

A documentação completa (fluxos, pacotes, erros HTTP, H2) está em **[docs/README.md](docs/README.md)** — comece por [01 — visão geral](docs/01-visao-geral.md) e [02 — referência técnica](docs/02-referencia-tecnica.md).

## Pré-requisitos

- **JDK 21+** (o `pom.xml` usa o *Maven Enforcer* para exigir essa faixa).
- **Maven3.9+** (ou wrapper, se o projeto passar a incluir `mvnw`).

## Build e testes

```bash
mvn clean verify
```

Com mais de uma JDK no sistema, aponte `JAVA_HOME` para a JDK 21 antes do Maven:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn clean verify
```

## Executar a aplicação

```bash
mvn spring-boot:run
```

| Recurso | URL (padrão) |
|---------|----------------|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI (JSON)** | http://localhost:8080/v3/api-docs |
| **Console H2** | http://localhost:8080/h2 — credenciais em `src/main/resources/application.properties` |

## Estrutura de código (resumo)

Código Java sob `src/main/java/poc/hexagonal/` segue o recorte **core** (domínio e portas) vs **adapters** (REST, Feign, JPA). Convenções e camadas: [02-referencia-tecnica.md](docs/02-referencia-tecnica.md) e regras em [`.cursor/rules/`](.cursor/rules/).

## Documentação e entregas rastreáveis

- **PRDs / TASKs / contexto pós-task:** `docs/prd/`, `docs/tasks/`, `docs/context/`
- **Personas de agente (Cursor):** [AGENTS.md](AGENTS.md)

## Licença e propósito

Repositório de **prova de conceito** e estudo; não substitui definições de produto, segurança ou operações de um sistema em produção.
