# PediuPatrão — Central de Pedidos

Projeto individual desenvolvido para a Atividade Prática M1 de Engenharia de Software. A aplicação simula a operação de uma pizzaria, com controle de acesso por perfil, cadastro de clientes, registro de pedidos e trilha de auditoria.

## Tecnologias

- Java 21
- Maven 3.10+
- Spring Boot 3.5
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- MongoDB Community Server

## Como executar

Pré-requisitos: JDK 21, Maven e MongoDB em execução na porta `27017`.

```powershell
java --version
mvn --version
mvn spring-boot:run
```

Depois, acesse [http://localhost:8080](http://localhost:8080).

Por padrão, a aplicação usa `mongodb://localhost:27017/pediupatrao`. É possível definir outra conexão pela variável de ambiente `MONGODB_URI`.

## Usuários iniciais para demonstração

Com o banco vazio, a aplicação cria os seguintes usuários:

| Usuário | Senha | Perfil |
| --- | --- | --- |
| `admin` | `admin123` | ADMIN |
| `gerente` | `gerente123` | GERENTE |
| `atendente` | `atendente123` | ATENDENTE |

As senhas são armazenadas com BCrypt. Essas credenciais são apenas para demonstração acadêmica.

## Perfis e permissões

| Recurso | ADMIN | GERENTE | ATENDENTE |
| --- | :---: | :---: | :---: |
| Gerenciar usuários e perfis | ✓ | — | — |
| Consultar clientes | ✓ | ✓ | ✓ |
| Cadastrar e alterar clientes | — | — | ✓ |
| Registrar pedidos | — | — | ✓ |
| Atualizar status dos pedidos | — | ✓ | ✓ |
| Aplicar desconto | — | ✓ | — |
| Cancelar pedido | — | ✓ | — |
| Consultar auditoria | ✓ | ✓ | — |

As permissões são protegidas no backend com Spring Security e autorização por método.

## Regras de negócio

### Clientes

- O atendente cadastra e altera clientes.
- O telefone possui máscara no formulário e validação no servidor.
- Inclusões e alterações geram registros de auditoria.

### Pedidos

- Um pedido é associado a um cliente e pode conter vários itens.
- Preços, subtotal, desconto e total são calculados no servidor.
- A entrada registra automaticamente o responsável e a data/hora.
- Fluxo permitido:

```text
Recebido > Em preparação > Pronto > Saiu para entrega ou Retirado > Finalizado
```

- O gerente aplica desconto de até 20% somente antes da saída.
- O gerente cancela antes da saída, com justificativa obrigatória.
- Pedidos cancelados não aceitam novas alterações.

### Auditoria

- Registra criação e alteração de clientes, criação de pedidos, mudanças de status, descontos e cancelamentos.
- Cada evento guarda usuário, perfil, data/hora, entidade, valores anteriores/novos e justificativa quando aplicável.
- A consulta é somente leitura e restrita a ADMIN e GERENTE.
- A interface apresenta apenas o necessário, evitando exposição desnecessária de dados pessoais.

## Endpoints REST principais

| Método | Endpoint | Finalidade |
| --- | --- | --- |
| `GET` | `/api/clientes` | Lista clientes |
| `POST` | `/api/clientes` | Cria cliente |
| `PUT` | `/api/clientes/{id}` | Atualiza cliente |
| `GET` | `/api/produtos` | Lista produtos ativos |
| `GET` | `/api/pedidos` | Lista pedidos |
| `POST` | `/api/pedidos` | Cria pedido |
| `PATCH` | `/api/pedidos/{id}/status` | Atualiza status |
| `PATCH` | `/api/pedidos/{id}/desconto` | Aplica desconto |
| `PATCH` | `/api/pedidos/{id}/cancelamento` | Cancela pedido |
| `GET` | `/api/auditoria` | Consulta a auditoria |

## Roteiro sugerido para o vídeo

1. Entre como ADMIN e apresente a gestão de usuários.
2. Entre como ATENDENTE, cadastre/edite um cliente e registre um pedido com mais de um item.
3. Atualize o pedido de Recebido para Em preparação e Pronto.
4. Demonstre que o atendente não acessa auditoria, desconto ou cancelamento.
5. Entre como GERENTE, aplique um desconto válido e cancele outro pedido com justificativa.
6. Tente aplicar desconto acima de 20% ou cancelar um pedido que já saiu.
7. Consulte a auditoria como gerente e apresente os eventos gerados.
