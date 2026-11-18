package ec.edu.tallerc4.application;

import ec.edu.tallerc4.domain.InputValidator;
import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AntPointsService {
    private final Optional<AntProvider> provider;

    public AntPointsService(Optional<AntProvider> provider) { this.provider = provider; }

    public AntPointsResponse query(String identification) { return query(identification, null); }

    public AntPointsResponse query(String identification, String scenario) {
        String normalized = InputValidator.requireIdentification(identification);
        AntProvider active = provider.orElseThrow(() ->
                new ServiceUnavailableException("El proveedor ANT no está configurado."));
        AntProviderResult result = scenario != null && active instanceof ScenarioAwareAntProvider scenarioProvider
                ? scenarioProvider.queryPoints(normalized, scenario)
                : active.queryPoints(normalized);
        return new AntPointsResponse(normalized, result.points(), result.source(), result.simulated(), false,
                Instant.now(), null, result.note());
    }
}
