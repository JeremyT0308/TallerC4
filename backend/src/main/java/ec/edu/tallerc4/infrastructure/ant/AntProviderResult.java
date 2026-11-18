package ec.edu.tallerc4.infrastructure.ant;

public record AntProviderResult(
        String identification,
        Double points,
        String licenseType,
        boolean noResults,
        boolean simulated,
        String source,
        String note
) { }
