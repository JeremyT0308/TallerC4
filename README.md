# TallerC4

Aplicación web académica para consultar información tributaria y vehicular del Ecuador con SRI y ANT, con un frontend React y un backend Java modular.

## Estado

Entrega funcional del flujo SRI → vehículo → ANT. Los contratos SRI usados por el backend fueron verificados contra los servicios REST públicos observados el 17 de noviembre de 2026. ANT se mantiene detrás de un puerto porque las verificaciones automatizadas del portal público de consulta de citaciones/puntos devolvieron respuestas no utilizables (`401 Unauthorized` y, en la reconsulta más reciente, `502 Bad Gateway`); el modo simulado es explícito y visible. La interfaz y los contratos no inventan datos cuando un proveedor no está disponible.

## Estructura

- `backend/`: Spring Boot, puertos/adaptadores, REST y pruebas.
- `frontend/`: React + Vite + TypeScript.
- `docs/contracts/`: OpenAPI y contratos de proveedores.
- `docs/architecture/`: inventario C4 textual y decisiones.
- `docs/usability/`: protocolo de prueba.
- `docs/evidence/`: checklist de capturas reales.

## Requisitos

- Java 21 y Maven 3.9+.
- Node.js 20+ y npm 10+.
- Redis opcional para probar cache real; las pruebas usan dobles controlados.

## Inicio local

```powershell
cd backend
mvn spring-boot:run
```

El backend inicia por defecto con ANT simulado y cache en memoria. `.env.example` es una plantilla de referencia; Spring Boot no carga `.env` automáticamente. Para cambiar valores en PowerShell, define las variables antes de iniciar:

```powershell
$env:ANT_PROVIDER = "simulated"
$env:ANT_SIMULATION_SCENARIO = "success"
$env:REDIS_ENABLED = "false"
mvn spring-boot:run
```

En otra terminal:

```powershell
cd frontend
npm install
npm run dev
```

La UI queda en `http://localhost:5173` y el API en `http://localhost:8080`.

## Verificación

```powershell
cd backend; mvn test
cd ..\frontend; npm test -- --run
npm run build
```

Para probar fallas ANT sin depender del portal externo, deja `ANT_PROVIDER=simulated` y cambia `ANT_SIMULATION_SCENARIO` entre `success`, `no-results`, `bad-gateway`, `timeout` e `invalid-response`. Para probar el adapter real, usa `ANT_PROVIDER=real` y una URL ANT autorizada/configurada; el frontend no cambia.

## Configuración principal

| Variable | Default | Uso |
| --- | --- | --- |
| `SRI_BASE_URL` | `https://srienlinea.sri.gob.ec` | Base de los adapters SRI |
| `ANT_PROVIDER` | `simulated` | `simulated` o `real` |
| `ANT_SIMULATION_SCENARIO` | `success` | Caso determinista del simulador |
| `ANT_REAL_URL` | Portal ANT público | URL del adapter real |
| `REDIS_ENABLED` | `false` | Activa cache Redis; memoria es el default |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_DATABASE` | `localhost`, `6379`, `0` | Endpoint Redis |
| `REDIS_PASSWORD` | vacío | Credencial local; nunca subirla al repositorio |
| `REDIS_SSL_ENABLED` | `false` | `true` para Redis Cloud/TLS |
| `REDIS_CONNECT_TIMEOUT_MS`, `REDIS_READ_TIMEOUT_MS` | `1000`, `1000` | Timeouts Redis |
| `ANT_CACHE_FRESH_MINUTES` | `15` | Ventana fresca |
| `ANT_CACHE_MAX_STALE_DAYS` | `7` | Antigüedad máxima de fallback |

El archivo `.env.example` contiene la configuración completa y no incluye secretos.

## Endpoints

- `GET /api/v1/taxpayers/{ruc}?email=...`
- `GET /api/v1/vehicles/{plate}`
- `GET /api/v1/ant/points?identification=...&scenario=...`

El contrato detallado está en [`docs/contracts/openapi.yaml`](docs/contracts/openapi.yaml). El inventario C4, las decisiones, el protocolo de usabilidad, la verificación Redis y el checklist de evidencia están en `docs/architecture/`, `docs/usability/` y `docs/evidence/`.

La guía para conectar Redis Cloud sin exponer credenciales está en [`docs/evidence/redis-cloud-verification.md`](docs/evidence/redis-cloud-verification.md).

