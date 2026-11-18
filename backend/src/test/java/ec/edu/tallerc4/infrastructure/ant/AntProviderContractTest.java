package ec.edu.tallerc4.infrastructure.ant;

import com.github.tomakehurst.wiremock.WireMockServer;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class AntProviderContractTest {
    private WireMockServer server;

    @BeforeEach
    void startServer() { server = new WireMockServer(0); server.start(); }

    @AfterEach
    void stopServer() { server.stop(); }

    @Test
    void simulatedSuccessHasExplicitSimulatedProvenance() {
        AntProviderResult result = new SimulatedAntAdapter("success").queryPoints("0000000011");
        assertEquals(30.0, result.points());
        assertTrue(result.simulated());
        assertEquals("ANT_SIMULATED", result.source());
    }

    @Test
    void simulatedNoResultsIsNotAnErrorButHasNoData() {
        AntProviderResult result = new SimulatedAntAdapter("no-results").queryPoints("0000000022");
        assertTrue(result.noResults());
        assertNull(result.points());
    }

    @Test
    void simulatedFailureCasesAreUnavailable() {
        assertThrows(ServiceUnavailableException.class, () -> new SimulatedAntAdapter("bad-gateway").queryPoints("0000000033"));
        assertThrows(ServiceUnavailableException.class, () -> new SimulatedAntAdapter("timeout").queryPoints("0000000044"));
        assertThrows(ServiceUnavailableException.class, () -> new SimulatedAntAdapter("invalid-response").queryPoints("0000000055"));
    }

    @Test
    void realAdapterMapsPointsFromOfficialPortalShape() {
        server.stubFor(get(urlPathEqualTo("/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/html")
                        .withBody("<html><body>LICENCIA TIPO: B Puntos: 28,5</body></html>")));

        AntProviderResult result = new RealAntAdapter(RestClient.builder().build(), server.baseUrl() + "/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp")
                .queryPoints("0912576287");

        assertEquals(28.5, result.points());
        assertFalse(result.simulated());
        assertEquals("ANT", result.source());
    }

    @Test
    void realAdapterTreatsUnauthorizedAndUnexpectedHtmlAsUnavailable() {
        server.stubFor(get(urlPathEqualTo("/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp"))
                .willReturn(aResponse().withStatus(401)));
        RealAntAdapter adapter = new RealAntAdapter(RestClient.builder().build(), server.baseUrl() + "/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp");
        assertThrows(ServiceUnavailableException.class, () -> adapter.queryPoints("0912576287"));

        server.resetAll();
        server.stubFor(get(urlPathEqualTo("/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/html")
                        .withBody("<html><body>Acceso sin puntos</body></html>")));
        assertThrows(ServiceUnavailableException.class, () -> adapter.queryPoints("0912576287"));
    }
}
