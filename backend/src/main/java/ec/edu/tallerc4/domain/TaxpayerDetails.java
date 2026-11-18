package ec.edu.tallerc4.domain;

public record TaxpayerDetails(
        String ruc,
        String legalName,
        String status,
        String taxpayerType,
        String regime,
        String mainEconomicActivity,
        String category
) { }
