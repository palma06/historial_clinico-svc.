# Historial Clínico — Microservicio de riesgo historial-clinico

Microservicio correspondiente al **caso caso07 — MediCare** (Telemedicina) de la Evaluación Parcial N°1.

| | |
|---|---|
| Stack | Spring Boot 3.3 · Java 21 · Maven · Spring Data JPA · H2 · springdoc-openapi |
| Calidad | JaCoCo cobertura LINE 100% · Cucumber (BDD) alineado a endpoints REST |
| Entrega | Docker / Docker Compose |

## Responsabilidad (SRP)

administra los datos y la lógica del dominio de Historial Clínico del caso caso07 (MediCare). Su base de datos es una **H2 en memoria** (un solo microservicio por base), cumpliendo aislamiento de datos por dominio.

## Página de presentación

Al ejecutar el servicio, `http://localhost:8080/` muestra la página de presentación del microservicio con documentación y enlaces a:

- **Swagger UI**: `/swagger-ui/index.html`
- **OpenAPI (yaml)**: `/v3/api-docs.yaml`
- **ReDoc**: `/redoc.html`
- **H2 Console**: `/h2-console`

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/expedientes` | Lista todos los recursos |
| GET | `/api/expedientes/{id}` | Obtiene un recurso por id |
| POST | `/api/expedientes` | Crea un recurso |
| PUT | `/api/expedientes/{id}` | Actualiza un recurso |
| DELETE | `/api/expedientes/{id}` | Elimina un recurso |

## Documentación del proyecto

La documentación completa está en la carpeta [`docs/`](docs/):

- [`docs/00_Resumen.md`](docs/00_Resumen.md) — propósito, responsabilidad y tecnologías
- [`docs/01_Arquitectura.md`](docs/01_Arquitectura.md) — componentes, arquitectura y patrones
- [`docs/02_API.md`](docs/02_API.md) — contrato REST y ejemplos curl
- [`docs/03_Pruebas.md`](docs/03_Pruebas.md) — tests unitarios, cobertura y Cucumber
- [`docs/04_Despliegue.md`](docs/04_Despliegue.md)
- [`docs/05_Justificacion.md`](docs/05_Justificacion.md) — justificación del servicio: RF/RNF/seguridad cubiertos, stack y por qué cada tecnología AWS
- [`docs/diagramas/`](docs/diagramas/) — C4 (contexto, contenedores, componentes), secuencia e infraestructura AWS — Docker, Docker Compose e integración

## Cómo ejecutar locmente

```bash
mvn spring-boot:run
```

## Cómo ejecutar con Docker

```bash
docker compose up --build
# http://localhost:8080
```

## Cómo ejecutar las pruebas

```bash
mvn test      # unit tests + Cucumber
mvn verify    # + verificación de cobertura JaCoCo (100% LINE, falla si baja)
```
## Convenciones y Buenas Prácticas de Desarrollo (IE5)

Guía de estándares adoptada para el ciclo de vida del microservicio `historial-clinico-svc` bajo la metodología GitFlow.

### 1. Convención de Commits (Conventional Commits)

Los mensajes se estructuran bajo el formato: `tipo(alcance): descripcion-corta` (en minúsculas, modo imperativo y sin tildes).

| Tipo | Propósito | Ejemplo |
|---|---|---|
| `feat` | Nueva funcionalidad en el sistema | `feat(ui): agregar pie de pagina con version` |
| `fix` | Corrección de un defecto o bug | `fix(ui): corregir titulo de la pagina principal` |
| `docs` | Cambios exclusivos en documentación | `docs: agregar changelog del microservicio historial-clinico` |
| `chore` | Configuración, dependencias o tareas de CI/CD | `chore(ci): agregar pipeline de compilacion` |
| `test` | Incorporación o ajuste de pruebas unitarias/BDD | `test(api): agregar escenario cucumber para expedientes` |

### 2. Naming de Ramas

Las ramas temporales deben nombrarse en minúsculas y con palabras separadas por guiones medios:

- **Features:** `feature/<descripcion-corta>` (ejemplo: `feature/pagina-presentacion`, `feature/changelog`). Origen: `develop`.
- **Hotfixes:** `hotfix/<descripcion-corta>` (ejemplo: `hotfix/titulo-pagina`). Origen: `main`.

### 3. Flujo de Merge y Políticas de Integración

- **Prohibido push directo:** No se permite realizar commits o pushes directos a las ramas `main` ni `develop`. Toda integración debe canalizarse exclusivamente a través de un **Pull Request (PR)**.
- **Aprobación mínima:** Cada PR requiere obligatoriamente al menos **1 aprobación** del compañero de equipo antes de habilitar la fusión.
- **Estrategia de fusión:** Se utiliza *Merge commit* o *Squash and merge* según la granularidad del cambio.
- **Limpieza:** La rama de trabajo debe eliminarse inmediatamente tras completarse la fusión (`Delete branch`).

### 4. Estrategia de Revisión de Código (Code Review)

1. **Apertura:** El autor del PR asigna al compañero como revisor formal en GitHub.
2. **Revisión activa:** El revisor inspecciona la pestaña *Files changed*, comenta observaciones si corresponde y solo ejecuta la aprobación (*Approve*) si el cambio cumple los estándares de calidad.
3. **Validación previa:** Antes de solicitar revisión o fusionar, el autor debe garantizar en local la ejecución exitosa de pruebas y cobertura:
```bash
   mvn test
   mvn verify