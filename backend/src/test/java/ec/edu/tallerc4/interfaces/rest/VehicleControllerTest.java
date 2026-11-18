package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.VehicleService;
import ec.edu.tallerc4.application.ResourceNotFoundException;
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

    @Test
    void rejectsInvalidPlateBeforeCallingService() throws Exception {
        mvc.perform(get("/api/v1/vehicles/!!").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/v1/vehicles/!!"));
    }

    @Test
    void returnsStructured404WhenVehicleDoesNotExist() throws Exception {
        when(service.find("ABC1234"))
                .thenThrow(new ResourceNotFoundException("No se encontró información de pagos para la placa indicada."));

        mvc.perform(get("/api/v1/vehicles/ABC1234").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/vehicles/ABC1234"));
    }
}
