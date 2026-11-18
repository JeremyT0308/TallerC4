package ec.edu.tallerc4.infrastructure.ant;

import ec.edu.tallerc4.application.AntProvider;
import ec.edu.tallerc4.application.BadGatewayException;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealAntAdapter implements AntProvider {
    private static final Pattern POINTS = Pattern.compile("Puntos\\s*:\\s*(?:<[^>]+>\\s*)*([0-9]+(?:[.,][0-9]+)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LICENSE = Pattern.compile("LICENCIA\\s+TIPO:\\s*(?:<[^>]+>\\s*)*([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);

    private final RestClient client;
    private final String queryUrl;

    public RealAntAdapter(RestClient client, String queryUrl) {
        this.client = client;
        this.queryUrl = queryUrl;
    }

    @Override
    public AntProviderResult queryPoints(String identification) {
        try {
            String body = client.get().uri(queryUrl + "?ps_identificacion=" + identification + "&ps_tipo_identificacion=CED")
                    .retrieve().body(String.class);
            if (body == null) throw new ServiceUnavailableException("ANT devolvió una respuesta vacía.");
            String lower = body.toLowerCase(Locale.ROOT);
            if (lower.contains("sin registros") || lower.contains("no se encontraron")) {
                return new AntProviderResult(identification, null, null, true, false, "ANT", "ANT no reportó puntos para la identificación.");
            }
            Matcher points = POINTS.matcher(body);
            if (!points.find()) throw new ServiceUnavailableException("ANT devolvió HTML sin un campo de puntos verificable.");
            double value = Double.parseDouble(points.group(1).replace(',', '.'));
            Matcher license = LICENSE.matcher(body);
            return new AntProviderResult(identification, value, license.find() ? license.group(1) : null,
                    false, false, "ANT", null);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 502) {
                throw new BadGatewayException("El portal ANT devolvió HTTP 502 Bad Gateway.", ex);
            }
            throw new ServiceUnavailableException("El portal ANT no está disponible o requiere autorización.", ex);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("El portal ANT no está disponible o requiere autorización.", ex);
        }
    }
}
