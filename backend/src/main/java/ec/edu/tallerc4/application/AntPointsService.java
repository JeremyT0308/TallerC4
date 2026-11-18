package ec.edu.tallerc4.application;

import ec.edu.tallerc4.domain.InputValidator;
import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class AntPointsService {
    private final Optional<AntProvider> provider;
    private final Optional<AntCache> cache;
    private final AntCachePolicy cachePolicy;
    private final Supplier<Instant> clock;

    public AntPointsService(Optional<AntProvider> provider) {
        this(provider, Optional.empty(), new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), Instant::now);
    }

    @Autowired
    public AntPointsService(Optional<AntProvider> provider, Optional<AntCache> cache,
                            AntCachePolicy cachePolicy, Supplier<Instant> clock) {
        this.provider = provider;
        this.cache = cache;
        this.cachePolicy = cachePolicy;
        this.clock = clock;
    }

    public AntPointsResponse query(String identification) { return query(identification, null); }

    public AntPointsResponse query(String identification, String scenario) {
        String normalized = InputValidator.requireIdentification(identification);
        AntProvider active = provider.orElseThrow(() ->
                new ServiceUnavailableException("El proveedor ANT no está configurado."));
        try {
            AntProviderResult result = scenario != null && active instanceof ScenarioAwareAntProvider scenarioProvider
                    ? scenarioProvider.queryPoints(normalized, scenario)
                    : active.queryPoints(normalized);
            Instant retrievedAt = clock.get();
            AntPointsResponse response = new AntPointsResponse(normalized, result.points(), result.source(), result.simulated(), false,
                    retrievedAt, null, result.note());
            if (result.points() != null) save(response);
            return response;
        } catch (ServiceUnavailableException providerFailure) {
            Optional<CachedAntPoints> cached = findCache(normalized);
            if (cached.isPresent() && cachePolicy.eligible(cached.get(), clock.get())) {
                CachedAntPoints value = cached.get();
                Instant fallbackAt = clock.get();
                boolean stale = true;
                Instant staleSince = cachePolicy.stale(value, fallbackAt) ? cachePolicy.staleSince(value) : fallbackAt;
                return new AntPointsResponse(value.identification(), value.points(), value.source(), value.simulated(), stale,
                        value.retrievedAt(), staleSince,
                        "Resultado recuperado de cache ANT; la consulta en vivo falló.");
            }
            throw new ServiceUnavailableException("ANT no está disponible y no existe un resultado ANT utilizable.", providerFailure);
        }
    }

    private void save(AntPointsResponse response) {
        try {
            cache.ifPresent(value -> value.saveSuccessful(new CachedAntPoints(response.identification(), response.points(),
                    response.source(), response.simulated(), response.retrievedAt(), null, response.note())));
        } catch (CacheUnavailableException ignored) {
            // A live result remains useful even when Redis is down.
        }
    }

    private Optional<CachedAntPoints> findCache(String identification) {
        try {
            return cache.flatMap(value -> value.find(identification));
        } catch (CacheUnavailableException ignored) {
            return Optional.empty();
        }
    }
}
