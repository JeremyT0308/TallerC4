package ec.edu.tallerc4.application;

import java.time.Instant;

public record CachedAntPoints(
        String identification,
        Double points,
        String source,
        boolean simulated,
        Instant retrievedAt,
        String licenseType,
        String note
) { }
