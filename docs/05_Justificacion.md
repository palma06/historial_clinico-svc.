# ms-historialclinico — Justificación del servicio y cobertura de requisitos

**Caso caso07 — MediCare** (Telemedicina) · EP01

Este documento justifica la existencia de **ms-historialclinico** como microservicio independiente: qué requisitos del negocio cubre (funcionales, no funcionales y de seguridad), por qué está delimitado así (SRP), y qué tecnología AWS se usa para cada responsabilidad y **por qué**. Los diagramas que respaldan esta justificación están en `docs/diagramas/`.

---

## 1. Misión del servicio

ms-historialclinico administra el catálogo del dominio: publicar, mantener y permitir buscar y consultar los recursos con alta disponibilidad y baja latencia, alimentando al resto del sistema con información vigente del caso caso07 (MediCare).

> Es el servicio más leído del caso (todas las vistas lo consultan) pero de baja contención de escritura: se escala en lectura de forma independiente, con caché y CDN, sin arrastrar a los servicios transaccionales.

---

## 2. Requisitos funcionales que cubre

| RF | Requisito (de `00_PresentacionEmpresa.md`) | Qué hace ms-historialclinico al respecto | Evidencia |
|----|------------------------------------------|-------------------------------|-----------|
| **RF-04** | Registrar y consultar el historial clínico digital | Administra el catálogo del dominio: alta, edición, publicación y búsqueda con filtros, con caché de lecturas | C4-2 (contenedores) y diagrama de secuencia (búsqueda con caché) |

**Por qué estos RF justifican un servicio aparte:** Es el servicio más leído del caso (todas las vistas lo consultan) pero de baja contención de escritura: se escala en lectura de forma independiente, con caché y CDN, sin arrastrar a los servicios transaccionales.

---

## 3. Requisitos no funcionales que cubre

| RNF | Criterio | Cómo lo cumple ms-historialclinico | Decisión técnica |
|-----|----------|--------------------------|------------------|
| **RNF-03** (Escalabilidad) | Escalar citas y consultas en campañas masivas (vacunación, epidemia) | Auto scaling independiente de este servicio (3→12 tareas Fargate según carga) | ECS Fargate + alarmas de CloudWatch: solo este componente escala en el pico |
| **RNF-05** (Rendimiento) | Videollamada con latencia baja y estable; agendamiento de cita en menos de 3 segundos | Respuestas dentro del umbral exigido por el caso (ver alarmas p95) | Caché/bajo acoplamiento + CloudWatch con alarma de latencia |
| **RNF-02** (Disponibilidad) | Las citas y la videollamada deben mantenerse operativas ante fallas de pagos o notificaciones | Aislamiento por eventos: este servicio sigue operando aunque fallen los vecinos | Comunicación asíncrona (SQS/EventBridge) + multi-AZ |

**Justificación SRP (IE9):** ms-historialclinico tiene **una sola razón de cambio**: las reglas de publicación, categorización y búsqueda del catálogo. Si mañana cambia esa regla, **ningún otro servicio se modifica**.

---

## 4. Requisitos de seguridad que cubre (mapeo STRIDE)

| Amenaza | Escenario en este servicio | Contramedida |
|---------|-----------------------------|--------------|
| **S**poofing | Consumir el catálogo sin autenticación | JWT obligatorio en API Gateway; el catálogo no expone endpoints públicos sin token |
| **T**ampering | Publicar o editar recursos sin permiso | Autorización por rol (solo el rol editor publica); validación de payload con Bean Validation |
| **R**epudiation | Negar una publicación o edición | Log de auditoría de escrituras y eventos de dominio con timestamp |
| **I**nformation disclosure | Filtrar datos del catálogo de otros tenants | Cifrado at-rest (KMS) y filtrado por tenant/owner en cada consulta |
| **D**enial of service | Raspado masivo del catálogo (scraping) | Throttling en API Gateway + caché (ElastiCache) que absorbe lecturas repetidas |
| **E**levation of privilege | Llamar a endpoints de administración sin rol | Claims de rol verificados en el gateway; SG que solo acepta tráfico del ALB |

