# Hylia API

**Curso:** FIAP — 1TDSPO  
**Solução:** Hylia (Pacientes, Profissionais, Hospitais, Consultas etc.)  
**Stack:** Java 17+, Maven, Quarkus 3.x, Oracle (JDBC), Agroal (pool), CDI (@Inject), JAX‑RS (REST)

## Sumário
- Objetivo & Escopo
- Arquitetura & Pastas
- Regras de Negócio
- Banco de Dados (DDL)
- Configuração & Execução
- Endpoints (Guia rápido)
- Plano de Testes (Postman)
- Por que CDI/@Inject?
- Mapeamento com a Rubrica

---

## Objetivo & Escopo
Hylia é um projeto didático que demonstra **Arquitetura Limpa (Clean Architecture)** no Quarkus com banco **Oracle**.

Inclui:
- Entidades de domínio: **Paciente**, **Professional**, **Hospital** (e fluxo de **Consulta** com confirmação/cancelamento, além de **Notificação**, **Acesso** e **CuidadorVinculo** quando aplicável).
- Validações de domínio (CPF/CRM/Email/UF/Idade) e normalização de dados.
- Persistência **JDBC** pura (Oracle) com pool **Agroal**.
- **API REST** (JAX‑RS) com DTOs de entrada/saída, mapeadores e resources.
- **CORS** configurado para desenvolvimento local e frontends na nuvem.

Fora do escopo (nesta entrega): autenticação real (JWT/OIDC), segurança robusta, rate limiting, jobs assíncronos.

---

## Arquitetura & Pastas
**Clean Architecture**: regras de negócio em `domain`, orquestração em `application/usecase`, entrada/saída em `infrastructure` (web + persistence). Acoplamento via **interfaces** e CDI.

```
src/main/java/br/com/fiap/hylia
├─ application
│  └─ usecase
│     ├─ paciente/…                 # ex.: CadastrarPaciente
│     ├─ professional/…             # ex.: CadastrarProfessional, CriarConsultaProfessional
│     └─ consulta/…                 # ConfirmarConsultaPaciente, CancelarConsulta
├─ domain
│  ├─ model/                        # Paciente, Professional, Hospital, Consulta, …
│  ├─ repository/                   # interfaces (ports)
│  ├─ util/                         # Validators
│  └─ exceptions/                   # ValidacaoDominioException, EntidadeNaoLocalizada
├─ infrastructure
│  ├─ persistence/                  # JDBC repositories, DatabaseConnection
│  └─ web/
│     ├─ dto/…                      # DTOs (records) de entrada/saída
│     ├─ mapper/…                   # mapeia DTO <-> domínio
│     └─ resource/…                 # JAX‑RS resources (controllers)
└─ resources/
   └─ application.properties
```

**Fluxo (ex.: criar Paciente):**  
Resource → Mapper → Use Case → `PacienteRepository` (interface do domínio) → implementação JDBC (infra) → Oracle → retorna entidade de domínio → mapeada para DTO de saída.

---

## Regras de Negócio (resumo)
**Paciente**
- CPF válido; nome normalizado; idade 0..120.
- Atualizações consistentes (evitar idades incoerentes).

**Professional**
- CRM válido (ex.: `CRM-1234`), identidade por CRM (`equals/hashCode`).
- CPF válido; email válido; idade ≥ 23; especialidade normalizada.

**Hospital**
- UF com 2 letras (armazenada em maiúsculas); email válido; nome/cidade normalizados.

**Exceções**
- `ValidacaoDominioException`: violações de regra de negócio.
- `EntidadeNaoLocalizada`: repositório não encontrou registro.
- Erros SQL: tratados na borda de infraestrutura (JDBC).

---

## Banco de Dados (DDL)
> O projeto usa a DDL oficial da disciplina (tabelas como `T_HC_USUARIO`, `T_HC_PROFISSIONAIS`, `T_HC_CONSULTA`, etc.).  
> Colunas `IDENTITY` no Oracle geram IDs crescentes — o primeiro ID **não** volta a 1 após deleções.

---

## Configuração & Execução

### Requisitos
- Java **17+**, Maven **3.9+**
- Acesso ao banco **Oracle**
- IDE sugerida: IntelliJ IDEA

### Dependências principais (Maven)
- `io.quarkus:quarkus-agroal` (datasource/pool)
- `io.quarkus:quarkus-jdbc-oracle` (driver Oracle)
- `io.quarkus:quarkus-arc` (CDI)
- `io.quarkus:quarkus-rest` + `quarkus-rest-jackson` (JAX‑RS + JSON)

### `application.properties`
Perfil **padrão (produção‑like)** usa variáveis de ambiente; perfil **dev** traz credenciais FIAP **para execução local/avaliação**.

```properties
# ===== default (prod-like) =====
quarkus.datasource.db-kind=oracle
quarkus.datasource.jdbc.url=${DB_URL}
quarkus.datasource.username=${DB_USER}
quarkus.datasource.password=${DB_PASS}

# CORS
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:5173
quarkus.http.cors.origin-patterns=https://.*\.vercel\.app,https://.*\.onrender\.com
quarkus.http.cors.methods=GET,POST,PATCH,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=Origin,Accept,Content-Type,Authorization
quarkus.http.cors.exposed-headers=Location

# ===== dev (execução local do professor) =====
%dev.quarkus.datasource.devservices.enabled=false
%dev.quarkus.datasource.jdbc.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL   # ou //host:1521/ORCL (service)
%dev.quarkus.datasource.username=RM561408
%dev.quarkus.datasource.password=171192
```

