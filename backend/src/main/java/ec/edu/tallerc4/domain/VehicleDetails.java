package ec.edu.tallerc4.domain;

public record VehicleDetails(
        String plate,
        String brand,
        String model,
        Integer modelYear,
        int paymentCount
) { }
