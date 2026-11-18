package ec.edu.tallerc4.infrastructure.ant;

import ec.edu.tallerc4.application.ScenarioAwareAntProvider;
import ec.edu.tallerc4.application.BadGatewayException;
import ec.edu.tallerc4.application.ServiceUnavailableException;
public class SimulatedAntAdapter implements ScenarioAwareAntProvider {
    private final String defaultScenario;

    public SimulatedAntAdapter(String defaultScenario) { this.defaultScenario = defaultScenario; }

    @Override
    public AntProviderResult queryPoints(String identification) {
        return queryPoints(identification, defaultScenario);
    }

    @Override
    public AntProviderResult queryPoints(String identification, String scenario) {
        return switch (scenario == null ? defaultScenario : scenario) {
            case "success" -> new AntProviderResult(identification, 30.0, "B", false, true, "ANT_SIMULATED",
                    "Resultado de demostración; no es una respuesta oficial ANT.");
            case "no-results" -> new AntProviderResult(identification, null, null, true, true, "ANT_SIMULATED",
                    "El simulador no encontró puntos para esta identificación.");
            case "bad-gateway" -> throw new BadGatewayException("ANT simulado: HTTP 502 Bad Gateway.");
            case "timeout" -> throw new ServiceUnavailableException("ANT simulado: timeout del proveedor.");
            case "invalid-response" -> throw new ServiceUnavailableException("ANT simulado: respuesta inválida.");
            default -> throw new IllegalArgumentException("Escenario ANT simulado desconocido.");
        };
    }
}
