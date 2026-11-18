package ec.edu.tallerc4.infrastructure.sri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.tallerc4.application.ResourceNotFoundException;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import ec.edu.tallerc4.application.VehicleProvider;
import ec.edu.tallerc4.domain.VehicleDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class SriVehicleAdapter implements VehicleProvider {
    private static final String PAYMENTS_PATH = "/sri-matriculacion-vehicular-recaudacion-servicio-internet/rest/consultaPagos/obtenerPorPlacaCampvCpn";

    private final RestClient client;
    private final ObjectMapper mapper;

    public SriVehicleAdapter(RestClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public VehicleDetails find(String plate) {
        try {
            String body = client.get().uri(uri -> uri.path(PAYMENTS_PATH).queryParam("placaCampvCpn", plate).build())
                    .retrieve().body(String.class);
            JsonNode root = mapper.readTree(body);
            JsonNode data = root.path("data");
            if (!data.isArray()) throw new SriPayloadException("SRI devolvió un reporte vehicular inválido.");
            if (data.isEmpty()) throw new ResourceNotFoundException("No se encontró información de pagos para la placa indicada.");
            JsonNode first = data.get(0);
            return new VehicleDetails(
                    first.hasNonNull("placaCamvActual") ? first.get("placaCamvActual").asText() : plate,
                    text(first, "marca"), text(first, "modelo"), integer(first, "anioModelo"), data.size());
        } catch (JsonProcessingException ex) {
            throw new SriPayloadException("SRI devolvió un reporte vehicular no interpretable.", ex);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("El servicio SRI de vehículos no está disponible.", ex);
        }
    }

    private String text(JsonNode node, String field) { return node.hasNonNull(field) ? node.get(field).asText() : null; }
    private Integer integer(JsonNode node, String field) { return node.hasNonNull(field) && node.get(field).canConvertToInt() ? node.get(field).asInt() : null; }
}
