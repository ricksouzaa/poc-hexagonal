# ADR 0001: Consulta de CEP — ViaCEP (primária), alternativas e **fallback**

- **Status:** Aceita — **ViaCEP (primária) + BrasilAPI v1 (fallback)** implementados em código (**TASK-001**); catálogo de alternativas e terceiro elo permanecem como horizonte na ADR.
- **Data:** 2026-04-17 · **Atualização fallback:** 2026-04-17

---

## Finalidade desta ADR

1. Registrar o **estado atual** da integração: **ViaCEP** como primeira fonte e **BrasilAPI v1** como fallback técnico atrás de `AddressLocatorPort`.
2. Documentar **outras APIs** (substituição, pagas, etc.) para decisões futuras.
3. **Centralizar a decisão de resiliência:** manter ViaCEP como **primeira** opção e adotar uma **segunda API** acionada **somente** quando a primeira falhar (timeout, erro HTTP, corpo inválido ou política de circuit breaker), **sem** substituir a integração atual como caminho feliz padrão.

O núcleo do domínio continua expondo apenas **`AddressLocatorPort`**; a cadeia primária + fallback deve morar em **um adaptador composto** (ou equivalente), evitando que `CustomerService` conheça múltiplos clientes HTTP.

---

## Contexto

O domínio de **Cliente** precisa **enriquecer endereço** a partir do **CEP** informado. No desenho **hexagonal**, isso é responsabilidade da porta de saída **`AddressLocatorPort`**, hoje implementada pelo adaptador **`AddressLocatorAdapter`**, que delega a um cliente HTTP.

Critérios relevantes para escolha de provedor **e de fallback**:

- Custo e necessidade de **API key**
- **Disponibilidade** e política de uso (incl. bloqueio por volume)
- Formato de resposta e **esforço de mapeamento** para o modelo `Address`
- **Manutenção** e risco de dependência de um único serviço gratuito
- Necessidade futura de **coordenadas**, **código IBGE** no core, ou **fallback** entre fontes
- Para **produção crítica:** disposição a **pagar** por limite previsível, **SLA** e **suporte**

---

## Estado atual (o que temos hoje) — ViaCEP

### Comportamento na aplicação

| Aspecto | Implementação |
|---------|----------------|
| Cliente HTTP | **`ViaCepRestClient`** (Spring Cloud **OpenFeign**), URL base `https://viacep.com.br` |
| Endpoint | `GET /ws/{zipCode}/json` — CEP **8 dígitos** sem máscara (hífen removido no adapter após resposta) |
| DTO | **`ViaCepAddressResponse`**: `cep`, `logradouro`, `complemento`, `bairro`, `localidade`, `uf` (JSON ViaCEP) |
| Mapeamento | **`ViaCepAddressMapper`** (MapStruct) → modelo de domínio **`Address`** |
| Pós-processamento | **`AddressLocatorAdapter`**: descarta respostas sem `zipCode` mapeável; normaliza CEP removendo `-` |
| Porta | **`AddressLocatorPort#findByZipCode`** retorna `Optional<Address>` — alinhado a “não encontrado” sem exceção HTTP no adapter |

### Contrato ViaCEP (oficial)

