package ec.edu.tallerc4.application;

import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;

public interface ScenarioAwareAntProvider extends AntProvider {
    AntProviderResult queryPoints(String identification, String scenario);
}
