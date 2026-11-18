# Verificación HTTP de contratos API

## Ejecución

- Fecha local: 2026-11-17T23:28:05-05:00.
- Backend: Spring Boot local en `http://127.0.0.1:8080`.
- Configuración: `ANT_PROVIDER=simulated`, `REDIS_ENABLED=false`, cache en memoria.
- SRI: adaptadores configurados contra el endpoint público definido por `SRI_BASE_URL`.
- Credenciales: no se usaron ni se imprimieron credenciales.
- Método: `Invoke-WebRequest`/`Invoke-RestMethod` de PowerShell; respuestas reales del backend local.

## Matriz observada

| Caso | Método y ruta | Resultado | Observación |
| --- | --- | --- | --- |
| ANT exitoso | `GET /api/v1/ant/points?identification=0000000099&scenario=success` | **PASS — 200** | `points=30`, `source=ANT_SIMULATED`, `stale=false`, `retrievedAt` presente |
| Validación ANT | `GET /api/v1/ant/points?identification=123&scenario=success` | **PASS — 400** | `code=VALIDATION_ERROR`, mensaje de identificación y `path` presentes |
| Gateway ANT sin cache | `GET /api/v1/ant/points?identification=0000000098&scenario=bad-gateway` | **PASS — 502** | `code=BAD_GATEWAY`, mensaje controlado, `status=502`, timestamp y path |
| Timeout ANT sin cache | `GET /api/v1/ant/points?identification=0000000097&scenario=timeout` | **PASS — 503** | `code=SERVICE_UNAVAILABLE`, mensaje controlado, timestamp y path |
| Contribuyente SRI | `GET /api/v1/taxpayers/1790016919001?email=demo@example.com` | **PASS — 200** | `ruc=1790016919001`, estado `ACTIVO`, razón social corporativa presente |
| Vehículo SRI | `GET /api/v1/vehicles/PEI7954` | **PASS — 200** | placa `PEI7954`, `paymentCount=10`, campos no entregados por SRI como `null` |
| Contribuyente inexistente | `GET /api/v1/taxpayers/1799999999999?email=demo@example.com` | **PASS — 404** | `code=NOT_FOUND`, mensaje, timestamp y path presentes |

## Forma de error

Los errores REST se entregan con `code`, `message`, `status`, `timestamp` y `path`. Se cubrieron en MockMvc y en la ejecución HTTP los casos 400, 404, 502 y 503. El frontend conserva compatibilidad porque sigue leyendo `message` y el status de la respuesta.

## Cambios que habilitan la verificación

- `AntController` valida la identificación antes de llamar al servicio.
- `BadGatewayException` distingue un upstream HTTP 502 de una indisponibilidad 503.
- `GlobalExceptionHandler` serializa 502 como `BAD_GATEWAY` con la misma forma estructurada.
- El simulador ANT conserva el fallback stale; 502 solo se expone cuando no hay cache elegible.
- `docs/contracts/openapi.yaml` declara la respuesta 502 para `/api/v1/ant/points`.

## Limitaciones

- Los casos ANT 502/503 son deterministas del simulador y no pretenden ser respuestas oficiales del portal ANT.
- La conexión Redis Cloud no fue probada porque no había variables `REDIS_*` disponibles; la verificación local usa cache en memoria.
- Las capturas visuales de los tres flujos y estados se consolidarán en la ejecución E2E documentada en `automated-browser-report.md`.
