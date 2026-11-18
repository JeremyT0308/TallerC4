# Inventario C4 de TallerC4

## Nivel 1 — Contexto del sistema

TallerC4 es una aplicación web académica para reunir, en una sola experiencia, consultas de contribuyente y vehículo del SRI y una consulta de puntos de licencia de la ANT. La persona usuaria introduce correo, RUC, placa e identificación; la aplicación devuelve cada resultado con el estado y la procedencia visibles.

Actores y sistemas externos:

- **Persona usuaria:** opera el navegador y revisa resultados y advertencias.
- **SRI:** proveedor externo de existencia/detalle de contribuyente y pagos vehiculares.
- **ANT:** proveedor externo de puntos; su acceso real depende de disponibilidad/autorización del portal.
- **Redis:** almacenamiento opcional para cachear resultados ANT.

## Nivel 2 — Contenedores

| Contenedor | Responsabilidad | Tecnología | Comunicación |
| --- | --- | --- | --- |
| Frontend | Formulario guiado, validación local, estados de carga/error y procedencia | React + TypeScript + Vite | HTTP JSON hacia `/api/v1/*` |
| Backend | Casos de uso, validación de dominio, contratos REST y resiliencia | Spring Boot 3 + Java 21 | HTTP JSON, `RestClient`, Redis |
| Redis opcional | Cache de resultados ANT exitosos | Redis | Redis protocol mediante Spring Data Redis |
| SRI | Datos tributarios y pagos reportados | Servicio público externo | HTTPS REST/JSON |
| ANT | Puntos de licencia | Portal externo configurable | HTTPS; el adaptador real interpreta HTML verificable |

El backend es un monolito modular: los límites de aplicación, dominio e infraestructura están explícitos en paquetes y puertos, sin introducir una complejidad operacional de microservicios para el alcance académico.

## Nivel 3 — Componentes del backend

### Entrada y aplicación

- `TaxpayerController`, `VehicleController` y `AntController`: exponen los endpoints REST.
- `TaxpayerService`, `VehicleService` y `AntPointsService`: coordinan validación, proveedor, normalización de respuestas y política de cache.
- `TaxpayerProvider`, `VehicleProvider`, `AntProvider` y `AntCache`: puertos que aíslan los casos de uso de proveedores y almacenamiento.
- `InputValidator`: reglas compartidas para RUC estructural, correo, placa e identificación.
- `GlobalExceptionHandler`: convierte errores de validación, no encontrado e indisponibilidad a `ApiError` estable.

### Infraestructura

- `SriTaxpayerAdapter` y `SriVehicleAdapter`: implementan los puertos SRI y mapean los payloads externos a DTOs internos.
- `RealAntAdapter`: consulta la URL configurada y extrae únicamente campos que puede verificar en la respuesta.
- `SimulatedAntAdapter`: ofrece escenarios deterministas (`success`, `no-results`, `bad-gateway`, `timeout`, `invalid-response`) para desarrollo, demostración y pruebas.
- `RedisAntCache` e `InMemoryAntCache`: implementan el mismo puerto de cache; Redis se activa por configuración y memoria es el default local.

## Flujos principales

### Consulta tributaria

`Browser → TaxpayerController → TaxpayerService → TaxpayerProvider → SRI → TaxpayerDetails → Browser`

El correo es obligatorio para el contrato de entrada, aunque la búsqueda de datos la realiza SRI con el RUC. Si SRI no confirma existencia, se devuelve 404; si el proveedor falla, 503.

### Consulta vehicular

`Browser → VehicleController → VehicleService → VehicleProvider → SRI → VehicleDetails → Browser`

El adapter conserva la placa y el conteo de pagos. Marca, modelo y año son anulables porque el endpoint observado puede no entregarlos.

### Consulta ANT con fallback

`Browser → AntController → AntPointsService → AntProvider`

Si la respuesta contiene puntos, se entrega como resultado vivo y se guarda en cache. Ante una indisponibilidad del proveedor, el servicio busca el último resultado elegible: puede ser stale, pero la respuesta lo marca explícitamente con `stale`, `retrievedAt`, `staleSince` y un mensaje de advertencia. Si no existe resultado elegible, responde 503.

La política predeterminada es 15 minutos de frescura y 7 días de antigüedad máxima. Una caída de Redis no invalida un resultado vivo y tampoco se presenta como dato ANT.

## Contratos y decisiones de protocolo

- El frontend consume JSON estable definido en `docs/contracts/openapi.yaml`.
- La procedencia ANT distingue `ANT`, `ANT_SIMULATED` y `ANT_CACHE`.
- La simulación se activa por configuración (`ANT_PROVIDER=simulated`) y puede seleccionar un escenario por query param para demostración.
- Los timeouts HTTP de SRI son configurables; por defecto son 3 s de conexión y 8 s de lectura.
- No se registran credenciales ni se persisten correos en la aplicación.
