package ec.edu.tallerc4.infrastructure.ant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ant")
public class AntProperties {
    private String provider = "simulated";
    private String simulationScenario = "success";
    private String realUrl = "https://consultaweb.ant.gob.ec/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp";

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getSimulationScenario() { return simulationScenario; }
    public void setSimulationScenario(String simulationScenario) { this.simulationScenario = simulationScenario; }
    public String getRealUrl() { return realUrl; }
    public void setRealUrl(String realUrl) { this.realUrl = realUrl; }
}
