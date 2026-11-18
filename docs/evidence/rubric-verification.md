# Verificación consolidada de rúbrica

- Generado: `2026-11-17T23:46:30-05:00`.
- Rama verificada: `main`.
- Condición de ejecución: backend y frontend locales; `ANT_PROVIDER=simulated`; `REDIS_ENABLED=false`; no se imprimieron credenciales.
- No se modificaron los diagramas IcePanel ni la arquitectura C1/C2/C3.

## Criterio 2 — contratos API y errores estructurados

**Estado: PASS.**

| Requisito | Prueba exacta | Resultado/evidencia |
| --- | --- | --- |
| Rutas SRI y ANT disponibles | `GET /api/v1/taxpayers/{ruc}`, `GET /api/v1/vehicles/{plate}`, `GET /api/v1/ant/points` | PASS; [`api-http-verification.md`](api-http-verification.md) |
| Validaciones de entrada | RUC/email, placa e identificación ANT inválidos | PASS — HTTP 400, `VALIDATION_ERROR` |
| Respuesta exitosa | SRI contribuyente, SRI vehículo y ANT simulado | PASS — HTTP 200 |
| No encontrado | RUC corporativo inexistente | PASS — HTTP 404, `NOT_FOUND` |
| Falla upstream diferenciada | ANT simulado `bad-gateway` y `timeout` sin cache | PASS — HTTP 502 `BAD_GATEWAY` y HTTP 503 `SERVICE_UNAVAILABLE` |
| Forma de error estable | `code`, `message`, `status`, `timestamp`, `path` | PASS en MockMvc y ejecución HTTP |
| Contrato publicado | OpenAPI actualizado con respuestas 200/400/404/502/503 | PASS — [`openapi.yaml`](../contracts/openapi.yaml) |

La evidencia HTTP usa el backend local real y datos de prueba documentados; los escenarios ANT 502/503 son simulaciones deterministas, no respuestas atribuidas al portal ANT.

## Criterio 3 — cache y resiliencia ANT

**Estado: PASS con limitación de infraestructura externa.**

| Requisito | Prueba exacta | Resultado/evidencia |
| --- | --- | --- |
| Éxito y almacenamiento | `AntPointsServiceTest`, `AntPointsCacheTest` | PASS |
| Timeout, 502 e inválido | Simulador con cache y sin cache | PASS — fallback stale o error estructurado |
| Recuperación | `success → timeout/502/invalid-response → success` | PASS — recupera último valor y luego vuelve a fresco |
| Cache miss | Falla sin entrada elegible | PASS — HTTP 503/502 según causa |
| Cache expirado | Prueba después de `7 días + 1 segundo` | PASS — no elegible |
| Frescura | `15 minutos` exactos frescos; después stale | PASS — límites cubiertos por `AntCachePolicyTest` |
| Proveniencia | `source`, `stale`, `retrievedAt`, `staleSince` y warning visible | PASS — [`ant-resilience-verification.md`](ant-resilience-verification.md) y capturas ANT |
| Suite de resiliencia | `mvn -q '-Dtest=AntPointsServiceTest,AntPointsCacheTest,AntCachePolicyTest,RedisAntCacheTest' test` | PASS — 17 pruebas, 0 fallos |

Los 15 minutos permiten reutilizar una respuesta reciente sin presentarla como una consulta viva cuando el proveedor falla. Los 7 días limitan la continuidad operativa para evitar servir indefinidamente datos de movilidad obsoletos. Redis Cloud no pudo verificarse porque el entorno no tenía variables `REDIS_*`; sí se probaron el cache en memoria y el adapter Redis con dobles controlados, sin inventar conectividad externa.

## Criterio 4 — integración React + Java

**Estado: PASS en entorno local.**

| Requisito | Prueba exacta | Resultado/evidencia |
| --- | --- | --- |
| Consulta SRI contribuyente | E2E con RUC corporativo de prueba | PASS — [`api-sri-taxpayer.png`](screenshots/api-sri-taxpayer.png) |
| Consulta SRI vehículo | E2E con placa `PEI7954` | PASS — [`api-sri-vehicle.png`](screenshots/api-sri-vehicle.png) |
| ANT simulado | Success, timeout, 502, recuperación y sin cache | PASS — [`automated-browser-report.md`](automated-browser-report.md) |
| Loading, toast y resultados | Capturas reales durante interacción Playwright | PASS — [`frontend-verification.md`](frontend-verification.md) |
| Responsive y microinteracciones | Viewports desktop/móvil y hover real | PASS — capturas `ux-after-mobile.png` y `ux-hover.png` |
| Accesibilidad de movimiento | CSS `prefers-reduced-motion`, labels y controles navegables | PASS — frontend existente; build exitoso |
| Tests frontend | `npm test -- --run --reporter=dot` | PASS — 6 pruebas, 0 fallos |
| Build producción | `npm run build` | PASS |
| Navegador | `npm run e2e:usability` con Edge/Playwright | PASS — 6 comprobaciones |

## Usabilidad humana y limitaciones

El protocolo de tres tareas —RUC, placa y puntos ANT— y la plantilla de observación están en [`../usability/protocol.md`](../usability/protocol.md) y [`../usability/recording-template.md`](../usability/recording-template.md). La ejecución realizada es automatizada y no se presenta como estudio con participantes; resultados humanos, nombres, tiempos y observaciones siguen pendientes.

La verificación Redis Cloud queda pendiente hasta disponer de un endpoint autorizado y credenciales suministradas de forma segura. No se incluyeron contraseñas, tokens ni `BITACORA_PRIVADA.md` en Git.

## Evidencia anterior y posterior

- Antes: [`screenshots/ux-before.png`](screenshots/ux-before.png).
- Después desktop: [`screenshots/ux-after-desktop.png`](screenshots/ux-after-desktop.png).
- Después móvil: [`screenshots/ux-after-mobile.png`](screenshots/ux-after-mobile.png).
- Hover: [`screenshots/ux-hover.png`](screenshots/ux-hover.png).
- Toast: [`screenshots/ux-toast.png`](screenshots/ux-toast.png).
- Loading: [`screenshots/ux-loading.png`](screenshots/ux-loading.png).
- Resultado: [`screenshots/ux-results.png`](screenshots/ux-results.png).
