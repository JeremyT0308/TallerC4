# Protocolo de prueba de usabilidad

## Objetivo

Comprobar que una persona puede completar el flujo SRI → vehículo → ANT, entender qué dato es oficial o simulado y reconocer cuándo un resultado ANT está stale o no disponible.

## Perfil y condiciones

- 3 a 5 estudiantes o colaboradores que conozcan formularios web, sin explicarles la arquitectura.
- Sesión de 10–15 minutos, navegador de escritorio y, si es posible, una segunda sesión móvil.
- No usar datos personales reales; emplear los datos de prueba documentados.
- Registrar pantalla solo con consentimiento y ocultar cualquier correo introducido.

## Preparación

1. Levantar backend con `ANT_PROVIDER=simulated` y `ANT_SIMULATION_SCENARIO=success`.
2. Levantar frontend con `npm run dev`.
3. Confirmar que la URL muestra los tres pasos y que el badge de simulación está explicado.

## Guion para la persona participante

No explicar dónde hacer clic. Leer únicamente: “Necesitas revisar la información tributaria, vehicular y los puntos de una persona. Cuéntame en voz alta qué entiendes y qué harías”.

## Tareas

1. Consultar un contribuyente usando correo y RUC.
2. Consultar la placa `PEI7954` y explicar qué significa el conteo de pagos.
3. Consultar puntos ANT con escenario de éxito e identificar la fuente.
4. Cambiar a `Sin resultados` y explicar la diferencia frente a `HTTP 502`.
5. Mostrar un resultado stale y decir si lo usaría como dato actual.

## Criterios de éxito

- Completa cada consulta sin asistencia.
- Encuentra los mensajes de validación sin enviar formularios incompletos.
- Distingue `ANT`, `ANT_SIMULATED` y `ANT_CACHE`.
- Describe stale como un resultado antiguo recuperado por contingencia, no como una consulta viva.
- Puede recuperar el flujo después de un error 502 o timeout.

## Preguntas posteriores

- ¿Qué parte del resultado te dio más confianza?
- ¿Qué texto o control fue ambiguo?
- ¿Qué esperabas que ocurriera después de un error del proveedor?
- ¿Qué información adicional necesitarías antes de usar el resultado en un trámite?
