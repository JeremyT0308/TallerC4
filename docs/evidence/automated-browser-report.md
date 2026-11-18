# Reporte de verificación automatizada de usabilidad

- Generado: 2026-11-18T03:24:54.509Z
- URL: http://127.0.0.1:5173/
- Condición: backend local con `ANT_PROVIDER=simulated`, cache en memoria y sin credenciales Redis.
- Datos de formulario: correo sintético; RUC corporativo y placa de prueba usados únicamente contra los endpoints SRI del entorno local.
- Esta ejecución automatizada no equivale a una prueba con participantes humanos.

## Resultados

- **PASS** — Tarea 1 — consulta RUC en SRI
- **PASS** — Tarea 2 — consulta de placa en SRI
- **PASS** — Tarea 3 — consulta ANT simulada exitosa
- **PASS** — ANT timeout — recuperación stale
- **PASS** — ANT HTTP 502 — conserva último resultado válido
- **PASS** — ANT timeout sin cache — servicio no disponible

## Evidencia visual

- `screenshots/01-initial-responsive-flow.png` — flujo inicial.
- `screenshots/01b-mobile-responsive-flow.png` — flujo inicial en viewport móvil.
- `screenshots/02-ant-simulated-success.png` — resultado ANT simulado con procedencia visible.
- `screenshots/03-ant-stale-after-timeout.png` — timeout recuperado desde cache con advertencia stale.
- `screenshots/04-ant-recovery-after-502.png` — HTTP 502 recuperado desde el último resultado válido.
- `screenshots/05-ant-unavailable-without-cache.png` — timeout sin cache elegible y error 503 visible.

## Resultados humanos pendientes

La sesión con participantes humanos sigue pendiente. Usar `docs/usability/protocol.md` y `docs/usability/recording-template.md` para registrar observaciones reales.
