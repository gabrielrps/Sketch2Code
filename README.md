# Sketch2Code

A REST API that converts hand-drawn sketches and wireframes into HTML5 + Bootstrap 5 code using local AI models via Ollama.

## How it works

The pipeline runs in two stages:

1. **Vision** — the LLaVA model receives the sketch image and generates a structured description of every visible UI element, its position, label, and color
2. **Code** — the Qwen2.5-Coder model receives that description and generates HTML faithful to it, without inventing anything beyond what was described

## Example

### Input sketch

&nbsp;

&nbsp;

### Generated result

&nbsp;

&nbsp;

## Requirements

- Java 21+
- Maven
- [Ollama](https://ollama.com) running with the following models:

```bash
ollama pull llava
ollama pull qwen2.5-coder:3b
```

> **Note:** To free VRAM between runs, run `ollama stop <model-name>` or set `OLLAMA_KEEP_ALIVE=0` in your Ollama container environment.

## Configuration

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434  # your Ollama URL

sketch2code:
  vision:
    model: llava
    temperature: 0.0
  code:
    model: qwen2.5-coder:3b
    temperature: 0.0
```

`temperature: 0.0` keeps the output nearly deterministic — the model always picks the highest probability token, so the same sketch consistently produces the same result.

## Running

```bash
./mvnw spring-boot:run
```

## API

**Endpoint:** `POST /api/sketch/generate`

**Body:** `multipart/form-data` with a `file` field containing the image (JPEG or PNG, max 10MB)

### Example with curl

```bash
curl -X POST http://localhost:8080/api/sketch/generate \
  -F "file=@my-sketch.png"
```

The response is the generated HTML, starting directly with `<!DOCTYPE html>`.

### Error responses

| Status | Reason |
|--------|--------|
| `400` | Empty file or unsupported format (only JPEG and PNG accepted) |
| `500` | Ollama communication failure or internal error |

## Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.5.14 |
| Language | Java 21 |
| AI integration | Spring AI 1.1.5 |
| Vision model | LLaVA (via Ollama) |
| Code model | Qwen2.5-Coder 3B (via Ollama) |
| Build | Maven |

## Project structure

```
src/main/java/it/skecto2code/
├── Sketch2CodeApplication.java
├── config/
│   └── AsyncConfig.java              # Async thread pool configuration
├── controller/
│   ├── SketchController.java         # POST /api/sketch/generate
│   └── GlobalExceptionHandler.java   # Global error handling
└── service/
    └── SketchToCodeService.java      # Vision → code pipeline
```
