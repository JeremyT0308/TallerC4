package ec.edu.tallerc4.infrastructure.sri;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(SriProperties.class)
public class SriHttpConfiguration {
    @Bean
    RestClient sriRestClient(SriProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        return RestClient.builder()
                .baseUrl(URI.create(properties.getBaseUrl()).toString())
                .requestFactory(factory)
                .build();
    }
}
