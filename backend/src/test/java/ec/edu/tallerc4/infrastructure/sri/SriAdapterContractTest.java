package ec.edu.tallerc4.infrastructure.sri;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import ec.edu.tallerc4.application.ResourceNotFoundException;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import ec.edu.tallerc4.domain.TaxpayerDetails;
import ec.edu.tallerc4.domain.VehicleDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class SriAdapterContractTest {
    private WireMockServer server;
    private RestClient client;

    @BeforeEach
    void startServer() {
        server = new WireMockServer(0);
        server.start();
        client = RestClient.builder().baseUrl(server.baseUrl()).build();
    }

    @AfterEach
    void stopServer() { server.stop(); }

    @Test
    void mapsTaxpayerDetailsFromSriShape() {
        server.stubFor(get(urlPathEqualTo("/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/existePorNumeroRuc"))
                .willReturn(okJson("true")));
        server.stubFor(get(urlPathEqualTo("/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc"))
                .willReturn(okJson("[{\"numeroRuc\":\"1790016919001\",\"razonSocial\":\"CORPORACION FAVORITA C.A.\",\"estadoContribuyenteRuc\":\"ACTIVO\",\"actividadEconomicaPrincipal\":\"VENTA AL POR MAYOR\",\"tipoContribuyente\":\"SOCIEDAD\",\"regimen\":\"GENERAL\",\"categoria\":null}]")));

        SriTaxpayerAdapter adapter = new SriTaxpayerAdapter(client, new ObjectMapper());

        assertTrue(adapter.exists("1790016919001"));
        TaxpayerDetails result = adapter.find("1790016919001");
        assertEquals("CORPORACION FAVORITA C.A.", result.legalName());
        assertEquals("ACTIVO", result.status());
        server.verify(getRequestedFor(urlPathEqualTo("/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc"))
                .withQueryParam("ruc", equalTo("1790016919001")));
    }

    @Test
    void mapsVehiclePaymentReportWithoutInventingMissingMetadata() {
        server.stubFor(get(urlPathEqualTo("/sri-matriculacion-vehicular-recaudacion-servicio-internet/rest/consultaPagos/obtenerPorPlacaCampvCpn"))
                .willReturn(okJson("{\"objeto\":null,\"mensajeServidor\":{\"texto\":\"ok\"},\"data\":[{\"placaCamvActual\":\"PEI7954\",\"fechaDePago\":\"2025-06-03 02:13\",\"monto\":98.64},{\"placaCamvActual\":\"PEI7954\",\"fechaDePago\":\"2024-02-05 01:55\",\"monto\":98.64}]}")));

        VehicleDetails result = new SriVehicleAdapter(client, new ObjectMapper()).find("PEI7954");

        assertEquals("PEI7954", result.plate());
        assertEquals(2, result.paymentCount());
        assertNull(result.brand());
    }

    @Test
    void turnsEmptyTaxpayerArrayIntoNotFound() {
        server.stubFor(get(urlPathEqualTo("/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc"))
                .willReturn(okJson("[]")));

        assertThrows(ResourceNotFoundException.class,
                () -> new SriTaxpayerAdapter(client, new ObjectMapper()).find("1790016919001"));
    }

    @Test
    void turnsBadGatewayIntoUnavailable() {
        server.stubFor(get(urlPathEqualTo("/sri-matriculacion-vehicular-recaudacion-servicio-internet/rest/consultaPagos/obtenerPorPlacaCampvCpn"))
                .willReturn(aResponse().withStatus(502).withBody("upstream unavailable")));

        assertThrows(ServiceUnavailableException.class,
                () -> new SriVehicleAdapter(client, new ObjectMapper()).find("PEI7954"));
    }

    @Test
    void turnsMalformedPayloadIntoUnavailable() {
        server.stubFor(get(urlPathEqualTo("/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/existePorNumeroRuc"))
                .willReturn(okJson("not-a-boolean")));

        assertThrows(ServiceUnavailableException.class,
                () -> new SriTaxpayerAdapter(client, new ObjectMapper()).exists("1790016919001"));
    }
}
