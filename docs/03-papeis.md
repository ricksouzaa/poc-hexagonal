# 03 — Leitura por papel

Objetivo: **o que importa para cada função** e **onde ver no repo**, sem repetir a narrativa de [01-visao-geral.md](01-visao-geral.md) nem as tabelas de [02-referencia-tecnica.md](02-referencia-tecnica.md). Tensões globais e roadmap: [05-revisao-multidisciplinar.md](05-revisao-multidisciplinar.md).

---

## Project Owner

- **Foco:** problema que a POC ilustra, o que o usuário **consegue** e **não consegue** fazer, expectativa vs produto real.
- **Em resumo:** cadastro de cliente com CPF + CEP e endereço via ViaCEP; não é CRM nem cadastro regulado completo.
- **Onde:** [01-visao-geral.md](01-visao-geral.md) (limites); [02-referencia-tecnica.md](02-referencia-tecnica.md) (regras de campo).

---

## Project Manager

- **Foco:** dependências de cronograma e demo.
- **Pontos:** ViaCEP e rede bloqueiam gravação; H2 zera ao reiniciar; Java 21 obrigatório no build.
- **Onde:** `pom.xml`, `application.properties`, tabela de dependências em [05-revisao-multidisciplinar.md](05-revisao-multidisciplinar.md).

---

## Software Architect

- **Foco:** fronteiras hexagonais e dívidas estruturais.
- **Coerente:** `CustomerService` só via portas; REST/JPA/Feign na borda; `CustomerConfig`.
- **Dívidas:** erro HTTP duplo (controller vs `@ControllerAdvice`); resiliência Feign; ADRs ausentes.
- **Onde:** `application/core/domains/customer/`, `adapters/`, `infrastructure/configs/`. Diagrama: [05-revisao-multidisciplinar.md](05-revisao-multidisciplinar.md).

---

## Backend Developer

- **Foco:** contratos e extensão sem vazar infra para o core.
- **Contratos:** `CustomerResource` → `CustomerServicePort` → adapters `CustomerPersistenceAdapter`, `AddressLocatorAdapter`, `ViaCepRestClient`.
- **Boas práticas no repo:** testes do serviço com mocks de portas; MapStruct.
- **Atenção:** 204 em lista vazia; i18n parcial de exceções.
- **Onde:** [02-referencia-tecnica.md](02-referencia-tecnica.md); `.cursor/rules/hexagonal-architecture.mdc`.

---

## Frontend / consumidor da API

- **Foco:** JSON, datas ISO, erros.
- **Contrato:** 400 + `ProblemDetail` vs404 por id; só CEP obrigatório no endereço no create.
- **Onde:** `adapters/in/rest/customer/dtos/`; OpenAPI em `/v3/api-docs`.

---

## DBA / dados

- **Foco:** integridade física vs regras de aplicação; evolução para produção.
- **Resumo:** duas tabelas, FK cliente → endereço, `create-drop`, sem Flyway/Liquibase.
- **Onde:** `adapters/out/persistence/customer/entities/`, **[04-modelo-dados.md](04-modelo-dados.md)** (análise + DDL).

---

## SRE / operações

- **Foco:** SPOF ViaCEP; configuração de dev (`show-sql`, H2 console, Swagger).
- **Evolução:** timeouts, health, desligar console/docs em prod.
- **Onde:** `application.properties`, `ViaCepRestClient`.

---

## QA

- **Foco:** cobertura vs comportamento documentado.
- **Hoje:** unitários do `CustomerService`; smoke Spring.
- **Lacunas sugeridas:** API (MockMvc/RestAssured), WireMock para ViaCEP, contrato OpenAPI.
- **Onde:** `src/test/java/`, matriz de erros em [02-referencia-tecnica.md](02-referencia-tecnica.md).

---

## Revisão integrada (vários papéis)

Síntese única, tensões e próximos passos: **[05-revisao-multidisciplinar.md](05-revisao-multidisciplinar.md)**. No Cursor, persona: `.cursor/skills/team-orchestrator/SKILL.md`.

---

[← Índice](README.md)
