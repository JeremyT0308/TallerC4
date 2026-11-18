package ec.edu.tallerc4.application;

import java.util.Optional;

public interface AntCache {
    Optional<CachedAntPoints> find(String identification);
    void saveSuccessful(CachedAntPoints value);
}
