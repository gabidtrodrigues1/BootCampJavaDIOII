# API de Orçamento Inteligente

Projeto desenvolvido para o Desafio de Projeto de Spring Boot + Spring AI da DIO.

A aplicação recebe comandos financeiros por áudio, transcreve o conteúdo para texto, usa IA para interpretar a intenção, executa funções reais da aplicação por meio de Tool Calling, registra ou consulta transações no banco e devolve uma resposta em áudio.

Todo o código foi escrito em português: pacotes, classes, métodos, variáveis, mensagens, JSONs e prompt do sistema.

## O que o projeto faz

- Registra transações financeiras via JSON.
- Lista todas as transações.
- Lista transações por categoria.
- Calcula resumo financeiro com total geral, quantidade de transações e totais por categoria.
- Recebe um arquivo de áudio em português.
- Transcreve o áudio com o modelo Whisper da OpenAI.
- Interpreta o comando com um `ChatClient`.
- Executa casos de uso reais por meio de ferramentas (`@Tool`).
- Sintetiza a resposta final em MP3 com o modelo de text-to-speech da OpenAI.
- Valida os dados e devolve erros HTTP padronizados.

## Melhoria implementada

A evolução principal escolhida foi **ampliar as consultas financeiras**:

1. Novo endpoint `GET /transacoes` para listar todas as transações.
2. Novo endpoint `GET /transacoes/resumo` para consultar:
   - total geral gasto;
   - quantidade de transações;
   - total e quantidade por categoria.
3. Duas novas ferramentas para a IA:
   - `listar_todas_transacoes`;
   - `consultar_resumo_financeiro`.
4. Como melhorias de apoio:
   - validação de descrição, valor e categoria;
   - tratamento global de erros;
   - adaptador de IA separado do controller;
   - testes automatizados dos fluxos principais;
   - Dockerfile e execução completa via Docker Compose.

Com isso, o assistente consegue responder perguntas como:

- "Quanto eu gastei no total?"
- "Qual foi o total gasto em mercado?"
- "Liste todas as minhas transações."
- "Quanto gastei em farmácia?"

## Arquitetura

O projeto mantém a separação em camadas do projeto base:

```text
br.com.dio.orcamento
├── dominio
│   ├── Categoria
│   ├── Transacao
│   ├── IdentificadorTransacao
│   ├── RepositorioTransacao
│   └── TransacaoInvalidaException
├── aplicacao
│   ├── casos de uso
│   ├── entrada e saída
│   └── porta AssistenteFinanceiro
└── infraestrutura
    ├── http
    ├── ia
    └── persistencia
```

Responsabilidades:

- **domínio:** regras e validações da transação, contrato do repositório.
- **aplicação:** casos de uso usados tanto pelo REST quanto pelo Tool Calling.
- **infraestrutura:** controller, JPA, MySQL e integração com OpenAI.

O `ChatClient` não é montado no controller. Ele fica em `AssistenteFinanceiroOpenAI`, que implementa a porta `AssistenteFinanceiro`. Assim, o controller depende de uma abstração da aplicação e não de detalhes da OpenAI.

## Fluxo principal

### Fluxo REST

```text
JSON -> ControladorTransacoes -> Caso de uso -> RepositorioTransacao -> MySQL -> JSON
```

### Fluxo com IA

```text
áudio -> TranscriptionModel (Whisper)
      -> texto
      -> ChatClient com as ferramentas registradas
      -> caso de uso escolhido pela IA
      -> banco de dados
      -> resposta em texto
      -> TextToSpeechModel
      -> áudio MP3
```

Ferramentas disponíveis para a IA:

| Ferramenta | Função |
|---|---|
| `registrar_transacao` | Registra uma nova transação |
| `listar_todas_transacoes` | Lista todas as transações |
| `listar_transacoes_por_categoria` | Lista transações de uma categoria |
| `consultar_resumo_financeiro` | Calcula totais e quantidade por categoria |

## Tecnologias

- Java 21
- Spring Boot 4.1.2
- Spring AI 2.0.2
- Spring Web MVC
- Spring Data JPA
- Hibernate
- MySQL 9.6
- H2 para testes
- OpenAI: chat, Whisper e text-to-speech
- Maven Wrapper
- JUnit 6, Mockito, AssertJ e MockMvc
- Docker e Docker Compose

