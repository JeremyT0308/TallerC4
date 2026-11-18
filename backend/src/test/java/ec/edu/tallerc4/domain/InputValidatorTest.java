package ec.edu.tallerc4.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void acceptsKnownValidRuc() {
        assertDoesNotThrow(() -> InputValidator.requireRuc("1790016919001"));
    }

    @Test
    void rejectsRucWithInvalidStructure() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requireRuc("179001691900X"));
        assertEquals("El RUC debe tener 13 dígitos y una estructura ecuatoriana válida.", error.getMessage());
    }

    @Test
    void normalizesAndAcceptsPlate() {
        assertEquals("ABC1234", InputValidator.requirePlate(" abc-1234 "));
    }

    @Test
    void rejectsMalformedEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requireEmail("correo-invalido"));
    }
}
