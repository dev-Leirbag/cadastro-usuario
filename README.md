# Cadastro de Usuário

API de **cadastro e autenticação de usuários** (login) — microserviço responsável por gerenciar usuários, autenticar requisições e permitir que usuários autenticados interajam com o serviço de **reserva de produtos / gestão de estoque** (repositório separado).

---

## Tecnologias principais

- Java (Spring Boot)
- Spring Boot
- Spring Security (autenticação)
- JWT (JSON Web Token) para emissão/validação de token
- Spring Data JPA (persistência)
- Banco Relacional - PostgreSQL
- Docker / Docker Compose para containerização

---

## Como a autenticação funciona (resumo prático)

1. O usuário realiza **login** (endpoint de autenticação) enviando credenciais (email + senha).
2. A API valida as credenciais e retorna um **token JWT** (corpo da resposta JSON contendo o token).
3. Para usar os endpoints protegidos, **no Insomnia** você deve abrir a aba **Auth** da requisição e **colocar o token com o prefixo `Bearer`** (por exemplo: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...`).  

> Nota técnica: internamente a aplicação valida o JWT em cada requisição protegida e resolve o usuário autenticado a partir das claims do token.

---

## Endpoints principais (exemplos)

### Cadastro de usuário
- **POST** `/cadastro`  
  **Request example (JSON):**
  ```json
  {
    "nome": "Gabriel",
    "email": "gabriel@example.com",
    "senha": "123456",
    "role": "ADMIN"
  }
  ```
  **Response (201 Created) exemplo:**
  ```json
  {
    "id": 1,
    "nome": "Gabriel",
    "email": "gabriel@example.com",
    "role": "ADMIN"
  }
  ```

### Login / Autenticação
- **POST** `/login`  
  **Request (JSON):**
  ```json
  {
    "email": "gabriel@example.com",
    "senha": "123456"
  }
  ```
  **Response (200 OK) exemplo:**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
  }
  ```

### Recursos protegidos (ex. listar usuários)
- **GET** `/usuario` — exige token JWT (via Auth no Insomnia com `Bearer ...`).

---

## Integração com o repositório de Produtos (reserva de produto)

Este serviço de usuário **integra-se** com o repositório de produtos para permitir **reserva de produto** / gerenciamento de estoque com o usuário autenticado. O fluxo geral é:

1. Usuário faz login no serviço de usuário → obtém JWT.
2. Frontend (ou Postman/Insomnia) usa o JWT para chamar o serviço de produtos (repositório: `cadastro-produto`) em endpoints de reserva/gerenciamento de estoque — o serviço de produtos valida o JWT e usa a identidade do usuário nas operações de reserva. citeturn0view1

---

## Rodando com Docker (modo rápido)

1. Clone o repositório:
   ```bash
   git clone https://github.com/dev-Leirbag/cadastro-usuario.git
   cd cadastro-usuario
   ```

2. (Opcional) edite variáveis de ambiente no `docker-compose.yml` ou no `application.yml/properties` se necessário — por exemplo, credenciais do banco, chave JWT, etc. (os arquivos de configuração estão no repositório).

3. Construa a imagem e suba os containers:
   ```bash
   docker-compose build
   docker-compose up -d
   ```

4. A API ficará disponível em:
   ```
   http://localhost:9090
   ```

5. Ao iniciar via Docker, **um perfil `admin` é criado automaticamente** (seed inicial). Consulte os arquivos de configuração / scripts no repositório para ver os parâmetros (login/senha/roles) usados na criação do admin. citeturn0view0

---

## Onde procurar as configurações / seed do admin

- Procure em `src/main/resources` por arquivos como `application.properties` / `application.yml`, `data.sql`, `import.sql` ou classes de inicialização (`CommandLineRunner`/`data loader`) — é aí que normalmente aparecem usuários seeded (admin). As configurações de Docker também podem expor variáveis de ambiente no `docker-compose.yml`. citeturn0view0turn3view1

---

## Testando no Insomnia (passo a passo rápido)

1. Faça o POST de login (`/login`) com email e senha -> receba o token no JSON de resposta.
2. Em qualquer requisição que precise de autenticação, abra a aba **Auth** no Insomnia, selecione tipo `Bearer` (ou `Bearer Token`) e **cole o valor incluindo o prefixo `Bearer`** (exemplo: `Bearer eyJ...`). Você relatou que esse é seu fluxo — siga-o.  
3. Execute a requisição; a API vai validar o token e permitir o acesso se válido.

---

## Troubleshooting rápido

- **Não está vindo o admin após o container subir?** Verifique os logs do container:
  ```bash
  docker-compose logs -f
  ```
  Busque por mensagens de seed/ criação de usuário ou erros de inicialização.
- **Porta diferente ou app não sobe?** Verifique `application.properties/yml` e `docker-compose.yml` para certificar que a porta está configurada como `9090` e não está conflitando com outro serviço. citeturn0view0turn3view1

---

## Links úteis

- Repositório do serviço de produtos (integração/ reserva de produto): https://github.com/dev-Leirbag/cadastro-produto.

---

## Licença
Projeto para estudos — use livremente.

