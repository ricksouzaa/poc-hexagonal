# Histórico de contexto — TASK-001 (fallback CEP)

| Campo | Valor |
|--------|--------|
| TASK | [TASK-001-cep-fallback-brasilapi.md](../tasks/TASK-001-cep-fallback-brasilapi.md) |
| PRD | [PRD-001](../prd/PRD-001-resiliencia-consulta-cep.md) |
| ADR | [0001](../adr/0001-viacep-e-alternativas-consulta-cep.md) |
| Branch | *(removida após merge)* — era `feat/task-001-adr-0001-cep-fallback-brasilapi`; trabalho atual em `main` |
| PR | https://github.com/ricksouzaa/poc-hexagonal/pull/1 |
| Repositório remoto | `git@github.com:ricksouzaa/poc-hexagonal.git` |
| Última atualização deste documento | 2026-04-17 |

---

## Leia-me primeiro (handoff para outro agente ou ferramenta)

Este arquivo é o **ponto único de continuidade** para a **TASK-001** e para o **arco de trabalho** que a precedeu no Cursor (personas, `docs/`, regras, implementação CEP, PR, merge).

**O que este documento contém**

- Metadados, **cronologia** das conversas (nível de resumo), **decisões**, **estado atual**, **pendências**, **comandos** e **arquivos-chave**.
- Instruções para **retomar** o trabalho em outra sessão (Cursor, Cloud Code, Windsurf, etc.): ler PRD/TASK/ADR, este arquivo, e o código referenciado.

**O que este documento *não* substitui**

- **PRD / TASK / ADR** — continuam a ser a fonte de verdade de escopo e decisão arquitetural.
- **Transcript literal completo** (cada mensagem trocada) — **não** é reproduzido aqui automaticamente. O Git e este markdown não recebem o log bruto do chat por padrão.

**É possível ter “todo o histórico de interação” aqui?**

