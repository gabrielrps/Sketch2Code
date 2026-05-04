# Sketch2Code

API REST que converte esboços e wireframes desenhados à mão em código HTML5 + Bootstrap 5 usando modelos de IA rodando localmente via Ollama.

## Como funciona

O pipeline tem duas etapas:

1. **Visão** — o modelo LLaVA recebe a imagem do esboço e gera uma descrição detalhada dos elementos, posições e cores
2. **Código** — o modelo Llama3 recebe essa descrição e gera o HTML fiel a ela, sem inventar nada além do que foi descrito

## Exemplo

### Esboço enviado

&nbsp;

&nbsp;

### Resultado gerado

&nbsp;

&nbsp;

## Requisitos

- Java 21+
- Maven
- [Ollama](https://ollama.com) rodando com os modelos:
  - `llava` (visão)
  - `llama3` (geração de código)

```bash
ollama pull llava
ollama pull llama3
```

## Configuração

Edite `src/main/resources/application.yaml`:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434  # URL do seu Ollama

sketch2code:
  vision:
    model: llava
    temperature: 0.2
  code:
    model: llama3
    temperature: 0.1
```

## Como rodar

```bash
./mvnw spring-boot:run
```

## Uso da API

**Endpoint:** `POST /api/sketch/generate`

**Body:** `multipart/form-data` com o campo `file` contendo a imagem (JPEG ou PNG, máx. 10MB)

### Exemplo com curl

```bash
curl -X POST http://localhost:8080/api/sketch/generate \
  -F "file=@meu-esboco.png"
```

A resposta é o HTML gerado diretamente, começando com `<!DOCTYPE html>`.

### Erros possíveis

| Status | Motivo |
|--------|--------|
| `400`  | Arquivo vazio ou formato inválido (apenas JPEG e PNG) |
| `500`  | Falha na comunicação com o Ollama ou erro interno |

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Framework | Spring Boot 3.5.14 |
| Linguagem | Java 21 |
| IA | Spring AI 1.1.5 |
| Modelos | Ollama (LLaVA + Llama3) |
| Build | Maven |

## Estrutura do projeto

```
src/main/java/it/skecto2code/
├── Sketch2CodeApplication.java
├── config/
│   └── AsyncConfig.java          # Thread pool para chamadas assíncronas
├── controller/
│   ├── SketchController.java     # Endpoint POST /api/sketch/generate
│   └── GlobalExceptionHandler.java  # Tratamento global de erros
└── service/
    └── SketchToCodeService.java  # Pipeline visão → código
```
