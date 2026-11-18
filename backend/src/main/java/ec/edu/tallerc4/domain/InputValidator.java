package ec.edu.tallerc4.domain;

import java.util.Locale;
import java.util.regex.Pattern;

public final class InputValidator {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern PLATE = Pattern.compile("^[A-Z0-9]{3,8}$");

    private InputValidator() { }

    public static String requireEmail(String value) {
        if (value == null || !EMAIL.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public static String requireRuc(String value) {
        if (!isValidRuc(value)) {
            throw new IllegalArgumentException("El RUC debe tener 13 dígitos y una estructura ecuatoriana válida.");
        }
        return value;
    }

    public static String requirePlate(String value) {
        String normalized = value == null ? "" : value.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
        if (!PLATE.matcher(normalized).matches()) {
            throw new IllegalArgumentException("La placa debe contener entre 3 y 8 caracteres alfanuméricos.");
        }
        return normalized;
    }

    public static boolean isValidRuc(String value) {
        if (value == null || !value.matches("\\d{13}") || !validProvince(value.substring(0, 2))
                || value.chars().allMatch(ch -> ch == '0')) {
            return false;
        }
        return true;
    }

    private static boolean validProvince(String province) {
        int code = Integer.parseInt(province);
        return (code >= 1 && code <= 24) || code == 30;
    }
}