- **Sim, mas só se alguém o colar ou anexar.** O Cursor (e ferramentas semelhantes) pode **exportar** o histórico da conversa; esse export pode ser colado na secção [Transcript / anexos](#transcript--anexos) abaixo, ou guardado como arquivo no repo (o time define política de tamanho e PII).
- Para referência interna no disco de desenvolvimento, parte do trabalho deste projeto aparece também no transcript agregado do Cursor (ver [Transcript / anexos](#transcript--anexos)).

---

## Onde paramos (estado em 2026-04-17)

| Área | Estado |
|------|--------|
| **Implementação TASK-001** | **Em `main`:** ViaCEP primária, BrasilAPI v1 só em **falha técnica**; `erro: true` ViaCEP **sem** segunda chamada; testes `AddressLocatorAdapterTest`; config Feign/timeouts. |
| **PR** | [#1](https://github.com/ricksouzaa/poc-hexagonal/pull/1) **mergeado**; branch de feature **apagada** (local e `origin`). |
| **`.cursor/`** | **Versionada** no Git (rules + skills). |
| **Documentação** | `docs/01`–`05`, PRD-001, TASK-001, ADR 0001, `docs/context/` (este arquivo); PRD §9 com nota (testes + homologação). |
| **Regras de workflow** | **`docs/context/…-historico.md` obrigatório** em todo fechamento de TASK — ver `.cursor/rules/task-context-history.mdc`, `workflow-entrega.mdc`, `task-creation.mdc`. |
| **README na raiz** | Versão expandida **commitada em `main`** (pós-merge do PR). |

**Próximos passos opcionais**

1. Smoke **E2E** / homologação (PRD RNF-01), se quiserem validação além dos testes unitários do adapter.
2. Próxima entrega: novo **PRD/TASK** + branch `feat/task-NNN-…` conforme `workflow-entrega.mdc`.

---

## Cronologia das interações (resumo para continuidade)

Ordem aproximada do que foi pedido e feito **nas sessões Cursor** ligadas a este repositório (não é transcript literal):

1. **Personas e skills** — Criação de `AGENTS.md` e skills em `.cursor/skills/` (Project Owner, PM, Backend, Frontend, DBA, SRE, QA).
2. **Interoperabilidade / orquestrador** — Esclarecimento de limites do Cursor; criação da skill **`team-orchestrator`** e atualização de `AGENTS.md`.
3. **Software Architect** — Skill `software-architect` e integração no fluxo e no orquestrador.
4. **Documentação inicial do sistema** — Pedido para o orquestrador “disparar” análise por personas; criação/evolução de `docs/` (hoje estruturado como `docs/README.md`, `01`–`05`, etc.; a primeira versão incluía arquivos como `analise-multidisciplinar.md` / `referencia-tecnica.md` que foram **reorganizados** no decorrer do projeto).
5. **Regras obrigatórias** — `.cursor/rules/hexagonal-architecture.mdc` e `solid-clean-architecture.mdc` com `alwaysApply: true` (hexagonal + SOLID + clean / Clean Architecture).
6. **Workflow de entrega** — Regras para PRD → TASK aprovada → implementação → PR → **`docs/context` obrigatório** (`workflow-entrega.mdc`, `task-creation.mdc`, `task-context-history.mdc`, `documentation-grounding.mdc`, `prd-creation.mdc`, `adr-creation.mdc`).
7. **TASK-001 (CEP)** — PRD-001 e ADR 0001; implementação do fallback BrasilAPI; ajustes MapStruct/Lombok/Feign; testes; atualização de `docs/02` e ADR.
8. **Fecho e PR** — `mvn clean verify` (Java 21); `gh pr create` → PR #1; preenchimento de checklists na TASK.
9. **Reforço do solicitante** — **`DocsContext` obrigatório** em todo fim de task (regras atualizadas para remover ambiguidade “opcional”).
10. **Conflito Git / `main` à frente** — `git merge origin/main`; conflito em `pom.xml` resolvido mantendo **ambos** os plugins; push; inclusão do restante de `.cursor/` no repositório.
11. **README** — Pedido de melhoria do README na raiz (conteúdo mais completo: stack, CEP, links para docs).
12. **Este documento** — Pedido de robustez para handoff cross-tool e esclarecimento sobre transcript completo.

---

## Resumo executivo (TASK-001)

Foi implementada, atrás de **`AddressLocatorPort`**, a cadeia **ViaCEP (primária) → BrasilAPI v1**, com fallback **apenas** em **falha técnica** da primária (timeout, 5xx, rede, 429, 403, `RetryableException`, etc.). Resposta de negócio “CEP inexistente” na ViaCEP (`erro: true` / resposta inutilizável) **não** aciona a segunda API. Inclui cliente Feign BrasilAPI, DTO, mapper MapStruct, logs em **DEBUG** `cep_provider=viacep|brasilapi` sem PII, e testes unitários do adapter.

---

## Decisões e políticas (além do código)

- **`AddressLocatorPort`** e **`CustomerService`**: sem alteração de assinatura (escopo TASK-001).
- Critérios **técnico vs negócio** alinhados à **ADR 0001**.
- **`docs/context/TASK-NNN-*-historico.md`**: **obrigatório** no fechamento de cada TASK com entrega real; ver regras em `.cursor/rules/`.
- **`.cursor/rules` e `.cursor/skills`**: devem **permanecer versionados** no Git (pedido explícito do solicitante).
- **PRD §9**: critérios marcados como atendidos ao nível de **testes de adapter** + comportamento acordado; **E2E/homologação** ainda recomendados.

---

## Verificação e Git

- **Build:** `mvn clean verify` com **JDK 21** (`pom.xml` exige `[21,)` via Enforcer). Em ambientes com Java 8 por padrão:  `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn clean verify`
- **Branch:** entrega em `main` (branch de feature removida após merge).
- **Commits relevantes (mensagens):** entrega TASK-001; docs PR/link PR; merge `main` + pom; chore `.cursor` + PRD/TASK; possivelmente commits posteriores (README, este histórico).

---

## Problemas encontrados e como foram resolvidos

| Problema | Resolução |
|----------|-----------|
| `RetryableException` vs `FeignException` em multi-catch | Tratar `RetryableException` no fluxo de `FeignException` / `instanceof` (subclasse). |
| Beans MapStruct ausentes após upgrade | `componentModel = "spring"` nos mappers; ordem Lombok + `lombok-mapstruct-binding` + `mapstruct-processor` no `pom.xml`. |
| PR conflitante com `main` | Merge `origin/main`; `pom.xml`: **dois** plugins (`maven-enforcer-plugin` + `maven-release-plugin`). |
| `mvn verify` falhando localmente | JDK errado no PATH; usar Java 21 explícito. |

---

## Arquivos e pastas mais tocados (referência rápida)

| Caminho | Notas |
|---------|--------|
| `src/main/java/.../adapters/out/AddressLocatorAdapter.java` | Orquestração ViaCEP → BrasilAPI. |
| `src/main/java/.../adapters/out/clients/brasilapi/**` | Cliente Feign, DTO, mapper. |
| `src/main/java/.../adapters/out/clients/viacep/**` | DTO com `erro`; cliente ViaCEP. |
| `src/test/java/.../AddressLocatorAdapterTest.java` | Cenários de sucesso, negócio, fallback, 400, dupla falha. |
| `src/main/resources/application.properties` | URLs e timeouts Feign. |
| `docs/tasks/TASK-001-*.md`, `docs/prd/PRD-001-*.md`, `docs/adr/0001-*.md` | Rastreabilidade. |
| `.cursor/rules/**`, `.cursor/skills/**` | Governança de agentes e código. |

---

## Follow-ups (fora ou além da TASK-001)

- Circuit breaker / Resilience4j; terceiro elo pago; BrasilAPI v2 (geo); métricas agregadas por `cep_provider`.
- `docs/04-modelo-dados.md`: checklists abertos sobre CPF único, 1:1 endereço, etc. — **não** são da TASK-001.
- Mockito / agent JVM (avisos em build sobre inline mock maker em JDK futuros).

---

## Transcript / anexos

**Política:** para replicação **fiel** de cada mensagem trocada, exporte o chat no Cursor (ou copie o histórico) e cole **abaixo** ou adicione um arquivo em `docs/context/transcripts/` com nome tipo `TASK-001-cursor-export-YYYYMMDD.md` (se o time autorizar).

**Referência a transcript local do Cursor (ambiente do Ricardo):** o agregado de mensagens deste projeto costuma existir sob a pasta de transcripts do Cursor; um id de conversa associado ao trabalho extenso no repo inclui  
[Sessões POC e documentação](555af7f1-2619-4748-916e-5ac15e503ca3)  
(caminho típico no disco: `~/.cursor/projects/.../agent-transcripts/<uuid>/...jsonl`). **Outros agentes ou máquinas não têm esse arquivo** — por isso este `historico.md` e o Git são o canal portátil.

```markdown
<!-- Cole export do chat abaixo, se desejar fidelidade total -->

```

---

## Checklist para o próximo agente (copiar/colar mentalmente)

- [ ] Li a **TASK-001** e a **ADR 0001**.
- [ ] Li **este** `historico.md` e a secção **Onde paramos**.
- [ ] Confirmei `git status` / branch / se **README** (ou outros) falta push.
- [ ] Abri o **PR #1** e verifiquei se está mergeável e se CI (se houver) passa.
- [ ] Se for implementar nova task: respeitar **workflow-entrega** (TASK aprovada antes de código) e criar/atualizar **`docs/context`** ao fechar.
