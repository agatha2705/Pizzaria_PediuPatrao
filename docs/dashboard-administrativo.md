# Dashboard Administrativo

## Objetivo

Evoluir a Visão geral do PediuPatrão para que ADMIN e GERENTE acompanhem vendas, situação dos pedidos e produtos mais vendidos usando dados fornecidos por uma API REST integrada ao MongoDB.

## Requisitos funcionais

| Código | Requisito | Implementação |
| --- | --- | --- |
| RF01 | Indicadores gerais de vendas | Faturamento, pedidos finalizados/recebidos, ticket médio, descontos e cancelamentos. |
| RF02 | Análise de vendas | Gráfico de faturamento diário no período filtrado. |
| RF03 | Situação dos pedidos | Quantidade de pedidos por status. |
| RF04 | Produtos mais vendidos | Ranking por quantidade e faturamento. |
| RF05 | Filtros | Data inicial, data final, hoje, últimos 7 dias e mês atual. |
| RF06 | Integração com API | A página usa `fetch` para consumir os endpoints `/api/dashboard`. |
| RF07 | Atualização e dados | Carregamento, vazio, erro e atualização manual. |

## Requisitos não funcionais

- Acesso ao dashboard e à API somente para ADMIN e GERENTE.
- Dados provenientes do MongoDB; valores não são calculados no navegador.
- Período validado no servidor: data inicial não pode ser posterior à final e o máximo é de 366 dias.
- Interface responsiva e sem dependências externas de gráficos.
- Pedidos cancelados não compõem faturamento nem ranking de produtos.
- Faturamento e ticket médio consideram somente pedidos finalizados.

## Endpoints

Todos exigem autenticação com perfil ADMIN ou GERENTE. Os parâmetros de data usam o padrão ISO `AAAA-MM-DD`. Quando não informados, o período padrão é os últimos 7 dias.

### Resumo

`GET /api/dashboard/resumo?inicio=2026-11-01&fim=2026-11-10`

```json
{
  "faturamento": 420.00,
  "pedidosFinalizados": 8,
  "pedidosRecebidos": 12,
  "ticketMedio": 52.50,
  "descontosConcedidos": 18.00,
  "cancelamentos": 1
}
```

### Vendas por dia

`GET /api/dashboard/vendas-por-dia?inicio=2026-11-01&fim=2026-11-10`

```json
[
  { "data": "2026-11-01", "faturamento": 120.00, "pedidosFinalizados": 2 },
  { "data": "2026-11-02", "faturamento": 300.00, "pedidosFinalizados": 6 }
]
```

### Pedidos por status

`GET /api/dashboard/pedidos-por-status?inicio=2026-11-01&fim=2026-11-10`

```json
[
  { "status": "RECEBIDO", "rotulo": "Recebido", "quantidade": 2 },
  { "status": "FINALIZADO", "rotulo": "Finalizado", "quantidade": 8 }
]
```

### Produtos mais vendidos

`GET /api/dashboard/produtos-mais-vendidos?inicio=2026-11-01&fim=2026-11-10&limite=5`

```json
[
  { "produto": "Pizza Mussarela", "quantidadeVendida": 10, "faturamento": 420.00 },
  { "produto": "Refrigerante 2L", "quantidadeVendida": 6, "faturamento": 72.00 }
]
```

`limite` é opcional, tem valor padrão `5` e aceita valores entre `1` e `10`.

## Erros esperados

| Situação | Status | Exemplo |
| --- | --- | --- |
| Usuário não autenticado | 401 | Acesso negado pelo Spring Security. |
| Perfil sem permissão | 403 | ATENDENTE não pode consultar a API do dashboard. |
| Período inválido | 400 | `A data inicial deve ser anterior ou igual à data final`. |
| Limite inválido | 400 | `O limite deve estar entre 1 e 10`. |

Exemplo de resposta de erro:

```json
{
  "dataHora": "2026-11-10T15:40:00",
  "status": 400,
  "mensagem": "A data inicial deve ser anterior ou igual à data final"
}
```

## Evidências de testes

Execute:

```powershell
mvn test
```

Os testes automatizados incluem cálculo do resumo do dashboard, rejeição de período inválido, proteção do último administrador, quantidades inválidas e mascaramento de dados pessoais na auditoria.

Para a evidência visual, acessar a Visão geral como ADMIN ou GERENTE, aplicar os filtros e registrar capturas da tela e das respostas dos endpoints.
