# Verificación de caché y resiliencia ANT

## Configuración y justificación

- Frescura: **15 minutos** (`ANT_CACHE_FRESH_MINUTES=15`). Durante ese intervalo, una respuesta exitosa se considera reciente; si el proveedor falla, aun así el fallback se marca `stale=true` para no presentarlo como respuesta viva.
- Retención máxima stale: **7 días** (`ANT_CACHE_MAX_STALE_DAYS=7`). Es un límite conservador para conservar continuidad operativa sin servir indefinidamente datos de movilidad posiblemente obsoletos.
- Redis: `REDIS_ENABLED=false` en esta ejecución porque no existen variables `REDIS_*` en el entorno. Se verificaron `InMemoryAntCache` y `RedisAntCache` con pruebas controladas; la conectividad Redis Cloud queda pendiente de credenciales autorizadas.
- Identificaciones usadas: valores sintéticos `0000000031`–`0000000034`, sin datos personales.

## Pruebas automatizadas

Ejecución focalizada: `mvn -q '-Dtest=AntPointsServiceTest,AntPointsCacheTest,AntCachePolicyTest,RedisAntCacheTest' test`.

Resultado: **PASS — 17 pruebas, 0 fallos**.

Se cubrieron:

- Respuesta viva exitosa y guardado solo cuando `points` no es nulo.
- Timeout, HTTP 502 y respuesta inválida con recuperación del último resultado válido.
- Cache miss con timeout, 502 y respuesta inválida.
- Cache vencida después de superar 7 días.
- Límites exactos de 15 minutos de frescura y 7 días de elegibilidad.
- Fallo de escritura/lectura Redis convertido a `CacheUnavailableException` sin perder una respuesta viva.
- Respuestas sin puntos que no contaminan el cache.

## Verificación HTTP real local

Fecha local: **2026-11-17T23:37:41-05:00**. Backend Spring Boot local, `ANT_PROVIDER=simulated`, `REDIS_ENABLED=false`.

| Escenario | HTTP | Observado |
| --- | ---: | --- |
| `0000000031`, `success` | 200 | 30 puntos, `source=ANT_SIMULATED`, `stale=false`, `retrievedAt` presente |
| `0000000031`, `timeout` | 200 | Conserva 30 puntos, `stale=true`, `retrievedAt` original, `staleSince` y nota de cache |
| `0000000031`, `bad-gateway` | 200 | Conserva el mismo resultado con `stale=true` y advertencia de fallback |
| `0000000031`, `invalid-response` | 200 | Conserva el último valor válido como stale |
| `0000000031`, `success` posterior | 200 | Recuperación: nuevo resultado vivo con `stale=false` |
| `0000000032`, `timeout` sin cache | 503 | Error estructurado `SERVICE_UNAVAILABLE` |
| `0000000033`, `bad-gateway` sin cache | 502 | Error estructurado `BAD_GATEWAY` |
| `0000000034`, `invalid-response` sin cache | 503 | Error estructurado `SERVICE_UNAVAILABLE` |

## Presentación React

La interfaz muestra badge `Datos stale`, fuente, fecha de consulta original, fecha `staleSince` cuando corresponde y una advertencia explícita indicando que ANT no respondió y que se usa el último cache elegible. Las capturas visuales se reutilizan desde:

- `docs/evidence/screenshots/03-ant-stale-after-timeout.png`
- `docs/evidence/screenshots/04-ant-recovery-after-502.png`
- `docs/evidence/screenshots/ux-results.png`

La verificación completa de esa presentación se repite en la suite frontend y en E2E de la Fase 3.

## Redis Cloud

No se inventa una conexión externa. No había `REDIS_HOST`, `REDIS_PASSWORD` ni otras variables `REDIS_*` disponibles. La guía exacta para una prueba autorizada está en [`redis-cloud-verification.md`](redis-cloud-verification.md); al ejecutarla deben registrarse solo endpoint no sensible, fecha, `PING` y resultado de cache, nunca la contraseña.