**Como executar (dev):**
```bash
mvn quarkus:dev
```
Quarkus usa o perfil `%dev` automaticamente.

**Empacotar e rodar (opcional):**
```bash
./mvnw -DskipTests package
java -Dquarkus.profile=dev -jar target/quarkus-app/quarkus-run.jar
```

---

## Endpoints (Guia rápido)

### Pacientes
- `POST https://hylia-api-service.onrender.com/api/pacientes` — criar
- `GET  https://hylia-api-service.onrender.com/api/pacientes` — listar
- `GET  https://hylia-api-service.onrender.com/api/pacientes/{cpf}` — buscar por CPF
- *(opcionais)* `PATCH /api/pacientes/{cpf}`, `DELETE /api/pacientes/{cpf}`

### Professionals
- `POST https://hylia-api-service.onrender.com/api/professionals` — criar
- `GET  https://hylia-api-service.onrender.com/api/professionals` — listar
- `GET  https://hylia-api-service.onrender.com/api/professionals/{crm}` — buscar por CRM
- *(opcionais)* `PATCH /api/professionals/{crm}`, `DELETE /api/professionals/{crm}`

### Hospitais
- `POST https://hylia-api-service.onrender.com/api/hospitais` — criar
- `GET  https://hylia-api-service.onrender.com/api/hospitais` — listar
- `GET  https://hylia-api-service.onrender.com/api/hospitais/{email}` — buscar por email
- *(opcionais)* `PATCH /api/hospitais/{email}`, `DELETE /api/hospitais/{email}`

### Consultas (fluxos de Profissional e Paciente)
- **Agenda do Profissional**
  - `POST https://hylia-api-service.onrender.com/api/professionals/{crm}/consultas` — cria consulta (`idPaciente`, `dtHoraIso`, `local`)
  - `GET  https://hylia-api-service.onrender.com/api/professionals/{crm}/consultas` — lista consultas do profissional
- **Ações do Paciente**
  - `GET  https://hylia-api-service.onrender.com/api/pacientes/{idPaciente}/consultas` — lista consultas do paciente
  - `POST https://hylia-api-service.onrender.com/api/pacientes/{idPaciente}/consultas/{idConsulta}/confirmar` — confirma (`{ "canal": "WEB" }` opcional)
  - `POST https://hylia-api-service.onrender.com/api/pacientes/{idPaciente}/consultas/{idConsulta}/cancelar` — cancela (`{ "motivo": "...", "canceladoPor": "PACIENTE" }` opcional)

---

## Plano de Testes (Postman)
> Use CPFs **válidos** (algoritmo), por exemplo: `11144477735`, `93541134780`.  
> Ajuste CRM/EMAIL para valores únicos quando necessário.

1) **Criar Professional**
```http
POST https://hylia-api-service.onrender.com/api/professionals
Content-Type: application/json
```
```json
{
  "cpf": "11144477735",
  "nome": "Dr. João",
  "idade": 35,
  "email": "joao@clinica.com",
  "especialidade": "Cardiologia",
  "crm": "CRM-1234"
}
```
→ **201 Created** com corpo (inclui `id`).

2) **Criar Paciente**
```http
POST https://hylia-api-service.onrender.com/api/pacientes
Content-Type: application/json
```
```json
{
  "cpf": "93541134780",
  "nome": "Maria da Silva",
  "idade": 28
}
```
→ **201 Created** com `id` (IDs podem ser altos por causa do identity do Oracle).

3) **Listar Professionals e Pacientes**
- `GET https://hylia-api-service.onrender.com/api/professionals` → 200 + array  
- `GET https://hylia-api-service.onrender.com/api/pacientes` → 200 + array

4) **Criar Consulta (Professional)**
```http
POST https://hylia-api-service.onrender.com/api/professionals/CRM-1234/consultas
Content-Type: application/json
```
```json
{
  "idPaciente": <ID_PACIENTE_DO_PASSO_2>,
  "dtHoraIso": "2025-12-01T14:00:00",
  "local": "Sala 3"
}
```
→ **201 Created** + dados da consulta (inclui `id`).

5) **Listar Consultas do Paciente**
- `GET https://hylia-api-service.onrender.com/api/pacientes/<ID_PACIENTE>/consultas` → deve exibir a consulta criada.

6) **Confirmar Consulta**
```http
POST https://hylia-api-service.onrender.com/api/pacientes/<ID_PACIENTE>/consultas/<ID_CONSULTA>/confirmar
Content-Type: application/json
```
```json
{ "canal": "WEB" }
```
→ **204 No Content**.

7) **Cancelar Consulta**
```http
POST https://hylia-api-service.onrender.com/api/pacientes/<ID_PACIENTE>/consultas/<ID_CONSULTA>/cancelar
Content-Type: application/json
```
```json
{ "motivo": "Indisponível", "canceladoPor": "PACIENTE" }
```
→ **204 No Content**.

> **Dica:** se receber **ORA‑00001** (violação de unique), troque CPF/CRM/EMAIL por valores inéditos.

---

## Por que CDI / @Inject?
- **Inversão de dependência:** o domínio depende de **interfaces**; a infraestrutura fornece **implementações**.
- **Wiring pelo container:** Quarkus/Arc cria beans `@ApplicationScoped` e injeta com `@Inject` → menos boilerplate e fronteiras claras.
- **Testabilidade & troca:** fácil substituir JDBC por JPA ou por mocks sem tocar o domínio.

---
