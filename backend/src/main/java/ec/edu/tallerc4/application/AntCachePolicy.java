package ec.edu.tallerc4.application;

import java.time.Duration;
import java.time.Instant;

public record AntCachePolicy(Duration freshFor, Duration maxStaleFor) {
    public boolean eligible(CachedAntPoints value, Instant now) {
        return !now.isAfter(value.retrievedAt().plus(maxStaleFor));
    }

    public boolean stale(CachedAntPoints value, Instant now) {
        return now.isAfter(value.retrievedAt().plus(freshFor));
    }

    public Instant staleSince(CachedAntPoints value) {
        return value.retrievedAt().plus(freshFor);
    }
}
