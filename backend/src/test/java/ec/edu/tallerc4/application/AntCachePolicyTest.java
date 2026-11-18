package ec.edu.tallerc4.application;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AntCachePolicyTest {
    private static final Instant NOW = Instant.parse("2026-11-17T20:00:00Z");
    private static final CachedAntPoints VALUE = new CachedAntPoints("0000000011", 30.0, "ANT_SIMULATED", true,
            NOW, null, null);
    private final AntCachePolicy policy = new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7));

    @Test
    void keepsExactlyFreshBoundaryFreshAndMarksLaterDataStale() {
        assertFalse(policy.stale(VALUE, NOW.plus(Duration.ofMinutes(15))));
        assertTrue(policy.stale(VALUE, NOW.plus(Duration.ofMinutes(15).plusSeconds(1))));
        assertEquals(NOW.plus(Duration.ofMinutes(15)), policy.staleSince(VALUE));
    }

    @Test
    void allowsExactlyMaximumAgeAndRejectsOlderCache() {
        assertTrue(policy.eligible(VALUE, NOW.plus(Duration.ofDays(7))));
        assertFalse(policy.eligible(VALUE, NOW.plus(Duration.ofDays(7).plusSeconds(1))));
    }
}
