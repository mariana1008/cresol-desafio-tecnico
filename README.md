# Desafio Técnico - Conta Corrente e Movimentação Financeira

API REST para gerenciamento de contas correntes: abertura de conta, depósito, saque, transferência entre contas e extrato.

**Stack:** Java 21 · Spring Boot 4.1 · Hibernate/JPA · PostgreSQL 16 · Flyway · Testcontainers · Docker

---

## 1. Como subir com Docker

### Pré-requisitos

- **Docker Desktop** instalado e **aberto** (o ícone da baleia deve estar estável).
- Portas **8080** (API) e **5432** (PostgreSQL) livres na máquina.

Confira a instalação:

```bash
docker --version
docker compose version
```

### Subir a aplicação

Na raiz do projeto (onde está o `docker-compose.yml`):

```bash
docker compose up --build
```
O que acontece na subida:

1. Sobe o **PostgreSQL** e aguarda o healthcheck (`pg_isready`) passar.
2. Constrói a imagem da aplicação (build multi-stage, ver [seção 4](#4-decisões-técnicas)).
3. Sobe a aplicação, que executa as **migrations do Flyway** e cria as tabelas.

### Verificar se está no ar

```bash
curl http://localhost:8080/actuator/health
```

### Derrubar tudo

Parar e remover os containers (**Ctrl + C** no terminal do `up`, depois):

```bash
docker compose down
```

### Subir do zero (reset completo)

Use quando quiser **banco limpo**, ou quando uma migration falhou no meio e deixou o banco em estado inconsistente:

```bash
# 1. Remove containers e volumes (apaga os dados do banco)
docker compose down -v

# 2. Reconstrói a imagem da aplicação sem cache
docker compose build --no-cache app

# 3. Sobe novamente
docker compose up
```

Com o banco zerado, a sequence de contas reinicia em **1000**: as primeiras contas criadas serão `1000-6`, `1001-4`, `1002-2`.

> O `-v` é o que garante a remoção dos dados. Sem ele, o volume do banco pode permanecer no Docker.

## 2. Executar sem containerizar a aplicação

```bash
docker compose up -d postgres      # só o banco
./mvnw spring-boot:run             # aplicação na JVM local (usa localhost:5432 por padrão)
```

### Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/contacorrente` | URL JDBC |
| `DB_USER` | `app` | Usuário do banco |
| `DB_PASSWORD` | `app` | Senha do banco |
| `DB_POOL_SIZE` | `10` | Tamanho máximo do pool de conexões **por instância** |

---

## 3. Como usar a API

Base URL: `http://localhost:8080`

| Operação | Método e rota | Status de sucesso |
|---|---|---|
| Abrir conta | `POST /contas` | 201 |
| Depósito | `POST /contas/deposito` | 201 |
| Saque | `POST /contas/saque` | 201 |
| Transferência | `POST /contas/transferencia` | 201 |
| Extrato | `GET /contas/{numero}/{digito}/extrato` | 200 |
| Listar contas (extra) | `GET /contas?documento=...` | 200 |
| Health check | `GET /actuator/health` | 200 |

A conta é identificada por **número + dígito verificador**. Ambos são retornados na abertura da conta.