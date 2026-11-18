# Informe de mejora UX/UI — TallerC4

## Alcance y fecha

Este informe documenta la mejora visual del frontend React + Vite ejecutada el 17 de noviembre de 2026 (evidencia generada entre 22:59 y 23:04, hora local de Ecuador; el reporte automatizado conserva la marca UTC). Se mantuvieron los contratos API, las integraciones SRI/ANT, la simulación ANT y la arquitectura C1/C2/C3. No se modificaron backend ni diagramas IcePanel.

## Comentarios de evaluación registrados como propuestas

Las observaciones recibidas en la solicitud de mejora se registran como propuestas de diseño, no como citas textuales ni resultados de participantes:

- Hacer el encabezado principal más compacto para que las tres consultas aparezcan antes.
- Dar una señal visual clara durante carga, éxito, error, advertencia y recuperación desde cache.
- Añadir navegación rápida entre contribuyente, vehículo y ANT.
- Modernizar hover, foco, iconos, transiciones y presentación de resultados sin sacrificar lectura.
- Asegurar una experiencia usable en móvil, teclado y con `prefers-reduced-motion`.

## Problemas observados en el diseño original

La captura original mostraba un hero alto que retrasaba el acceso a los formularios, un encabezado estático y tarjetas con poca diferenciación de estados. El feedback de carga se limitaba al cambio de texto del botón; no había toast ni navegación de progreso. Los resultados eran bloques estáticos, y la procedencia/antigüedad de ANT necesitaba más jerarquía visual. La disposición responsive existente apilaba contenido, pero no comunicaba el orden del flujo ni ofrecía feedback equivalente en móvil.

## Cambios realizados y justificación

| Cambio | Implementación | Razón UX |
| --- | --- | --- |
| Refactor de `App.tsx` | `TaxpayerQueryCard`, `VehicleQueryCard`, `AntQueryCard`, `ProgressNav`, `ResultCard`, `ToastViewport`, `LoadingButton` e iconos reutilizables | Separar responsabilidades y mantener el estado/API en un solo contenedor sin cambiar contratos |
| Hero compacto | Texto resumido, metadatos SRI/ANT y espacio reducido | Permitir que las consultas aparezcan antes y conservar la identidad visual |
| Navegación de progreso | Tres pasos con estado pendiente, cargando, listo o revisar y scroll suave | Hacer visible el recorrido y permitir saltar entre consultas |
| Animaciones CSS-first | Entrada `rise-in`, aparición de resultados, toast, spinner y microtransiciones hover | Dar sensación de interfaz viva con bajo costo y sin nueva librería |
| Feedback de consulta | Botón con `aria-busy`, spinner, texto de carga y helper `Consultando datos…` | Reducir incertidumbre mientras responde SRI/ANT |
| Toasts | Notificaciones de éxito, error y advertencia con cierre y auto-dismiss | Confirmar acciones sin quitar contexto del formulario |
| Resultados interactivos | Tarjetas ANT/SRI expandibles con `details/summary`, badges y microinteracción | Mostrar primero el resumen y revelar procedencia/detalles bajo demanda |
| Estados ANT | Badge `Simulado`/`Datos stale`, fuente, `retrievedAt`, `staleSince` y advertencia | Mantener la procedencia y diferenciar cache de datos frescos |
| Formularios | Iconos, estados de foco, errores inline, botones de ancho completo en móvil | Mejorar escaneabilidad, teclado y corrección de errores |
| Responsive y movimiento reducido | Grid que se apila, navegación horizontal en móvil y `@media (prefers-reduced-motion: reduce)` | Evitar cortes y respetar preferencias de accesibilidad |

## Comparación visual

| Momento | Evidencia genuina |
| --- | --- |
| Antes | [ux-before.png](../evidence/screenshots/ux-before.png) |
| Después — desktop | [ux-after-desktop.png](../evidence/screenshots/ux-after-desktop.png) |
| Después — móvil | [ux-after-mobile.png](../evidence/screenshots/ux-after-mobile.png) |
| Hover | [ux-hover.png](../evidence/screenshots/ux-hover.png) |
| Loading | [ux-loading.png](../evidence/screenshots/ux-loading.png) |
| Toast | [ux-toast.png](../evidence/screenshots/ux-toast.png) |
| Resultados | [ux-results.png](../evidence/screenshots/ux-results.png) |

Las capturas posteriores fueron tomadas por Playwright con Edge local. Las capturas de loading y resultados ANT usan la identificación sintética `0000000099`; no representa a una persona. Las consultas SRI se verificaron automáticamente, pero no se usaron sus identificadores en estas seis capturas UX.

## Resultados de verificación posterior

| Verificación | Condición | Resultado |
| --- | --- | --- |
| Vitest + Testing Library | Suite frontend después del refactor | PASS — 6 pruebas, 0 fallos |
| Build | `npm run build` | PASS — TypeScript y Vite producción |
| RUC | Consulta SRI local con dato de prueba | PASS |
| Placa | Consulta vehicular SRI local con placa de prueba | PASS |
| ANT éxito | Simulación `success` | PASS — resultado y fuente visible |
| ANT timeout | Cache válido disponible | PASS — `Datos stale`, fuente y timestamp |
| ANT HTTP 502 | Último resultado válido disponible | PASS — recuperación stale |
| ANT sin cache | Timeout con identificación distinta | PASS — error de servicio visible |
| Responsive | Viewport 390 × 844 | PASS — formularios apilados y navegación desplazable |

El detalle de cada caso y su timestamp está en [automated-browser-report.md](../evidence/automated-browser-report.md). La ejecución automatizada no es un estudio humano.

## Estado del estudio con participantes

Pendiente. No se inventan nombres, participantes, tiempos, comentarios ni métricas humanas. Para realizarlo, usar [protocol.md](protocol.md) y completar [report-template.md](report-template.md) después de observar personas reales.

## Limitaciones conocidas

- La evidencia usa el backend local y ANT simulado para que los escenarios sean deterministas; `ANT_SIMULATED` no es una respuesta oficial de ANT.
- La validación visual automatizada confirma interacción y layout, no sustituye una sesión moderada con participantes.
- El hover solo es observable en dispositivos con puntero; la misma acción conserva foco visible y estados accesibles en teclado.
