# Reporte de verificación automatizada de usabilidad

- Generado: 2026-11-18T04:41:51.205Z
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

- `screenshots/01-initial-responsive-flow.png` — flujo inicial conservado de la evidencia previa.
- `screenshots/01b-mobile-responsive-flow.png` — flujo móvil conservado de la evidencia previa.
- `screenshots/ux-before.png` — diseño original conservado antes del cambio.
- `screenshots/ux-after-desktop.png` — diseño renovado en viewport desktop.
- `screenshots/ux-after-mobile.png` — diseño renovado en viewport móvil.
- `screenshots/ux-hover.png` — hover real sobre el CTA de contribuyente.
- `screenshots/ux-toast.png` — toast real de éxito ANT.
- `screenshots/ux-loading.png` — loading real con respuesta demorada.
- `screenshots/ux-results.png` — resultado ANT expandido con procedencia.
- `screenshots/api-sri-taxpayer.png` — resultado SRI de contribuyente en flujo E2E.
- `screenshots/api-sri-vehicle.png` — resultado SRI vehicular en flujo E2E.
- `screenshots/02-ant-simulated-success.png` — resultado ANT simulado conservado de la evidencia previa.
- `screenshots/03-ant-stale-after-timeout.png` — timeout stale conservado de la evidencia previa.
- `screenshots/04-ant-recovery-after-502.png` — recuperación 502 conservada de la evidencia previa.
- `screenshots/05-ant-unavailable-without-cache.png` — error sin cache conservado de la evidencia previa.

## Resultados humanos pendientes

La sesión con participantes humanos sigue pendiente. Usar `docs/usability/protocol.md` y `docs/usability/recording-template.md` para registrar observaciones reales.
