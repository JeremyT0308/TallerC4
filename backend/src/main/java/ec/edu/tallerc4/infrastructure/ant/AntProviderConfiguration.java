package ec.edu.tallerc4.infrastructure.ant;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AntProperties.class)
public class AntProviderConfiguration {
    @Bean
    @Conditional(SimulatedAntCondition.class)
    SimulatedAntAdapter simulatedAntAdapter(AntProperties properties) {
        return new SimulatedAntAdapter(properties.getSimulationScenario());
    }

    @Bean
    @Conditional(RealAntCondition.class)
    RealAntAdapter realAntAdapter(AntProperties properties) {
        return new RealAntAdapter(RestClient.builder().build(), properties.getRealUrl());
    }
}
