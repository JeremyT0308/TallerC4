package ec.edu.tallerc4.infrastructure.sri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.tallerc4.application.ResourceNotFoundException;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import ec.edu.tallerc4.application.TaxpayerProvider;
import ec.edu.tallerc4.domain.TaxpayerDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class SriTaxpayerAdapter implements TaxpayerProvider {
    private static final String EXISTS_PATH = "/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/existePorNumeroRuc";
    private static final String DETAILS_PATH = "/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc";

    private final RestClient client;
    private final ObjectMapper mapper;

    public SriTaxpayerAdapter(RestClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public boolean exists(String ruc) {
        String body = get(EXISTS_PATH, "numeroRuc", ruc);
        try {
            JsonNode node = mapper.readTree(body);
            if (!node.isBoolean()) throw new SriPayloadException("SRI devolvió un indicador de existencia inválido.");
            return node.booleanValue();
        } catch (JsonProcessingException ex) {
            throw new SriPayloadException("SRI devolvió un indicador de existencia no interpretable.", ex);
        }
    }

    @Override
    public TaxpayerDetails find(String ruc) {
        String body = get(DETAILS_PATH, "ruc", ruc);
        try {
            JsonNode records = mapper.readTree(body);
            if (!records.isArray()) throw new SriPayloadException("SRI devolvió un detalle de contribuyente inválido.");
            if (records.isEmpty()) throw new ResourceNotFoundException("No se encontró un contribuyente para el RUC indicado.");
            JsonNode item = records.get(0);
            return new TaxpayerDetails(
                    text(item, "numeroRuc"), text(item, "razonSocial"), text(item, "estadoContribuyenteRuc"),
                    text(item, "tipoContribuyente"), text(item, "regimen"), text(item, "actividadEconomicaPrincipal"),
                    text(item, "categoria"));
        } catch (JsonProcessingException ex) {
            throw new SriPayloadException("SRI devolvió un detalle de contribuyente no interpretable.", ex);
        }
    }

    private String get(String path, String parameter, String value) {
        try {
            return client.get().uri(uri -> uri.path(path).queryParam(parameter, value).build())
                    .retrieve().body(String.class);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("El servicio SRI no está disponible.", ex);
        }
    }

    private String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }
}
