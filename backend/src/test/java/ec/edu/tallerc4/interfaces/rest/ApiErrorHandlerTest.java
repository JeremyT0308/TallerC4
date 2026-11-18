package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.TaxpayerService;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(TaxpayerController.class)
class ApiErrorHandlerTest {

    @Autowired MockMvc mvc;
    @MockBean TaxpayerService service;

    @Test
    void rejectsMissingEmailWithStableErrorShape() throws Exception {
        mvc.perform(get("/api/v1/taxpayers/1790016919001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/v1/taxpayers/1790016919001"));
    }

    @Test
    void mapsProviderFailureToStructured503() throws Exception {
        when(service.find("1790016919001", "persona@example.com"))
                .thenThrow(new ServiceUnavailableException("El servicio SRI no está disponible."));

        mvc.perform(get("/api/v1/taxpayers/1790016919001").param("email", "persona@example.com"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("El servicio SRI no está disponible."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/taxpayers/1790016919001"));
    }
}
