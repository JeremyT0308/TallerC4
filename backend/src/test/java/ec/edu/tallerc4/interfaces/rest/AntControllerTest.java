package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.AntPointsResponse;
import ec.edu.tallerc4.application.AntPointsService;
import ec.edu.tallerc4.application.BadGatewayException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AntController.class)
class AntControllerTest {
    @Autowired MockMvc mvc;
    @MockBean AntPointsService service;

    @Test
    void returnsStableAntResponse() throws Exception {
        when(service.query("0912576287")).thenReturn(new AntPointsResponse("0912576287", 30.0, "ANT", false, false, Instant.parse("2026-11-17T20:00:00Z"), null, null));

        mvc.perform(get("/api/v1/ant/points").param("identification", "0912576287"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(30.0))
                .andExpect(jsonPath("$.source").value("ANT"))
                .andExpect(jsonPath("$.stale").value(false));
    }

    @Test
    void rejectsInvalidIdentificationBeforeCallingService() throws Exception {
        mvc.perform(get("/api/v1/ant/points").param("identification", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/v1/ant/points"));

        verifyNoInteractions(service);
    }

    @Test
    void mapsProviderBadGatewayToStructured502() throws Exception {
        when(service.query("0912576287"))
                .thenThrow(new BadGatewayException("ANT upstream devolvió HTTP 502 Bad Gateway."));

        mvc.perform(get("/api/v1/ant/points").param("identification", "0912576287"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("BAD_GATEWAY"))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("ANT upstream devolvió HTTP 502 Bad Gateway."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/ant/points"));
    }
}