## Estrutura do projeto

```text
.
├── compose.yml
├── Dockerfile
├── pom.xml
├── mvnw
├── docs/
│   └── requisicoes.http
├── src/main/java/br/com/dio/orcamento/
│   ├── AplicacaoOrcamento.java
│   ├── dominio/
│   ├── aplicacao/
│   └── infraestrutura/
├── src/main/resources/
│   ├── application.properties
│   └── prompts/mensagem-sistema.st
└── src/test/
    ├── java/
    └── resources/application-test.properties
```

## Pré-requisitos

- Java 21
- Docker
- Docker Compose
- Uma chave da OpenAI para testar o fluxo de áudio

O projeto inclui o Maven Wrapper, então não é necessário ter Maven instalado.

## Como executar

### 1. Configurar a chave da OpenAI

Crie um arquivo `.env` na raiz do projeto a partir do exemplo:

```bash
cp .env.example .env
```

Edite o `.env`:

```properties
OPENAI_API_KEY=sua-chave-da-openai-aqui
```

Também é possível exportar a variável no terminal:

```bash
export OPENAI_API_KEY="sua-chave-da-openai-aqui"
```

O arquivo `.env` está no `.gitignore` e não deve ser versionado.

### 2. Subir o MySQL

```bash
docker compose up -d banco
```

O banco fica disponível em `localhost:3307`, com:

- banco: `orcamento`
- usuário: `app`
- senha: `app`

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A API fica disponível em `http://localhost:8080`.

### Execução completa com Docker

Para subir banco e API juntos:

```bash
docker compose --profile completo up --build
```

O perfil `completo` usa a variável `OPENAI_API_KEY` informada no ambiente ou no arquivo `.env`.

### Executar sem Java instalado no host

Se você não tiver Java/Maven instalados, use a imagem Maven:

```bash
docker run --rm -v "$PWD":/app -w /app \
  maven:3.9-eclipse-temurin-21 mvn test
```

Para subir a aplicação a partir do jar gerado:

```bash
docker run --rm --network host -v "$PWD":/app -w /app \
  maven:3.9-eclipse-temurin-21 \
  java -jar target/orcamento-api-0.0.1-SNAPSHOT.jar
```

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/transacoes` | Cria uma transação |
| GET | `/transacoes` | Lista todas as transações |
| GET | `/transacoes/categoria/{categoria}` | Lista transações por categoria |
| GET | `/transacoes/resumo` | Consulta o resumo financeiro |
| POST | `/transacoes/ia` | Processa áudio e devolve MP3 |

Categorias disponíveis:

```text
MERCADO
FARMACIA
AUTOMOVEL
RESTAURANTE
LAZER
OUTROS
```

### Criar transação

```bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Compra no mercado",
    "valorEmCentavos": 15000,
    "categoria": "MERCADO"
  }'
```

Resposta:

```json
{
  "id": "a2b15f01-f6f2-4f29-a076-f0c0c4035537",
  "descricao": "Compra no mercado",
  "categoria": "MERCADO",
  "valor": 150.00,
  "dataHora": "2026-11-03T13:10:47.762572095Z"
}
```

### Listar todas as transações

```bash
curl http://localhost:8080/transacoes
```

### Listar por categoria

```bash
curl http://localhost:8080/transacoes/categoria/MERCADO
```

### Consultar resumo financeiro

```bash
curl http://localhost:8080/transacoes/resumo
```

Exemplo de resposta:

```json
{
  "totalGeral": 175.00,
  "quantidadeTransacoes": 2,
  "porCategoria": [
    {
      "categoria": "MERCADO",
      "total": 150.00,
      "quantidade": 1
    },
    {
      "categoria": "FARMACIA",
      "total": 25.00,
      "quantidade": 1
    }
  ]
}
```

### Processar áudio com IA

```bash
curl -X POST http://localhost:8080/transacoes/ia \
  -F "file=@caminho/para/audio.m4a" \
  -o resposta.mp3
