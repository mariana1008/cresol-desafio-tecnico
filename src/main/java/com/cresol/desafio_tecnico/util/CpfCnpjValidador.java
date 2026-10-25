package com.cresol.desafio_tecnico.util;

public final class CpfCnpjValidador {

    private static final int[] PESOS_CNPJ_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private CpfCnpjValidador() {
    }

    /** Remove máscara (. / -) e padroniza em maiúsculas. */
    public static String normalizar(String documento) {
        return documento == null ? null : documento.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }

    public static boolean isValido(String documento) {
        String d = normalizar(documento);
        if (d == null) {
            return false;
        }
        return switch (d.length()) {
            case 11 -> cpfValido(d);
            case 14 -> cnpjValido(d);
            default -> false;
        };
    }

    private static boolean cpfValido(String cpf) {
        if (!cpf.matches("\\d{11}") || todosIguais(cpf)) {
            return false;
        }
        int dv1 = digitoCpf(cpf, 9, 10);
        int dv2 = digitoCpf(cpf, 10, 11);
        return dv1 == valor(cpf.charAt(9)) && dv2 == valor(cpf.charAt(10));
    }

    private static int digitoCpf(String cpf, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += valor(cpf.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    // Aceita CNPJ numérico e o alfanumérico (12 primeiros caracteres A-Z/0-9 + 2 DVs numéricos)
    private static boolean cnpjValido(String cnpj) {
        if (!cnpj.matches("[A-Z0-9]{12}\\d{2}") || todosIguais(cnpj)) {
            return false;
        }
        int dv1 = digitoCnpj(cnpj, PESOS_CNPJ_1);
        int dv2 = digitoCnpj(cnpj, PESOS_CNPJ_2);
        return dv1 == valor(cnpj.charAt(12)) && dv2 == valor(cnpj.charAt(13));
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += valor(cnpj.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /** Valor do caractere: '0'-'9' = 0-9; 'A'-'Z' = 17-42 (código ASCII - 48), regra do CNPJ alfanumérico. */
    private static int valor(char c) {
        return c - '0';
    }

    private static boolean todosIguais(String s) {
        return s.chars().distinct().count() == 1;
    }
}