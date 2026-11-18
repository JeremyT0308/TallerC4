package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.TaxpayerService;
import ec.edu.tallerc4.domain.TaxpayerDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxpayerController.class)
class TaxpayerControllerTest {

    @Autowired MockMvc mvc;
    @MockBean TaxpayerService service;

    @Test
    void returnsStructuredTaxpayerResponse() throws Exception {
        when(service.find("1790016919001", "persona@example.com"))
                .thenReturn(new TaxpayerDetails("1790016919001", "CORPORACION FAVORITA C.A.", "ACTIVO",
                        "SOCIEDAD", "GENERAL", "VENTA AL POR MAYOR", null));

        mvc.perform(get("/api/v1/taxpayers/1790016919001")
                        .param("email", "persona@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruc").value("1790016919001"))
                .andExpect(jsonPath("$.legalName").value("CORPORACION FAVORITA C.A."));
    }

    @Test
    void rejectsInvalidRucBeforeCallingService() throws Exception {
        mvc.perform(get("/api/v1/taxpayers/179001691900X")
                        .param("email", "persona@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("El RUC debe tener 13 dígitos y una estructura ecuatoriana válida."));

        verifyNoInteractions(service);
    }
}
