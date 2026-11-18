# TallerC4

Aplicación web académica para consultar información tributaria y vehicular del Ecuador con SRI y ANT, con un frontend React y un backend Java modular.

## Estado

El repositorio se construye por fases. Los contratos SRI usados por el backend fueron verificados contra los servicios REST públicos observados el 17 de noviembre de 2026. ANT se mantiene detrás de un puerto porque el portal público de consulta de citaciones/puntos devolvió `401 Unauthorized` durante la verificación automatizada; el modo simulado es explícito y visible.

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
Copy-Item .env.example .env
cd backend
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

La documentación final indicará qué verificaciones externas fueron posibles y cuáles requieren credenciales o revisión manual.

