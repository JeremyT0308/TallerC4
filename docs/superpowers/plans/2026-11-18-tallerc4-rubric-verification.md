# TallerC4 Rubric Verification Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Verificar y corregir los criterios 2, 3 y 4 de la rúbrica sin reconstruir TallerC4, dejando evidencia reproducible y documentación consolidada.

**Architecture:** Mantener los controladores REST, servicios, puertos/adaptadores, React + Vite y el contrato ANT existente. Los cambios de API se limitarán a errores estructurados y mapeo explícito de gateway; los cambios de cache reforzarán pruebas y metadatos sin alterar el flujo frontend.

**Tech Stack:** Spring Boot 3, Java 21, Maven, JUnit/MockMvc, React + Vite, Vitest/Testing Library, Playwright/Edge, OpenAPI 3.0.3.

**Spec:** `C:/Users/Usuario/.codex/attachments/82e4af07-4241-4255-8fef-9cef11b45a40/Pasted text.txt`

## Global Constraints

- No modificar IcePanel ni reconstruir la arquitectura C1/C2/C3.
- No cambiar contratos existentes de React salvo documentar respuestas nuevas compatibles.
- No exponer credenciales, datos sensibles ni modificar secretos.
- No incluir `BITACORA_PRIVADA.md` en GitHub.
- No hacer `git commit` ni `git push`; esperar confirmación entre etapas.

## Review Focus

- Identificación ANT inválida debe producir HTTP 400 estructurado antes del proveedor.
- Un HTTP 502 del proveedor debe distinguirse de un servicio 503 cuando no existe fallback.
- Fallback fresco usado por una falla debe marcarse stale aunque aún esté dentro de la ventana de frescura.
- Cache vencida, Redis no disponible y respuesta ANT inválida no deben convertir un error en dato aparentemente fresco.
- OpenAPI debe declarar rutas, parámetros, respuestas, errores y timestamps que coincidan con código y pruebas.

## Etapa 1 — Punto 2: API y contrato

**Archivos:** controladores REST, excepciones, handler global, OpenAPI y pruebas REST/validación.

- [x] Auditar rutas y verbos con pruebas MockMvc y `curl`/PowerShell contra backend local.
- [x] Añadir mapeo explícito para upstream HTTP 502 sin romper el fallback stale.
- [x] Cubrir 200, 400, 404, 502 y 503 con cuerpo `code`, `message`, `status`, `timestamp`, `path`.
- [x] Validar correo, RUC, placa e identificación y comprobar que las entradas normalizadas llegan al servicio.
- [x] Actualizar OpenAPI para reflejar respuestas reales.
- [x] Ejecutar pruebas backend focalizadas y prueba HTTP real.
- [x] Preparar comandos manuales para `fix: verify api contracts and structured errors`, sin ejecutarlo.

## Etapa 2 — Punto 3: cache y resiliencia ANT

**Archivos:** servicio ANT, política/cache, pruebas de servicio/cache/Redis y evidencia técnica.

- [ ] Cubrir success, 502, timeout, respuesta inválida, recuperación, sin cache y TTL expirado.
- [ ] Confirmar `stale`, `source`, `retrievedAt`, `staleSince` y advertencia visible en React.
- [ ] Verificar memoria y Redis real solo si existen credenciales disponibles; registrar ausencia sin inventar resultado.
- [ ] Documentar frescura de 15 minutos y TTL máximo de 7 días con justificación.
- [ ] Ejecutar pruebas backend y comandos locales reproducibles.
- [ ] Preparar comandos manuales para `test: strengthen ant cache resilience verification`, sin ejecutarlo.

## Etapa 3 — Punto 4 y documentación final

**Archivos:** `docs/evidence/rubric-verification.md`, README, reporte E2E y BITACORA ignorada.

- [ ] Ejecutar backend + frontend y comprobar los tres flujos E2E en desktop y móvil.
- [ ] Reutilizar evidencias válidas y capturar solo las faltantes.
- [ ] Crear tabla por criterio con requisito, implementación, prueba, resultado, evidencia y limitación.
- [ ] Actualizar README con enlaces al contrato, reporte de rúbrica y evidencias.
- [ ] Actualizar `BITACORA_PRIVADA.md` sin incluirlo en staging.
- [ ] Ejecutar suites, build y E2E finales.
- [ ] Preparar comandos manuales para `docs: consolidate rubric compliance evidence`, sin ejecutarlo.
