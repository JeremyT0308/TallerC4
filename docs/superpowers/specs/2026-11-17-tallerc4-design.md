# TallerC4 — Diseño de la aplicación

## Intención

TallerC4 es una aplicación web en español para consultar, desde un solo flujo,
información tributaria y vehicular del Ecuador. La persona ingresa correo y RUC,
consulta existencia y datos del contribuyente en SRI, consulta un vehículo por
placa y solicita puntos/licencia a ANT. La interfaz debe hacer visible la
procedencia de cada dato y nunca presentar un resultado simulado como oficial.

El propósito académico es demostrar un C4 nivel 4 coherente con un monolito
modular, contratos externos aislados y tolerancia a fallas; no se busca crear
autenticación, microservicios ni un portal transaccional del SRI.

## Alcance

### Metas

- React + Vite, accesible y responsive, con un flujo de consulta claro.
- Java 21 + Spring Boot, REST/JSON, validación y manejo centralizado de errores.
- Tres operaciones SRI reales y configurables:
  - existencia de RUC: `GET https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/existePorNumeroRuc?numeroRuc={ruc}`;
  - detalle de RUC: `GET https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc?ruc={ruc}`;
  - pagos/vehículo por placa o CAMV/CPN: `GET https://srienlinea.sri.gob.ec/sri-matriculacion-vehicular-recaudacion-servicio-internet/rest/consultaPagos/obtenerPorPlacaCampvCpn?placaCampvCpn={identificador}`.
- Contrato ANT interno estable con `RealAntAdapter` y `SimulatedAntAdapter`.
- Redis Cloud opcional por variables de entorno, estrategia cache-aside y fallback
  al último resultado ANT exitoso que aún esté dentro del máximo de stale.
- OpenAPI, contratos de ejemplo, inventario arquitectónico, protocolo de
  usabilidad y checklist de evidencia; sin diagramas.
- Pruebas unitarias, de contrato, integración de aplicación y frontend donde
  el entorno lo permita.

### No metas

- No se implementa login, almacenamiento de usuarios, pagos ni cambios en los
  portales oficiales.
- No se inventa un endpoint ANT como si fuera oficial. La pantalla pública
  observada es `https://consultaweb.ant.gob.ec/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp`;
  su acceso automatizado devolvió 401 al verificarlo, por lo que el adaptador
  real será configurable, marcará indisponibilidad ante 401/HTML inesperado y
  documentará la limitación.
- No se genera evidencia de uso, capturas ni respuestas externas ficticias.

## Requisitos y decisiones

- RUC: 13 dígitos; se valida el dígito verificador ecuatoriano para evitar
  llamadas obvias inválidas.
- Placa: se normaliza a mayúsculas y se aceptan formatos alfanuméricos sin
  espacios ni guiones.
- El correo se recopila como dato de contacto de la consulta; no se persiste.
- La consulta ANT de puntos usa el identificador de persona de 10 dígitos
  derivado de un RUC de persona natural (`ruc[0..10]`). Esta es una decisión
  explícita de integración debido a que el formulario requerido no solicita
  cédula separada; sociedades se rechazan con un error de datos insuficientes.
- La operación ANT interna es `GET /api/v1/ant/points?identification={cedula}`.
  La respuesta siempre incluye `points`, `source`, `simulated`, `stale`,
  `retrievedAt` y `staleSince` cuando aplica.
- Simulación ANT determinista por sufijo de identificación:
  - `0000000011`: éxito;
  - `0000000022`: sin resultados;
  - `0000000033`: 502;
  - `0000000044`: timeout;
  - `0000000055`: respuesta inválida.
  Para seleccionar estos casos en la interfaz se permite un modo demo explícito
  que reemplaza la identificación derivada solo en desarrollo/pruebas.
- Cache ANT: TTL fresco configurable por `ANT_CACHE_FRESH_MINUTES` (15 por
  defecto) y máximo stale por `ANT_CACHE_MAX_STALE_DAYS` (7 por defecto).
  Los resultados exitosos se guardan con timestamp y procedencia; errores no se
  guardan como éxitos.
- Redis se conecta con timeouts configurables. Si Redis falla, la consulta
  continúa con resultado en vivo y, si no es posible, devuelve 503 estructurado.

## Enfoques considerados

1. **Monolito modular con puertos/adaptadores (seleccionado).** Una aplicación
   Spring Boot contiene controladores, casos de uso, dominio, puertos y
   adaptadores SRI/ANT/Redis. Es sencillo de ejecutar y de mapear a C4, y permite
   sustituir ANT sin tocar frontend ni lógica de aplicación.
2. **Scraping desde React.** Reduciría código inicial, pero expone contratos,
   rompe CORS, mezcla UI con proveedores inestables y no permite cache segura ni
   manejo consistente de errores. Se descarta.
3. **Tres microservicios.** Aislaría fallas, pero agrega complejidad de red y
   despliegue sin valor para la rúbrica; contradice el alcance de monolito
   modular. Se descarta.

## Estructura propuesta

- `backend/`: Maven, paquetes `domain`, `application`, `ports`, `infrastructure`
  y `interfaces/rest`.
- `frontend/`: Vite, React, TypeScript, CSS accesible, pruebas Vitest/Testing
  Library.
- `docs/contracts/`: OpenAPI y ejemplos JSON, incluyendo equivalencia ANT.
- `docs/architecture/`: inventario C4 textual y decisiones.
- `docs/usability/`: protocolo y plantilla de observación.
- `docs/evidence/`: checklist para capturas reales.
- `README.md`, `.gitignore`, `.env.example`, `BITACORA_PRIVADA.md` ignorado.

## Criterio de éxito

Una persona puede levantar backend/frontend sin secretos, validar un RUC,
consultar detalle y pagos vehiculares cuando SRI responde, activar escenarios
ANT simulados, observar loading/éxito/vacío/error/stale y seguir un protocolo de
prueba. La documentación distingue claramente datos reales, simulados y stale.
