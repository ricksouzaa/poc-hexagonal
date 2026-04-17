# 02 — Referência técnica

Fatos para implementação e integração. Narrativa e limites de produto: [01-visao-geral.md](01-visao-geral.md). Dados e DDL: [04-modelo-dados.md](04-modelo-dados.md).

---

## Stack

| Item | Valor |
|------|--------|
| Java | **21** (`pom.xml`; `maven-enforcer-plugin` exige `>=21`) |
| Spring Boot | **4.0.5** |
| Spring Cloud | **2025.1.1** (Oakwood — OpenFeign no BOM) |
| JPA / Hibernate | Hibernate **7.2.x** (via Boot 4) |
| Banco (padrão) | H2 em memória `jdbc:h2:mem:hexagonal` |
| OpenAPI | Springdoc **3.0.3** |
| Validação | Jakarta Validation |
| MapStruct | **1.6.3** + Lombok — `@Mapper(componentModel = "spring")`; no Maven, ordem dos annotation processors: **Lombok → lombok-mapstruct-binding → mapstruct-processor** |

---

## Pacotes (hexagonal)

```
poc.hexagonal
├── application.core          # domínio, portas, CustomerService, CoreException
├── adapters.in.rest        # CustomerResource, DTOs, mappers REST
├── adapters.out            # persistência, ViaCEP + BrasilAPI (Feign), `AddressLocatorAdapter`
└── infrastructure          # @Configuration, ControllerAdvice, SpringDoc
```

---

## Regras no domínio (`CustomerService`)

- `taxIdNumber`: **11** caracteres (DTO: `@Size(11)`).
- Endereço obrigatório; CEP **8** caracteres no request.
- `save`: endereço substituído pelo retorno de `AddressLocatorPort` (ViaCEP primário; BrasilAPI v1 só em falha técnica — ver seção CEP abaixo).

---

## DTOs REST

**Request**

| Campo | Obrigatório | Nota |
|--------|-------------|------|
| `name` | sim | |
| `taxIdNumber` | sim | 11 chars |
| `birthday` | não | `LocalDate` → ISO date no JSON |
| `address.zipCode` | sim | 8 chars |

**Response:** `id` (UUID), espelho dos dados + `address.*` vindos do persistido.

---

## API `/customers`

| Método | Caminho | Resposta típica |
|--------|---------|-------------------|
| POST | `/customers` | 201 sem body |
| PUT | `/customers/{id}` | 204 ou 404 |
| DELETE | `/customers/{id}` | 204 ou 404 |
| GET | `/customers/{id}` | 200 ou 404 |
| GET | `/customers` | 200 com lista ou **204** se vazia |

---

## Erros HTTP

| Situação | Status |
|----------|--------|
| Bean Validation no body | **400** `ProblemDetail` |
| `CoreException` (domínio) | **400** `ProblemDetail` |
| Cliente não existe (GET/PUT/DELETE por id) | **404** (tratado no controller) |
| Outras exceções | **500** |

**Contrato:** falha de CEP no POST costuma vir como **400** (`AddressNotFoundException`); alinhar com consumidores se desejarem outro código.

---

## Persistência (resumo)

Entidades `CustomerEntity` / `AddressEntity`; DDL gerado, `create-drop`. Detalhes, riscos e scripts SQL: [04-modelo-dados.md](04-modelo-dados.md).

---

## Consulta de CEP (ViaCEP + fallback BrasilAPI v1)

Ordem e contratos (detalhes e critérios de fallback: [adr/0001-viacep-e-alternativas-consulta-cep.md](adr/0001-viacep-e-alternativas-consulta-cep.md)):

| Ordem | Cliente Feign | URL / path |
|-------|---------------|------------|
| 1ª | `ViaCepRestClient` | `https://viacep.com.br/ws/{zipCode}/json` |
| 2ª (só falha técnica na 1ª) | `BrasilApiCepRestClient` | `{brasilapi.cep.base-url}/api/cep/v1/{cep}` — padrão `https://brasilapi.com.br` |

- **`AddressLocatorAdapter`** implementa `AddressLocatorPort`: tenta ViaCEP; se resposta de negócio (`erro: true` ou sem CEP útil), retorna vazio **sem** chamar BrasilAPI; se `FeignException` / rede indicar falha técnica (ex.: 5xx, 429, 403, sem status), tenta BrasilAPI v1; CEP no modelo fica **só dígitos** (8 posições).
- **Observabilidade:** logs em nível DEBUG com `cep_provider=viacep|brasilapi` (sem dados pessoais).
- **Timeouts Feign:** `application.properties` — `findAddressByZipCode` (ViaCEP) e `brasilApiCep` (`connect-timeout` / `read-timeout`).

---

## Configuração útil

- `CustomerConfig`: bean `CustomerServicePort`.
- H2 console: `/h2`
- `spring.jpa.show-sql=true` (dev)

---

## Testes

- `CustomerServiceTest` — unitário, portas mockadas  
- `AddressLocatorAdapterTest` — unitário, ViaCEP + BrasilAPI mockados (fallback e não-fallback)  
- `PocHexagonalApplicationTests` — contexto Spring  

---

## URLs locais (porta 8080)

| Recurso | URL |
|---------|-----|
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2 |

OpenAPI metadata: `SpringDocConfig` (título “Hexangonal POC API”, contato ClickBus).

---

## Execução

```bash
mvn spring-boot:run
```

Java **21** alinhado ao `pom.xml`. Credenciais H2 em `application.properties`. Build: `mvn clean verify`.

---

[← Índice](README.md) · [01 Visão geral](01-visao-geral.md) · [03 Papéis](03-papeis.md)
