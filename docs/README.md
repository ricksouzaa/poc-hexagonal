# Documentação — `poc-hexagonal`

Índice único. Cada arquivo tem **um papel**; evite repetir o mesmo tema em vários lugares — use os links abaixo.

---

## Estrutura (ordem sugerida)

| # | Arquivo | Conteúdo |
|---|---------|----------|
| **01** | [01-visao-geral.md](01-visao-geral.md) | **O quê e por quê:** propósito, fluxos de negócio, limites, exemplos JSON, diagrama de sequência |
| **02** | [02-referencia-tecnica.md](02-referencia-tecnica.md) | **Como é feito:** stack, pacotes, API, DTOs, erros HTTP, ViaCEP, testes, URLs Swagger/H2 |
| **03** | [03-papeis.md](03-papeis.md) | **Para quem trabalha no quê:** leitura objetiva por papel (PO, PM, arquiteto, devs, DBA, SRE, QA) |
| **04** | [04-modelo-dados.md](04-modelo-dados.md) | **Dados:** riscos do schema, DDL H2, script endurecido de referência |
| **05** | [05-revisao-multidisciplinar.md](05-revisao-multidisciplinar.md) | **Revisão de time:** síntese, diagrama hexagonal, tensões, próximos passos |

### ADRs (decisões de arquitetura)

| ID | Arquivo |
|----|---------|
| 0001 | [adr/0001-viacep-e-alternativas-consulta-cep.md](adr/0001-viacep-e-alternativas-consulta-cep.md) — CEP: ViaCEP (primária), **BrasilAPI v1 (fallback recomendado)**, pagas e outras opções |

### Contexto por tarefa (histórico IA / agentes)

| Conteúdo | Onde |
|-----------|------|
| Histórico consolidado por **TASK-NNN** (decisões, interações, verificações) | Pasta **`docs/context/`** — arquivos `TASK-NNN-<slug>-historico.md` (criados ao **fechar** cada task, ver `workflow-entrega.mdc` e `task-context-history.mdc`) |

---

## Caminhos de leitura

| Situação | Onde começar |
|----------|----------------|
| Primeiro contato | **01** → **02** |
| Vai implementar ou revisar código | **02** + `.cursor/rules/` (hexagonal, SOLID) |
| Precisa de ângulo de produto, risco ou dados | **03** (sua seção) e, se DBA, **04** |
| Retrospectiva / orquestração / dívida técnica global | **05** |

---

## Build e execução

Detalhes de **Java 21**, **jenv** e Maven: [README.md](../README.md) na raiz.

```bash
mvn spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`  
- H2 Console: `http://localhost:8080/h2` (credenciais em `application.properties`)

---

## Agentes Cursor (personas)

Definição de papéis para o assistente: [AGENTS.md](../AGENTS.md) e `.cursor/skills/`.

## Regras Cursor (`.cursor/rules/`)

| Regra | Efeito |
|--------|--------|
| `workflow-entrega.mdc` | **Sempre:** branch → descoberta → **PRD-NNN** aprovado → arquivo **TASK-NNN** → **`Status: Aprovado` na TASK** (mandatório antes de código) → mensagem explícita para implementar → `mvn verify` → PR → `docs/context/TASK-NNN-*-historico.md` |
| `task-context-history.mdc` | **Sempre:** conteúdo mínimo do **histórico de contexto** por task em `docs/context/` |
| `documentation-grounding.mdc` | **Sempre:** ancorar respostas em `docs/01`–`05` e manter doc alinhada a mudanças |
| `hexagonal-architecture.mdc` | **Sempre:** arquitetura hexagonal |
| `solid-clean-architecture.mdc` | **Sempre:** SOLID e código limpo |
| `prd-creation.mdc` | Ao criar **PRD** / requisitos de produto |
| `adr-creation.mdc` | Ao criar **ADR** (decisão de arquitetura; “ABR” tratar como ADR salvo outro formato) |
| `task-creation.mdc` | **TASK** com inventário (criar/alterar), **3 checklists** (técnica → verificação/PR → pós-PR), revisão obrigatória durante execução |

Artefatos sugeridos pelo workflow: **`docs/prd/`**, **`docs/tasks/`**, **`docs/context/`** (histórico pós-task).

Regras com `alwaysApply: false` entram quando o **tema** bate na `description` ou quando você as menciona com `@` no Cursor.
