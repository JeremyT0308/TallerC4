# Verificación frontend y navegador

- Generado: `2026-11-17T23:41:51-05:00` (ejecución E2E; reporte completo en `automated-browser-report.md`).
- URL: `http://127.0.0.1:5173/`.
- Backend: Spring Boot local en `http://127.0.0.1:8080`.
- Condición: `ANT_PROVIDER=simulated`, escenarios deterministas, `REDIS_ENABLED=false`, cache en memoria.
- Datos: correo sintético, RUC corporativo `1790016919001`, placa de prueba `PEI7954` e identificación ANT sintética `0000000099`.
- La ejecución automatizada no representa una prueba humana con participantes.

## Comandos y resultados observados

| Comando | Resultado | Prueba |
| --- | --- | --- |
| `npm test -- --run --reporter=dot` | **PASS — 6 pruebas, 0 fallos** | Vitest + Testing Library |
| `npm run build` | **PASS** | TypeScript y bundle Vite de producción |
| `npm run e2e:usability` | **PASS — 6 comprobaciones** | Playwright con Microsoft Edge headless |

## Comprobaciones E2E

- Consulta de contribuyente SRI: **PASS**, resultado `ACTIVO`.
- Consulta de vehículo SRI: **PASS**, aparece el conteo de pagos.
- Consulta ANT simulada: **PASS**, 30 puntos, fuente `ANT_SIMULATED`.
- Loading: **PASS**, botón ocupado, indicador visible y captura tomada durante una respuesta demorada.
- Timeout con cache: **PASS**, badge `Datos stale`, advertencia, procedencia y timestamps visibles.
- HTTP 502 con cache: **PASS**, conserva el último resultado válido como stale.
- Timeout sin cache: **PASS**, mensaje de servicio no disponible.
- Responsive y hover: **PASS**, capturas desktop, móvil y hover generadas en navegador real.

## Capturas de esta ejecución

- `screenshots/ux-after-desktop.png`
- `screenshots/ux-after-mobile.png`
- `screenshots/ux-hover.png`
- `screenshots/ux-loading.png`
- `screenshots/ux-toast.png`
- `screenshots/ux-results.png`
- `screenshots/api-sri-taxpayer.png`
- `screenshots/api-sri-vehicle.png`

Las capturas se tomaron con Playwright/Edge y no contienen tokens, contraseñas ni identificadores personales. Las imágenes ANT usan el identificador sintético documentado.