---

## 5. Stack tecnológico y por qué cada tecnología

### 5.1 Stack de la aplicación

| Tecnología | Para qué se usa en ms-historialclinico |
|------------|------------------------------|
| **Java 21 + Spring Boot 3.3** | Framework estándar de la asignatura: implementa la API REST, la lógica de negocio y el acceso a datos del servicio |
| **Spring Data JPA** | Persistencia de las entidades del dominio en la base de datos propia (repositorios por entidad) |
| **Bean Validation** | Validación de los payloads de entrada antes de procesar (jakarta.validation) |
| **springdoc-openapi** | Documentación viva del contrato REST (Swagger UI / ReDoc) para consumidores y equipo |
| **Docker + Docker Compose** | Empaquetado reproducible; la misma imagen corre en local y en ECS Fargate |
| **JUnit 5 + Mockito + MockMvc** | Pruebas unitarias y de contrato HTTP (cobertura 100 % LINE con JaCoCo) |
| **Cucumber (BDD)** | Escenarios en español alineados a los endpoints, ejecutados contra el servidor real |

### 5.2 Stack AWS y justificación de cada servicio

| Servicio AWS | Rol en ms-historialclinico | Por qué se eligió |
|--------------|----------------|--------------------|
| **Amazon Aurora Serverless** | BD del catálogo con lecturas multi-AZ | SQL relacional para filtros complejos; el caso exige búsqueda con filtros |
| **Amazon ElastiCache (Redis)** | Caché de consultas frecuentes (Cache-Aside) | Absorbe el tráfico repetido de lectura y responde en milisegundos (RNF de rendimiento) |
| **Amazon CloudFront** | CDN de assets y respuestas cacheables | El catálogo es lo más leído del caso: se sirve desde el edge |
| **Amazon API Gateway** | Entrada con JWT y throttling | Protege contra raspado masivo (STRIDE-D) y valida tokens |
| **AWS KMS** | Cifrado at-rest | Protección de datos del catálogo por tenant |
| **CloudWatch + X-Ray** | Métricas de latencia de búsqueda | Alarma si p95 supera el umbral del caso (IE8) |

### 5.3 Patrones aplicados (IE5)

| Patrón | Dónde |
|--------|-------|
| **API Gateway** | Entrada única con JWT y throttling |
| **Cache-Aside** | Caché de lecturas en ElastiCache para absorber el tráfico repetido |
| **CDN** | CloudFront sirve assets y respuestas cacheables del catálogo |

---

## 6. Delimitación: qué NO hace ms-historialclinico (IE9/IE10)

| No hace | Lo hace | Por qué |
|---------|---------|---------|
| usuarios | ms-usuarios | razones de cambio distintas: la autenticación se centraliza aquí, pero el negocio de cada dominio queda en su servicio |
| citas | ms-citas | razones de cambio distintas: la operación se orquesta aquí, pero cada colaborador es autónomo |
| consultas | ms-consultas | razones de cambio distintas: el seguimiento vive aquí, pero la operación que lo origina vive en el servicio central |
| recetas | ms-recetas | razones de cambio distintas: el seguimiento vive aquí, pero la operación que lo origina vive en el servicio central |
| pagos | ms-pagos | razones de cambio distintas: el dinero se procesa aquí, pero la operación que lo origina vive en el servicio central |

---

## 7. Diagramas que respaldan esta justificación

```
docs/diagramas/
├── c4/
│   ├── C4-1-Contexto     el servicio, sus actores y sus vecinos
│   ├── C4-2-Contenedor   la API, la BD propia y los componentes del dominio
│   └── C4-3-Componentes  validador/service, clientes, publicador, repos
├── secuencia/
│   └── Secuencia-Expediente   consulta con caché y publicación
└── infraestructura/
    └── Infra-AWS         despliegue solo de este servicio, con iconos oficiales AWS
```

