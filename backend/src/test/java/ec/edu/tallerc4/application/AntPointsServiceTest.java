package ec.edu.tallerc4.application;

import ec.edu.tallerc4.infrastructure.ant.AntProviderResult;
import org.junit.jupiter.api.Test;

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
}