```

O arquivo `resposta.mp3` contém a resposta falada do assistente.

Exemplos de comandos que podem ser enviados no áudio:

- "Gastei 50 reais no mercado."
- "Paguei 120 reais de combustível."
- "Quanto eu gastei no total?"
- "Liste todas as minhas transações."
- "Quanto gastei em farmácia?"

### Exemplo de erro de validação

```bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "",
    "valorEmCentavos": 0,
    "categoria": "MERCADO"
  }'
```

Resposta `400`:

```json
{
  "dataHora": "2026-11-03T13:10:47.916674193Z",
  "status": 400,
  "erro": "Dados da requisição inválidos",
  "detalhes": [
    "descricao: A descrição é obrigatória",
    "valorEmCentavos: O valor em centavos deve ser maior que zero"
  ],
  "caminho": "/transacoes"
}
```

## Como testar

### Testes automatizados

```bash
./mvnw test
```

Os testes usam H2 e mocks, portanto **não precisam de MySQL nem de chave da OpenAI**.

Cobertura atual:

- validações do domínio;
- registro de transação;
- listagem de todas as transações;
- listagem por categoria;
- cálculo do resumo financeiro;
- endpoints REST com `MockMvc`;
- upload de áudio com o assistente mockado;
- persistência JPA com H2;
- carregamento do contexto Spring.

Resultado da execução neste ambiente:

```text
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Teste manual do fluxo principal

1. Suba o MySQL.
2. Exporte `OPENAI_API_KEY`.
3. Inicie a aplicação.
4. Crie algumas transações via JSON.
5. Consulte `/transacoes/resumo`.
6. Envie um áudio para `/transacoes/ia` e salve o MP3 retornado.
7. Verifique no resumo se a transação falada foi registrada.

Exemplo de sequência:

```bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -d '{"descricao":"Mercado","valorEmCentavos":15000,"categoria":"MERCADO"}'

curl http://localhost:8080/transacoes/resumo

curl -X POST http://localhost:8080/transacoes/ia \
  -F "file=@audio.m4a" \
  -o resposta.mp3
```

## Decisões de projeto

- **Valores em centavos no domínio:** evita erros de arredondamento com `double`.
- **`BigDecimal` nas respostas:** os valores são convertidos para reais na saída.
- **Porta `AssistenteFinanceiro`:** o controller não conhece `ChatClient`, Whisper ou TTS.
- **Um caso de uso por intenção:** as mesmas regras são usadas pelo REST e pela IA.
- **Prompt em arquivo separado:** fica fácil ajustar o comportamento do assistente.
- **H2 nos testes:** os testes rodam sem depender de Docker ou internet.
- **Sem Lombok:** o código fica explícito e sem processamento adicional de anotações.
- **Erros padronizados:** o cliente recebe status, mensagem, detalhes e caminho.

## O que foi aprendido

- Como integrar Spring AI em uma aplicação Spring Boot real.
- Como funciona o fluxo de transcrição, chat e síntese de voz.
- Como registrar ferramentas com `@Tool` e permitir que a IA execute casos de uso.
- A importância de manter IA e regras de negócio separadas.
- Como testar o fluxo sem consumir créditos da OpenAI.
- Como usar H2 para testes e MySQL para execução local.
- Como tratar validações e erros de forma centralizada.
- Como organizar um projeto em camadas de domínio, aplicação e infraestrutura.

## Limitações e próximos passos

- O fluxo de áudio real precisa de uma chave válida da OpenAI.
- O projeto usa `ddl-auto=update`; em produção, o ideal é usar Flyway ou Liquibase.
- Não há autenticação nem controle de usuários.
- As listagens não são paginadas.
- Próximas evoluções possíveis:
  - resumo por período;
  - filtros por data;
  - exclusão e edição de transações;
  - auditoria das interações com a IA;
  - testes de integração com a OpenAI usando variável de ambiente;
  - documentação OpenAPI/Swagger.

## Referências

- Repositório da trilha: https://github.com/digitalinnovationone/dio-spring-boot-learning-track
- Projeto base Spring AI: https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/05-spring-ai/README.md
- Documentação Spring AI: https://docs.spring.io/spring-ai/reference/index.html
