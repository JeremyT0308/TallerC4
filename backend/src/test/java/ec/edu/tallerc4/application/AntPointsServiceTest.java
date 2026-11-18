package ec.edu.tallerc4.application;

import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;
import ec.edu.tallerc4.infrastructure.ant.SimulatedAntAdapter;
import ec.edu.tallerc4.infrastructure.cache.InMemoryAntCache;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AntPointsServiceTest {
    @Test
    void returnsLiveResultWithProviderProvenance() {
        AntProvider provider = mock(AntProvider.class);
        when(provider.queryPoints("0912576287")).thenReturn(new AntProviderResult("0912576287", 30.0, "B", false, false, "ANT", ""));

        AntPointsResponse response = new AntPointsService(Optional.of(provider)).query("0912576287");

        assertEquals(30.0, response.points());
        assertEquals("ANT", response.source());
        assertFalse(response.stale());
        assertFalse(response.simulated());
        verify(provider).queryPoints("0912576287");
    }

    @Test
    void rejectsIdentificationWithWrongLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new AntPointsService(Optional.empty()).query("123"));
    }

    @Test
    void simulatedTimeoutAndBadGatewayRecoverFromLastSuccessfulResult() {
        SimulatedAntAdapter provider = new SimulatedAntAdapter("success");
        InMemoryAntCache cache = new InMemoryAntCache();
        AntPointsService service = new AntPointsService(Optional.of(provider), Optional.of(cache),
                new AntCachePolicy(Duration.ofMinutes(15), Duration.ofDays(7)),
                () -> Instant.parse("2026-11-17T20:00:00Z"));

        AntPointsResponse live = service.query("0000000011", "success");
        AntPointsResponse timeoutFallback = service.query("0000000011", "timeout");
        AntPointsResponse badGatewayFallback = service.query("0000000011", "bad-gateway");

        assertEquals(30.0, live.points());
        assertFalse(live.stale());
        assertEquals(30.0, timeoutFallback.points());
        assertTrue(timeoutFallback.stale());
        assertEquals("ANT_SIMULATED", timeoutFallback.source());
        assertEquals(30.0, badGatewayFallback.points());
        assertTrue(badGatewayFallback.stale());
        assertEquals("ANT_SIMULATED", badGatewayFallback.source());
    }
}
