# TASK-001 — Implementar fallback CEP (ViaCEP → BrasilAPI v1)

| Campo | Valor |
|--------|--------|
| **Status** | **Aprovado** — implementação e `mvn verify` concluídos (2026-04-17); abrir PR e fechar §7 |
| Número da entrega | 001 |
| Tipo | feature |
| Branch | `feat/task-001-adr-0001-cep-fallback-brasilapi` |
| PRD | [PRD-001-resiliencia-consulta-cep.md](../prd/PRD-001-resiliencia-consulta-cep.md) |
| ADR | [0001-viacep-e-alternativas-consulta-cep.md](../adr/0001-viacep-e-alternativas-consulta-cep.md) |
| **PR aberto** | *(preencher após `gh pr create`)* |

---

## 1. Resumo técnico

Implementar, atrás de **`AddressLocatorPort`**, a cadeia **ViaCEP (primária) → BrasilAPI v1** como fallback **somente** em **falha técnica** da primária (timeout, 5xx, rede,429/403, etc.), alinhado à **ADR 0001** e ao **PRD-001**. Não alterar assinatura de **`AddressLocatorPort`** nem **`CustomerService`**. Resposta de negócio “CEP inexistente” na ViaCEP (`erro: true` / sem CEP utilizável) **não** aciona a segunda API.

*Ajustes pontuais de versão de Java, Spring ou outras bibliotecas no repo são **fora do escopo formal** desta TASK (podem existir no branch por decisão paralela do time).*

---

## 2. Inventário — arquivos a **criar**

| Caminho |
|---------|
| `src/main/java/poc/hexagonal/adapters/out/clients/brasilapi/BrasilApiCepRestClient.java` |
| `src/main/java/poc/hexagonal/adapters/out/clients/brasilapi/dtos/BrasilApiCepResponse.java` |
| `src/main/java/poc/hexagonal/adapters/out/clients/brasilapi/mappers/BrasilApiCepMapper.java` |
| `src/test/java/poc/hexagonal/adapters/out/AddressLocatorAdapterTest.java` |

---

## 3. Inventário — arquivos a **alterar**

| Caminho | Motivo |
|---------|--------|
| `src/main/java/poc/hexagonal/adapters/out/AddressLocatorAdapter.java` | Orquestrar ViaCEP + fallback BrasilAPI; logs `cep_provider`; tratamento `FeignException` / técnico vs negócio |
| `src/main/java/poc/hexagonal/adapters/out/clients/viacep/dtos/ViaCepAddressResponse.java` | Campo opcional `erro` (não acionar fallback quando `true`) |
| `src/main/resources/application.properties` | `brasilapi.cep.base-url`; timeouts Feign (`findAddressByZipCode`, `brasilApiCep`) |

---

## 4. Inventário — documentação e config (fora de `src/`)

| Caminho |
|---------|
| `docs/02-referencia-tecnica.md` — ViaCEP, BrasilAPI v1, ordem de provedores, comportamento de fallback (alinhar à implementação) |
| `docs/context/TASK-001-cep-fallback-brasilapi-historico.md` — ao fechar a entrega (`task-context-history.mdc`) |

---

## 5. Checklist — execução técnica (implementação)

*Marcar apenas após concluir cada item. **Todos** devem estar `[x]` antes de passar à seção 6.*

- [x] Criar `BrasilApiCepRestClient` (Feign, `GET /api/cep/v1/{cep}`, base configurável).
- [x] Criar `BrasilApiCepResponse` + `BrasilApiCepMapper` (MapStruct → `Address`).
- [x] Ajustar `ViaCepAddressResponse` com `erro` e filtro no adapter (sem fallback nesse caso).
- [x] Refatorar `AddressLocatorAdapter`: primária ViaCEP; em falha técnica (ADR), BrasilAPI; normalizar CEP (somente dígitos no modelo); log `cep_provider=viacep|brasilapi` sem PII.
- [x] Configurar `application.properties` (base URL BrasilAPI + timeouts dos dois clientes Feign).
- [x] Criar `AddressLocatorAdapterTest` (sucesso ViaCEP; erro negócio ViaCEP sem BrasilAPI; 500 ViaCEP + sucesso BrasilAPI; 400 ViaCEP sem fallback; ambas falham).

---

## 6. Checklist — verificação e PR

*Executar **somente** quando a seção 5 estiver 100% `[x]`.*

- [x] `mvn clean verify` (JDK conforme `pom.xml` / política do repo).
- [x] Revisar `docs/02-referencia-tecnica.md` e marcar coerência com o código.
- [ ] `gh pr create` (título/descrição com PRD-001, TASK-001, ADR 0001, branch).

---

## 7. Checklist — pós-PR (antes do commit final que fecha a entrega)

- [ ] Tabela do topo: preencher **PR aberto** (URL ou #).
- [ ] Revisar seções 5 e 6: todos os itens refletem o estado real; ajustar notas se o review do PR mudou o escopo.
- [x] Criar/atualizar `docs/context/TASK-001-cep-fallback-brasilapi-historico.md`.
- [ ] **Commit(s)** incluindo: código + **esta TASK** (checklists atualizados) + `docs/02` + `docs/context` conforme aplicável.

---

## 8. Notas de execução

- Implementação validada com `mvn clean verify` (Java 21 no ambiente de verificação).
- ADR 0001 atualizada: fallback deixa de constar como “pendente” apenas em documento.

---

## Não escopo

- BrasilAPI v2 (geo); circuit breaker; API paga como terceiro elo.
