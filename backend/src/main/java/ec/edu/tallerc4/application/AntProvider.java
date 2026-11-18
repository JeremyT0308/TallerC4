package ec.edu.tallerc4.application;

import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;

public interface AntProvider {
    AntProviderResult queryPoints(String identification);
}
