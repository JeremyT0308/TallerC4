# Contrato ANT interno y equivalencia de adaptadores

La aplicación consulta `GET /api/v1/ant/points?identification={cedula}`.
La identificación debe tener 10 dígitos. Cuando la UI parte de un RUC de
persona natural, deriva los primeros 10 dígitos; una sociedad no se presenta
como una persona para la consulta ANT.

## Respuesta estable

```json
{
  "identification": "0912576287",
  "points": 30.0,
  "source": "ANT_SIMULATED",
  "simulated": true,
  "stale": false,
  "retrievedAt": "2026-11-17T20:00:00Z",
  "staleSince": null,
  "note": "Resultado de demostración; no es una respuesta oficial ANT."
}
```

`RealAntAdapter` usa la URL configurable del portal oficial de consulta de
citaciones y busca un campo HTML de puntos verificable. También reconoce el
tipo de licencia cuando aparece en la respuesta del proveedor, aunque el
contrato estable de la aplicación solo expone los puntos y su procedencia.
Rechaza 401, HTML inesperado o ausencia de puntos como indisponibilidad. Las
verificaciones automatizadas del 17 de noviembre de 2026 recibieron 401 y la
reconsulta más reciente recibió 502, por lo que no se afirma aquí que exista
un API REST público verificable.

`SimulatedAntAdapter` cumple el mismo contrato y ofrece casos deterministas:
`success`, `no-results`, `bad-gateway`, `timeout` e `invalid-response`. Todo
resultado simulado incluye `simulated=true`, `source=ANT_SIMULATED` y una nota
visible en la UI.
