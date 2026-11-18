package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.TaxpayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
