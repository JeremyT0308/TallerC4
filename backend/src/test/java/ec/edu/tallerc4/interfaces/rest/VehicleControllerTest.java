package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.VehicleService;
import ec.edu.tallerc4.domain.VehicleDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired MockMvc mvc;
    @MockBean VehicleService service;

    @Test
    void normalizesPlateAndReturnsVehicle() throws Exception {
        when(service.find("ABC1234")).thenReturn(new VehicleDetails("ABC1234", "MAZDA", "BT-50", 2024, 2));

        mvc.perform(get("/api/v1/vehicles/abc-1234").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value("ABC1234"))
                .andExpect(jsonPath("$.paymentCount").value(2));
    }
}
