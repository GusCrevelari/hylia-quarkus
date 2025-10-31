package br.com.fiap.hylia.domain.util;

import java.util.regex.Pattern;


// Validadores foram criados em uma unica classe, sendo importada em outras classes

public final class Validators {
    private Validators() {
    }

    //regex para email e crm...
    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern CRM_RE = Pattern.compile("^[A-Z]{2,}-\\d{3,}$"); // e.g., CRM-1234

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_RE.matcher(email).matches();
    }

    public static boolean isValidCrm(String crm) {
        return crm != null && CRM_RE.matcher(crm).matches();
    }

    public static boolean isValidUf(String uf) {
        return uf != null && uf.matches("^[A-Za-z]{2}$");
    }


    public static String normalizeSpaces(String s, String fieldName) {
        if (s == null) throw new IllegalArgumentException(fieldName + " is required");
        String out = s.trim().replaceAll("\\s+", " ");
        if (out.isEmpty()) throw new IllegalArgumentException(fieldName + " is required");
        return out;
    }

    public static String sanitizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    public static boolean isValidCpf(String raw) {
        String cpf = sanitizeCpf(raw);
        if (cpf == null || cpf.length() != 11) return false;
        if (cpf.chars().distinct().count() == 1) return false;
        return digit(cpf, 10) == (cpf.charAt(9) - '0') && digit(cpf, 11) == (cpf.charAt(10) - '0');
    }

    private static int digit(String cpf, int w) {
        int s = 0, k = w;
        for (int i = 0; i < w - 1; i++) s += (cpf.charAt(i) - '0') * (k--);
        int r = 11 - (s % 11);
        return (r >= 10) ? 0 : r;
    }
}
