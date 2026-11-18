package ec.edu.tallerc4.application;

import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AntPointsCacheTest {
    private static final Instant NOW = Instant.parse("2026-11-17T20:00:00Z");

    @Test
    void savesSuccessfulLiveResult() {
        RecordingProvider provider = new RecordingProvider(new AntProviderResult("0912576287", 30.0, "B", false, false, "ANT", null));
        RecordingCache cache = new RecordingCache();
        AntPointsService service = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW);

        AntPointsResponse response = service.query("0912576287");

        assertFalse(response.stale());
        assertNotNull(cache.saved);
        assertEquals(30.0, cache.saved.points());
    }

    @Test
    void returnsEligibleCachedResultAsStaleAfterProviderFailure() {
        RecordingProvider provider = new RecordingProvider(new ServiceUnavailableException("timeout"));
        RecordingCache cache = new RecordingCache(new CachedAntPoints("0912576287", 30.0, "ANT", false,
                Instant.parse("2026-11-17T19:30:00Z"), "B", null));
        AntPointsService service = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW);

        AntPointsResponse response = service.query("0912576287");

        assertTrue(response.stale());
        assertEquals("ANT", response.source());
        assertEquals(Instant.parse("2026-11-17T19:30:00Z"), response.retrievedAt());
        assertEquals(Instant.parse("2026-11-17T19:45:00Z"), response.staleSince());
    }

    @Test
    void marksFreshCacheAsStaleWhenItIsUsedAfterProviderFailure() {
        RecordingProvider provider = new RecordingProvider(new ServiceUnavailableException("502"));
        RecordingCache cache = new RecordingCache(new CachedAntPoints("0912576287", 30.0, "ANT", false,
                Instant.parse("2026-11-17T19:55:00Z"), "B", null));
        AntPointsResponse response = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW).query("0912576287");

        assertTrue(response.stale());
        assertEquals(NOW, response.staleSince());
    }

    @Test
    void rejectsExpiredCachedResultWithStructuredUnavailableException() {
        RecordingProvider provider = new RecordingProvider(new ServiceUnavailableException("502"));
        RecordingCache cache = new RecordingCache(new CachedAntPoints("0912576287", 30.0, "ANT", false,
                Instant.parse("2026-11-01T19:30:00Z"), "B", null));
        AntPointsService service = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW);

        ServiceUnavailableException error = assertThrows(ServiceUnavailableException.class,
                () -> service.query("0912576287"));
        assertTrue(error.getMessage().contains("no existe un resultado ANT utilizable"));
    }

    @Test
    void preservesBadGatewayStatusWhenNoCacheIsEligible() {
        RecordingProvider provider = new RecordingProvider(new BadGatewayException("ANT upstream devolvió HTTP 502 Bad Gateway."));
        AntPointsService service = new AntPointsService(Optional.of(provider), Optional.of(new RecordingCache()),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW);

        BadGatewayException error = assertThrows(BadGatewayException.class,
                () -> service.query("0912576287"));

        assertTrue(error.getMessage().contains("no existe un resultado ANT utilizable"));
    }

    @Test
    void returnsLiveResultWhenCacheWriteFails() {
        RecordingProvider provider = new RecordingProvider(new AntProviderResult("0912576287", 30.0, "B", false, false, "ANT", null));
        RecordingCache cache = new RecordingCache();
        cache.failWrites = true;
        AntPointsResponse response = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW).query("0912576287");
        assertEquals(30.0, response.points());
        assertFalse(response.stale());
    }

    @Test
    void doesNotCacheNoResults() {
        RecordingProvider provider = new RecordingProvider(new AntProviderResult("0912576287", null, null, true, true, "ANT_SIMULATED", "sin resultados"));
        RecordingCache cache = new RecordingCache();
        AntPointsResponse response = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)), () -> NOW).query("0912576287");
        assertNull(response.points());
        assertNull(cache.saved);
    }

    private static class RecordingProvider implements AntProvider {
        private final AntProviderResult result;
        private final RuntimeException failure;
        RecordingProvider(AntProviderResult result) { this.result = result; this.failure = null; }
        RecordingProvider(RuntimeException failure) { this.result = null; this.failure = failure; }
        @Override public AntProviderResult queryPoints(String identification) { if (failure != null) throw failure; return result; }
    }

    private static class RecordingCache implements AntCache {
        private CachedAntPoints saved;
        private final CachedAntPoints existing;
        private boolean failWrites;
        RecordingCache() { this.existing = null; }
        RecordingCache(CachedAntPoints existing) { this.existing = existing; }
        @Override public Optional<CachedAntPoints> find(String identification) { return Optional.ofNullable(existing); }
        @Override public void saveSuccessful(CachedAntPoints value) { if (failWrites) throw new CacheUnavailableException("redis down"); saved = value; }
    }
}
