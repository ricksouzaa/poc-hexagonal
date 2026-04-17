# Subagentes (personas) da equipe

Este repositório define **papéis de agente** alinhados a funções reais de produto e engenharia. Cada papel tem uma **skill** correspondente em `.cursor/skills/<papel>/SKILL.md`.

**Documentação do sistema:** índice em [docs/README.md](docs/README.md); visão geral [docs/01-visao-geral.md](docs/01-visao-geral.md); papéis [docs/03-papeis.md](docs/03-papeis.md).

**Regras Cursor:** `.cursor/rules/` — **`workflow-entrega.mdc`** (TASK aprovada antes de código; fases checklist técnico → verify/PR → pós-PR + commit com TASK atualizada); **`task-creation.mdc`** (inventário criar/alterar + 3 checklists obrigatórios); **`task-context-history.mdc`**; `documentation-grounding.mdc`; `prd-creation.mdc`; `adr-creation.mdc`.

## Team Orchestrator (gerente do time)

Não existe no Cursor um **processo nativo** que rode vários agentes em paralelo com fila de mensagens. O que funciona é **uma persona de orquestração**: uma skill que manda o modelo **passar pelos papéis relevantes em sequência** e **consolidar** decisões numa única resposta.

- **Skill:** `.cursor/skills/team-orchestrator/SKILL.md`
- **Quando usar:** “time inteiro”, “orquestrar”, “revisão multidisciplinar”, “como gerente de engenharia / tech lead coordenando o time”.
- **Comportamento:** triagem → contribuições por papel (só os necessários) → tensões → plano único com próximos passos.

Para trabalho focado (só backend, só QA, etc.), use **uma skill de papel** em vez do orquestrador.

## Como acionar papéis isolados

- Mencione o papel na conversa (ex.: “como **PM**, priorize isso”) **ou**
- Peça para aplicar a skill em `.cursor/skills/<papel>/`.

Com **orquestrador**, uma resposta pode cobrir vários papéis em seções; com **papel único**, manter um foco principal por mensagem (salvo pedido explícito de combinar duas visões, ex.: PM + QA).

## Mapa de subagentes → skills

| Subagente        | Skill (pasta)              | Foco principal                          |
|------------------|----------------------------|-----------------------------------------|
| Team Orchestrator| `team-orchestrator`        | Coordena papéis relevantes e consolida plano |
| Project Owner    | `project-owner`            | Visão, valor, escopo, decisões de negócio |
| Project Manager  | `project-manager`          | Planejamento, riscos, dependências, comunicação |
| Software Architect | `software-architect`     | Fronteiras, NFRs, integração, ADRs, trade-offs técnicos |
| Backend Dev      | `backend-developer`        | APIs, domínio, persistência, integrações |
| Frontend Dev     | `frontend-developer`       | UI/UX técnica, acessibilidade, performance web |
| DBA              | `database-administrator`   | Modelo de dados, índices, migrações, consistência |
| SRE              | `site-reliability-engineer`| Confiabilidade, observabilidade, deploy, SLOs |
| QA               | `quality-assurance`        | Estratégia de testes, qualidade, critérios de aceite |

## Fluxo sugerido (ordem lógica, não obrigatória)

1. **Project Owner** — problema, outcome, restrições, o que está fora de escopo.  
2. **Project Manager** — entregas, marcos, riscos, stakeholders.  
3. **Software Architect** — estrutura do sistema, contratos entre partes, NFRs, decisões gravosas (ADRs), encaixe hexagonal.  
4. **Backend / Frontend** — implementação nas camadas certas alinhada à arquitetura acordada.  
5. **DBA** — esquema, performance de dados, evolução segura do banco.  
6. **SRE** — como opera, monitora e recupera em produção.  
7. **QA** — pirâmide de testes, cenários, regressão e definição de “pronto”.

## Regras de colaboração

- **Arquiteto** consolida o **desenho técnico** entre camadas e integrações; **devs** executam e retornam dúvidas de implementação.  
- **Backend** e **Frontend** alinham contratos (API, eventos, erros) com referência ao que o arquiteto definiu para fronteiras e evolução.  
- **DBA** revisa mudanças que afetam esquema, índices ou volumes.  
- **SRE** exige observabilidade mínima para mudanças que impactam produção.  
- **QA** traduz requisitos em casos testáveis e critérios objetivos.  
- **Project Owner** desempata por **valor de negócio**; **PM** desempata por **capacidade e risco**; **Arquiteto** desempata por **padrão estrutural e trade-offs técnicos** (subordinado ao *o quê* do PO e ao *quando* do PM).

## Escopo deste projeto (`poc-hexagonal`)

Stack atual: **Java / Spring Boot**, arquitetura hexagonal — persona de **Arquiteto** deve reforçar ports/adapters e evitar vazamento de infraestrutura no core. Personas de **Frontend** aplicam-se quando houver camada web no repositório ou integração com clientes HTTP; até lá, usem-na para contratos de API e experiência consumindo os endpoints.
