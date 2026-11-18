# Decisiones de arquitectura

## ADR-001 — Monolito modular como primera entrega

**Contexto:** el producto es una aplicación académica pequeña con tres consultas y un equipo reducido.

**Decisión:** usar un único backend Spring Boot dividido por puertos, servicios y adaptadores.

**Consecuencia:** se mantiene simple el despliegue y se conserva una frontera clara para extraer integraciones si el producto crece.

## ADR-002 — Proveedores externos detrás de puertos

**Contexto:** SRI y ANT pueden cambiar payloads, disponibilidad o protección contra automatización.

**Decisión:** los casos de uso dependen de `TaxpayerProvider`, `VehicleProvider` y `AntProvider`, nunca de clientes HTTP directamente.

**Consecuencia:** WireMock y el simulador permiten probar contratos y fallas sin depender de los portales en cada ejecución.

## ADR-003 — Simulación ANT explícita y visible

**Contexto:** el portal público ANT respondió 401 durante la verificación automatizada y no se debe presentar un dato inventado como oficial.

**Decisión:** el simulador se habilita solo mediante configuración, devuelve `ANT_SIMULATED` y la UI muestra el badge `Simulado`.

**Consecuencia:** se puede demostrar el flujo completo sin ocultar que el dato no proviene de ANT en vivo.

## ADR-004 — Validación estructural de RUC

**Contexto:** el sistema necesita rechazar entradas evidentemente inválidas, pero no existe un checksum general vigente que cubra todas las clases de RUC que el SRI emite.

**Decisión:** exigir 13 dígitos, provincia ecuatoriana y estructura no nula; la existencia y los datos se dejan al SRI.

**Consecuencia:** se evita bloquear RUC válidos por aplicar una regla matemática incompleta.

## ADR-005 — Cache stale con advertencia explícita

**Contexto:** los puntos son útiles cuando el proveedor está temporalmente caído, pero un dato antiguo no debe parecer fresco.

**Decisión:** conservar solo resultados con puntos, permitir 15 minutos frescos y hasta 7 días stale, y devolver metadatos de antigüedad.

**Consecuencia:** el usuario puede distinguir disponibilidad actual de continuidad operativa y tomar una decisión informada.

## ADR-006 — Redis opcional y degradación suave

**Contexto:** el entorno local y las pruebas no deben requerir un servicio Redis.

**Decisión:** memoria es el default; Redis se activa con `REDIS_ENABLED=true`. Las fallas de Redis no convierten un resultado vivo en error.

**Consecuencia:** desarrollo reproducible y un camino claro hacia despliegue con persistencia compartida.