Segundo a documentação em [https://viacep.com.br/](https://viacep.com.br/):

- Webservice **gratuito** voltado a **alto desempenho** de consulta pontual.
- CEP com formato inválido → **HTTP 400**.
- CEP válido porém inexistente → corpo JSON com **`"erro": true`** (não é404 HTTP).
- Há alerta explícito de que **uso massivo** (ex.: validação de bases locais em loop) pode resultar em **bloqueio automático por tempo indeterminado**.
- Base com **ordem de grandeza de milhões de CEPs** (número divulgado no site, com data de atualização da base).

### Pontos fortes desta integração no código

- **Sem API key**; integração mínima com Feign.
- Campos ViaCEP mapeiam **naturalmente** ao agregado de endereço brasileiro.
- Tratamento de “não encontrado” compatível com `Optional` quando o corpo não traz CEP útil.

### Riscos / limitações reconhecidas

- **Sem SLA** nem suporte comercial formal.
- **Limite de uso** não numérico público; dependência de **boa conduta** e volume moderado.
- **Provedor único** no código: indisponibilidade ViaCEP derruba o fluxo de gravação que depende do CEP.
- **Resiliência** (timeout, retry, circuit breaker) **não** configurada no Feign nesta POC.

---

## Análise arquitetural — estratégia de fallback (visão de arquiteto)

### Objetivo do fallback

Reduzir **ponto único de falha** operacional: indisponibilidade ou bloqueio pontual do ViaCEP não deve, por si só, impedir o enriquecimento de endereço quando outra fonte pública válida ainda responde.

### Quando acionar a segunda API (critérios sugeridos)

| Situação | Acionar fallback? |
|----------|-------------------|
| Timeout ou erro de rede contra ViaCEP | **Sim** |
| HTTP 5xx do ViaCEP | **Sim** |
| HTTP 429 / bloqueio (se identificável) | **Sim** (e registrar para cap de volume) |
| Resposta vazia / sem CEP utilizável (equivalente a “não encontrado” no adapter atual) | **Não** para CEP inexistente — **ambas** as fontes tendem a não ter o CEP; **Sim** apenas se a primeira resposta for **malformada** ou ambígua |
| CEP com formato inválido (8 dígitos) | **Não** — falhar cedo no domínio; não adianta segunda API |

### Por que não usar API paga como *segundo* elo por padrão

APIs comerciais exigem **chave**, **faturamento** e, em muitos casos, **custo por requisição**. Colocá-las como **fallback automático** pode:

- gerar **custo** em picos de falha da primeira API;
- exigir **segredos** e rotação de chave na mesma cadeia que hoje é só Feign público.

Recomenda-se **terceiro degrau** opcional (paga ou Correios) só após **volume**, **SLA** ou decisão financeira explícita — não como substituto imediato da segunda linha gratuita.

### Candidatos à segunda posição (resumo)

| Candidato | Prós como fallback | Contras |
|-----------|-------------------|---------|
| **BrasilAPI v1** | **Operador e stack diferentes** do ViaCEP; sem API key no modelo público; comunidade e código aberto; endpoint **v1** alinhado a “só endereço” (sem cadeia de geo da v2). | JSON diferente → **novo cliente Feign + mapper**; mesmo “tipo” de serviço gratuito (sem SLA contratual). |
| **Postmon** | REST simples. | Histórico de **manutenção/disponibilidade** menos previsível; menos confiança como padrão corporativo sem due diligence. |
| **Provedor pago** | SLA e suporte. | Custo em toda falha da primeira linha; complexidade de credenciais. |

**Conclusão da análise:** a melhor relação **custo / independência / previsibilidade técnica** para **2ª API automática** é **BrasilAPI v1** (`GET /api/cep/v1/{cep}`), **não** a v2 (menos superfície de falha, paridade com o fluxo atual sem coordenadas).

---

## Alternativa analisada em profundidade: **BrasilAPI** (base para fallback recomendado)

**BrasilAPI** ([https://brasilapi.com.br/](https://brasilapi.com.br/)) é um projeto **open source** brasileiro que expõe diversos dados nacionais, incluindo **CEP**, com API REST pública.

### Endpoints relevantes (visão geral)

- **v1** — `GET /api/cep/v1/{cep}`: dados de endereço em formato próprio (campos como `state`, `city`, `neighborhood`, `street`, `cep`, etc.).
- **v2** — `GET /api/cep/v2/{cep}`: inclui **coordenadas** (dependência de cadeia de geocodificação); histórico de issues no repositório público indica que a v2 já teve falhas quando serviços externos (ex.: limites de geocoding) falhavam — com correções posteriores para ainda retornar dados de CEP quando possível.

Documentação e código: repositório **[BrasilAPI/BrasilAPI](https://github.com/BrasilAPI/BrasilAPI)**.

### Prós (BrasilAPI, especialmente v1 como substituto direto)

| Pró | Detalhe |
|-----|---------|
| **Comunidade e transparência** | Projeto aberto; evolução e incidentes visíveis no GitHub. |
| **Contrato JSON estável por versão** | Previsibilidade por path de versão (`v1` vs `v2`). |
| **Possível riqueza futura** | v2 oferece **localização** se o produto precisar de mapas/geo sem segunda integração imediata. |
| **Sem API key** para uso básico (conforme modelo atual do projeto público). | Reduz atrito igual ao ViaCEP. |
| **Agnosticismo de fonte interna** | A API pode compor/agregar fontes; reduz acoplamento conceitual a um único site “monolítico” do ponto de vista do consumidor (ainda depende da operação da BrasilAPI). |

### Contras (BrasilAPI)

| Contra | Detalhe |
|--------|---------|
| **Mapeamento diferente** | Campos e nomes **não** são os do ViaCEP; exige **novo DTO + mapper** (ou adaptação do existente) e testes. |
| **Sem SLA enterprise gratuito** | Assim como ViaCEP, uso em **alto volume** ou padrões abusivos pode ser **incompatível** com os termos do projeto (há orientações contra varredura em massa). |
| **v2 mais complexa** | Mais pontos de falha (geocoding); para **paridade** com o fluxo atual (só endereço), **v1** tende a ser o alvo mais simples. |
| **Disponibilidade** | Depende da infraestrutura da BrasilAPI; histórico de issues mostra que **erros 500** e regressões já ocorreram (com correções). |
| **Operação própria** | Se a equipe quiser **garantias** contratuais, ainda pode ser necessário **provedor pago** ou **instância própria** (fora do escopo gratuito comunitário). |

### Quando BrasilAPI tende a ser melhor escolha que “só ViaCEP”

- Desejo de **padronizar** integrações brasileiras na mesma **família de APIs** (CEP + outros recursos da BrasilAPI no futuro).
- Necessidade de **coordenadas** (optar por **v2** com tratamento de falha parcial).
- Estratégia de **abstração** no domínio (`AddressLocatorPort`) mantida, trocando apenas o adaptador — **baixo impacto no core** se bem feito.

### Quando manter ViaCEP pode ser preferível

- **Mínima superfície de mudança** e equipe já familiarizada com o contrato ViaCEP.
- Volume **baixo/médio** e aceitação explícita dos **termos de uso** e do risco de **bloqueio** em cenários de uso indevido.
- Não há necessidade imediata de **geo** nem de unificar com outros endpoints BrasilAPI.

---

## APIs pagas ou contratuais (terceira linha de opção)

Quando ViaCEP/BrasilAPI **gratuitos** não bastam (volume alto, **SLA**, auditoria, suporte), a alternativa natural é **contrato comercial** ou **SaaS de APIs**. A lista abaixo é **panorama de mercado** com base em **documentação e sites públicos** (2026); **preços, cotas e SLAs** devem ser **confirmados** com cada fornecedor antes de decisão.

### Correios — API Busca CEP (relação contratual)

- **Modelo:** API destinada a **clientes com contrato comercial** ativo com os Correios; há manuais de integração para ambientes de homologação e produção.
- **Prós:** fonte **oficial** ligada à operação postal; adequada quando já há **relação comercial** com Correios ou quando compliance exige canal formal.
- **Contras:** **não** é “self-service” como ViaCEP; ciclo de **contrato**, onboarding e possíveis custos associados à conta contratada; integração típica mais “enterprise”.

Referência: [Manual API Busca CEP — Correios Developers](https://www.correios.com.br/atendimento/developers/manuais/manual-api-busca-cep).

### APIBrasil (plataforma de APIs)

- **Modelo:** plataforma comercial com dezenas de APIs (incluindo CEP/CNPJ etc.), **documentação** em portal dedicado e acesso via **conta/plano**.
- **Prós (tipicamente anunciados):** **SLA** e disponibilidade em material de marketing, **suporte**, limites por plano, SDKs em várias linguagens — útil quando o time quer **um contrato** e previsibilidade operacional.
- **Contras:** **custo recorrente**; **vendor lock-in** na plataforma; necessidade de gestão de **API key** e conformidade com termos; valores e cotas **variando por plano** — obter proposta atual em [https://apibrasil.com.br/](https://apibrasil.com.br/) / [https://doc.apibrasil.io/](https://doc.apibrasil.io/).

### CepJá

- **Modelo:** API de CEP (e serviços correlatos) com **documentação** pública e autenticação por **API Key**; planos costumam ser apresentados por **volume** (ex.: requisições por dia), conforme página do produto.
- **Prós:** foco em **CEP**; contrato comercial claro para quem aceita pagar pelo uso.
- **Contras:** comparar **preço por requisição** com volume esperado; dependência de um **fornecedor menor** que ViaCEP/BrasilAPI em alcance de comunidade — avaliar saúde do serviço e roadmap.

Referência: [https://www.cepja.com.br/api-docs](https://www.cepja.com.br/api-docs).

### BrazilGuide / InfoCEP (API pública paga)

- **Modelo:** planos por assinatura com cotas mensais de requisições e **API Key** (conforme divulgação no site).
- **Prós:** pacotes que combinam **CEP com outros dados** (ex.: CNPJ, DDD), útil se o produto já precisar desses insumos no mesmo fornecedor.
- **Contras:** validar **atualização de preços** e limites no site; avaliar se o bundle compensa face a integrar **BrasilAPI** (gratuita) + outro provedor.

Referência: [https://infocep.com.br/api](https://infocep.com.br/api) / área de desenvolvedores.

### Nuvem Fiscal (uso misto)

- **Modelo:** foco em **APIs fiscais** (NF-e etc.); alguns planos incluem **cotas de consulta de CEP** junto com outros serviços.
- **Prós:** faz sentido se o sistema **já** é cliente Nuvem Fiscal para fiscal e pode **reutilizar** cota de CEP.
- **Contras:** **não** é um provedor “só CEP”; custo e complexidade desalinhados se a única necessidade for endereço.

Referência: [https://www.nuvemfiscal.com.br/](https://www.nuvemfiscal.com.br/) e documentação de API do produto.

### Síntese: gratuito vs pago

| Dimensão | ViaCEP / BrasilAPI (público) | Provedor pago / Correios |
|----------|------------------------------|---------------------------|
| Custo direto | Zero (tipicamente) | Assinatura + possível contrato |
| SLA / suporte | Não contratual (comunitário) | Costuma existir em contrato ou termos comerciais |
| Chave / onboarding | Não ou mínimo | Quase sempre API Key ou credencial contratual |
| Adequação | POC, baixo/médio volume, aceite de risco | Produção crítica, alto volume, exigência formal |

---

## Outras alternativas (panorama breve — não paga)

| Opção | Observação |
|-------|------------|
| **Postmon** (`api.postmon.com.br`) | Alternativa histórica REST; **não** recomendado como fallback padrão frente à BrasilAPI v1; due diligence se usado. |
| **Base local / dataset próprio** | **Prós:** controle e offline possível. **Contras:** **compliance**, atualização periódica da base, infraestrutura. |

Nenhuma delas foi implementada neste repositório; servem como **horizonte** para decisão.

---

## Decisão (registro)

1. **Provedor primário (implementado):** **ViaCEP** — permanece o caminho **padrão** em condições normais.
2. **Provedor de fallback (implementado):** **BrasilAPI v1** — `GET https://brasilapi.com.br/api/cep/v1/{cep}` — ver **recomendação final** abaixo.
3. **BrasilAPI v2** permanece **fora** da cadeia de fallback **inicial**: geocoding adiciona falhas sem ganho obrigatório para o modelo `Address` atual.
4. **Substituição completa** do ViaCEP pela BrasilAPI **não** é objetivo desta ADR; **terceiro elo** pago (Correios, SaaS) só após decisão de produto/financeira explícita.

---

## Consequências

- O **domínio** permanece desacoplado: **`AddressLocatorPort`** inalterada em assinatura; implementação evolui para **adaptador composto** (ViaCEP → se falha técnica, BrasilAPI v1) ou **decorator** equivalente.
- Novo **Feign client** + **DTO/mapper** BrasilAPI v1 → `Address`; testes de integração com mocks dos dois clientes.
- Configurar **timeouts curtos** na primeira chamada para não somar latência excessiva antes do fallback; **observabilidade** (log/métrica: `cep_provider=viacep|brasilapi`) para operação.
- Atualizar **`docs/02-referencia-tecnica.md`** quando o fallback for implementado (ordem, URLs, erros).

---

## Recomendação final (arquiteto) — cadeia de consulta CEP

| Ordem | API | Papel |
|-------|-----|--------|
| **1ª** | **ViaCEP** (`https://viacep.com.br/ws/{cep}/json`) | Primária — comportamento e contrato já alinhados ao código atual. |
| **2ª (fallback)** | **BrasilAPI v1** (`https://brasilapi.com.br/api/cep/v1/{cep}`) | Acionar somente em **falha técnica** da primeira (rede, timeout, 5xx, resposta inutilizável por erro de serviço — não para “CEP inexistente” em duplicidade). |

**Justificativa em uma frase:** BrasilAPI v1 oferece **fonte e operação distintas** do ViaCEP, **sem chave** no modelo público, **sem** a complexidade da v2, com custo de implementação aceitável dentro do **mesmo adaptador** atrás da porta hexagonal.

---

## Referências (consulta em 2026-04-17)

- ViaCEP — documentação e termos de uso: [https://viacep.com.br/](https://viacep.com.br/)
- BrasilAPI — site e documentação: [https://brasilapi.com.br/](https://brasilapi.com.br/)
- BrasilAPI — código e issues (CEP v2, tratamento de erros): [https://github.com/BrasilAPI/BrasilAPI](https://github.com/BrasilAPI/BrasilAPI)
- Correios Developers — manual API Busca CEP: [https://www.correios.com.br/atendimento/developers/manuais/manual-api-busca-cep](https://www.correios.com.br/atendimento/developers/manuais/manual-api-busca-cep)
- APIBrasil — [https://apibrasil.com.br/](https://apibrasil.com.br/) · documentação: [https://doc.apibrasil.io/](https://doc.apibrasil.io/)
- CepJá — documentação: [https://www.cepja.com.br/api-docs](https://www.cepja.com.br/api-docs)
- BrazilGuide / InfoCEP — API: [https://infocep.com.br/api](https://infocep.com.br/api)
- Nuvem Fiscal — [https://www.nuvemfiscal.com.br/](https://www.nuvemfiscal.com.br/)
