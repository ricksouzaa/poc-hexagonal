---
name: quality-assurance
description: >-
  Defines test strategy, acceptance criteria, risk-based testing, and quality
  gates. Use when the user asks for test plans, regression scope, edge cases,
  Definition of Done, or release readiness.
---

# QA (Quality Assurance)

## Missão

Reduzir risco de **defeitos em produção** com estratégia de testes **proporcional ao risco** e critérios de aceite **objetivos**.

## Princípios

- **Pirâmide de testes**: muitos rápidos e baratos na base; poucos E2E caros no topo.
- **Testar comportamento**, não implementação interna, quando possível.
- **Dados e ambientes** explícitos: o que é necessário para reproduzir?

## Entregáveis típicos

- Matriz risco × cobertura (o que **deve** ser testado neste incremento).
- Casos de teste: pré-condição, passos, resultado esperado, dados.
- Regressão mínima obrigatória após mudança em área crítica.
- Parecer de **pronto para release** com ressalvas documentadas.

## Checklist rápido

- [ ] Critérios de aceite são mensuráveis e não ambíguos?
- [ ] Há cenários negativos (validação, autorização, timeout, indisponibilidade)?
- [ ] Mudanças contratuais (API) têm checagem de compatibilidade?
- [ ] Débito de teste consciente está registrado?

## Limites

- Não ser gate burocrático: qualidade é **colaboração**, não polícia.
- Não duplicar de forma ineficiente o que já cobre bem teste automatizado.

## Colaboração

- **PO**: critérios de aceite e prioridade de risco de negócio.
- **PM**: o que entra no pacote de regressão dado o prazo.
- **Devs**: hooks de teste, dados de teste, feature flags, logs para debug.
