# Histórico de contexto — TASK-001 (fallback CEP)

| Campo | Valor |
|--------|--------|
| TASK | [TASK-001-cep-fallback-brasilapi.md](../tasks/TASK-001-cep-fallback-brasilapi.md) |
| PRD | [PRD-001](../prd/PRD-001-resiliencia-consulta-cep.md) |
| ADR | [0001](../adr/0001-viacep-e-alternativas-consulta-cep.md) |
| Branch | `feat/task-001-adr-0001-cep-fallback-brasilapi` |
| PR | *(preencher quando existir)* |
| Fechamento | 2026-04-17 |

---

## Resumo executivo

Entrega **TASK-001**: `AddressLocatorAdapter` passa a usar **ViaCEP** primeiro e **BrasilAPI v1** só em **falha técnica** (rede, 5xx, 429, 403, `RetryableException`, etc.), sem segunda chamada para CEP inexistente de negócio (`erro: true` na ViaCEP). Novo cliente Feign + DTO + MapStruct para BrasilAPI; testes unitários do adapter com mocks; `docs/02` e ADR alinhados ao comportamento.

---

## Decisões (escopo formal da task)

- Manter **`AddressLocatorPort`** e **`CustomerService`** sem mudança de assinatura.
- Critérios de “falha técnica” vs negócio alinhados à tabela da **ADR 0001**.
- Logs em **DEBUG** com `cep_provider=viacep|brasilapi`, sem dados pessoais.

---

## Interações / processo

- Workflow com **PRD aprovado** → **TASK aprovada** → implementação.
- **2026-04-17 (fechamento):** o solicitante pediu seguir o plano de PR e reforçou que **`docs/context/…-historico.md` não é opcional** — é **obrigatório** em todo fechamento de TASK; regras em `.cursor/rules/task-context-history.mdc` e `workflow-entrega.mdc` foram alinhadas a isso.
- Ajustes de build/versões **fora** do escopo formal desta TASK **não** são detalhados aqui.

---

## Verificação

- `mvn clean verify` com JDK compatível com o `pom.xml` do repositório.
- Branch: `feat/task-001-adr-0001-cep-fallback-brasilapi`; PR preenchido na tabela do topo após `gh pr create`.

---

## Problemas e soluções

- **Ordem de `catch` com `RetryableException` e `FeignException`:** tratar `RetryableException` dentro do fluxo de `FeignException` / `instanceof` para evitar erro de compilação (subclasse).
- **Beans MapStruct ausentes após upgrade de stack:** garantir `componentModel = "spring"` nos mappers relevantes e ordem de processadores Lombok + `lombok-mapstruct-binding` + `mapstruct-processor` no `pom.xml`.

---

## Follow-ups sugeridos (fora desta TASK)

- Circuit breaker / Resilience4j; terceiro elo pago; BrasilAPI v2 se precisar de geo; diagrama em `docs/05` com dois clientes Feign.
