# Checklist de evidencia reproducible

La evidencia debe mostrar la interfaz o la salida real de una ejecución. No se deben presentar respuestas ANT simuladas como si fueran oficiales.

## Capturas recomendadas

- [ ] Pantalla inicial responsive con los tres pasos y campos etiquetados.
- [ ] Resultado SRI de contribuyente con `RUC`, estado, tipo, régimen y actividad.
- [ ] Resultado SRI vehicular para `PEI7954`, mostrando el conteo de pagos y los campos ausentes como no disponibles.
- [ ] Resultado ANT de escenario `Éxito` con badge `Simulado`, fuente `ANT_SIMULATED` y nota visible.
- [ ] Resultado ANT `Sin resultados` o error `HTTP 502`, con mensaje accionable.
- [ ] Resultado stale con `Datos stale`, `Consultado`, `Datos stale desde` y advertencia.
- [ ] Vista móvil o ancho reducido donde no se corte ningún formulario.

## Datos reproducibles

- RUC de prueba: `1716537341001` para el flujo simulado de frontend.
- Placa de prueba: `PEI7954`.
- Identificación ANT: `1716537341`.
- Escenarios simulados: `success`, `no-results`, `bad-gateway`, `timeout`, `invalid-response`.

## Comandos de verificación

```powershell
cd backend
mvn test

cd ..\frontend
npm test -- --run
npm run build
```

## Matriz de procedencia

| Evidencia | Fuente declarada | ¿Permite afirmar dato oficial? |
| --- | --- | --- |
| SRI contribuyente | SRI | Sí, como respuesta del proveedor consultado |
| SRI vehículo | SRI | Sí, solo para los campos presentes en el payload |
| ANT éxito local | `ANT_SIMULATED` | No; es demostración |
| ANT cache | `ANT_CACHE` o fuente original con `stale=true` | No como dato fresco; debe mostrarse la antigüedad |
| ANT real | `ANT` | Solo si el portal respondió y el campo fue extraído verificablemente |

## Evidencia externa no automatizada

Si ANT devuelve 401, captcha, HTML sin campos verificables o cambia su contrato, registrar la fecha, URL, estado HTTP y captura del mensaje; no sustituirlo silenciosamente con un número inventado. La prueba real de Redis requiere un Redis accesible y debe indicar host/configuración sin exponer credenciales.
