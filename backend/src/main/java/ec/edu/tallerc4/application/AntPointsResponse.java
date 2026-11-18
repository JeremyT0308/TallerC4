package ec.edu.tallerc4.application;

import java.time.Instant;

public record AntPointsResponse(
        String identification,
        Double points,
        String source,
        boolean simulated,
        boolean stale,
        Instant retrievedAt,
        Instant staleSince,
        String note
) { }
