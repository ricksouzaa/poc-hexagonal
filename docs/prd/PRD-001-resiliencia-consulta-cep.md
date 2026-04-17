# PRD — Resiliência na consulta de CEP (fallback)

| Campo | Valor |
|--------|--------|
| Número da entrega (PRD/TASK) | 001 |
| Versão | 0.1 |
| Data | 2026-04-17 |
| Status | **Aprovado** (2026-04-17 — autorização explícita para prosseguir) |
| Branch | `main` *(entrega via PR #1; branch de feature removida)* |
| TASK | [TASK-001-cep-fallback-brasilapi.md](../tasks/TASK-001-cep-fallback-brasilapi.md) |
| ADR de referência | [0001 — ViaCEP e fallback BrasilAPI v1](../adr/0001-viacep-e-alternativas-consulta-cep.md) |

---

## 1. Contexto e problema

Hoje o cadastro de cliente depende de **uma única fonte pública** para transformar CEP em endereço completo. Quando essa fonte está **indisponível** ou **falha de forma transitória**, o usuário não consegue concluir o cadastro mesmo com um CEP válido — experiência ruim e risco operacional.

## 2. Objetivos

- **O1:** Reduzir falhas de cadastro causadas por **indisponibilidade pontual** da fonte primária de CEP.
- **O2:** Manter o comportamento esperado pelo usuário: informou CEP válido e dados coerentes → deve haver caminho para obter endereço quando **pelo menos uma** fonte confiável responder.
- **O3:** Não alterar a regra de negócio de “CEP inexistente”: se o CEP não existe nas bases, o resultado continua sendo **falha de negócio** clara (sem mascarar como sucesso).

## 3. Não-objetivos

- Trocar a fonte primária de CEP como caminho padrão.
- Garantir SLA contratual ou suporte 24/7 neste incremento.
- Incluir segunda consulta quando o CEP é **validamente inexistente** nas fontes (evitar trabalho inútil), salvo quando a primeira resposta for **ambígua** ou **inválida por falha de serviço** (ver ADR 0001).
- Integrar APIs **pagas** ou Correios neste incremento.

## 4. Personas / usuários-alvo

- **Integrador ou operador de cadastro** que depende da API para registrar clientes com endereço via CEP.
- **Time interno** que precisa de menor sensibilidade a incidentes na fonte primária.

## 5. Escopo funcional (prioridade)

| Prioridade | Item |
|------------|------|
| **Must** | Se a primeira fonte falhar por **motivo técnico** (indisponibilidade, erro de serviço), tentar **automaticamente** a segunda fonte definida na **ADR 0001** (BrasilAPI v1). |
| **Must** | Manter ViaCEP como **primeira** tentativa em condições normais. |
| **Should** | Observabilidade mínima de qual fonte atendeu (suporte/métricas), sem expor dados pessoais em log. |
| **Could** | Atualizar documentação técnica de referência (`docs/02`) alinhada ao comportamento implementado. |

## 6. Requisitos funcionais (negócio)

- **RF-01:** Dado um CEP válido (formato aceito pelo produto), quando a consulta na **primeira** fonte não for concluída com sucesso por **falha técnica**, o sistema deve **tentar** a **segunda** fonte (ADR 0001) antes de devolver erro ao usuário.
- **RF-02:** Dado um CEP **inexistente** com resposta de negócio clara na primeira fonte, o sistema **não** deve insistir na segunda fonte só para repetir o mesmo resultado (conforme tabela da ADR).
- **RF-03:** O resultado para cadastro (endereço enriquecido) deve permanecer **utilizável** da mesma forma, independentemente de qual fonte respondeu com sucesso. *Divergências pontuais entre fontes são aceitáveis neste incremento; tratamento fino de “fonte da verdade” fica em evolução futura se o negócio exigir.*

## 7. Requisitos não funcionais (negócio)

- **RNF-01:** Tempo extra na jornada com fallback deve ser **aceitável** para cadastro online (validar em homologação).
- **RNF-02:** Uso das APIs públicas alinhado a termos e volume moderado (sem varredura em massa).

## 8. Fluxos (resumo)

1. Usuário envia cadastro com CEP.
2. Sistema consulta **fonte primária** (ViaCEP).
3. Se **falha técnica** → consulta **fonte secundária** (BrasilAPI v1).
4. Se sucesso → persiste e retorna sucesso conforme regras atuais.
5. Se CEP inexistente → falha de negócio clara (sem sucesso falso).

## 9. Critérios de aceite (negócio)

*Verificação **pós-TASK-001** (2026-04-17): cenários abaixo cobertos por testes unitários de `AddressLocatorAdapter` e pelo comportamento descrito na ADR 0001; smoke **end-to-end** em homologação permanece recomendável (RNF-01).*

- [x] Com fonte primária **simulada indisponível** e secundária **ok**, cadastro com CEP válido **conclui** com endereço preenchido.
- [x] Com ambas falhando por motivo técnico, usuário recebe **erro** coerente (sem sucesso parcial enganoso).
- [x] Com CEP **inexistente** (resposta de negócio na primária), não há segunda chamada **só** para duplicar “não encontrado”.
- [x] Em condições normais, a fonte primária é tentada **primeiro**.

## 10. Dependências e riscos

- **Dependência:** ADR 0001 aceita (fallback BrasilAPI v1).
- **Risco:** APIs públicas sem SLA; possível indisponibilidade **simultânea** ou bloqueio por volume.
- **Risco:** pequena divergência de dados entre fontes — aceitável para este incremento (ver RF-03).

## 11. Métricas sugeridas (futuro operação)

- Cadastros concluídos após uso da fonte secundária (agregado).
- Falhas técnicas na fonte primária.

## 12. Open questions

- Nenhuma bloqueante para abrir **TASK-001** após aprovação deste PRD; evoluções (terceiro elo pago, regra formal de divergência) ficam fora do escopo.
