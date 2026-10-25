package com.cresol.desafio_tecnico.util;

public final class DigitoVerificadorConta {

    private DigitoVerificadorConta() {
    }

    /** Módulo 11, pesos 2..9 cíclicos, da direita para a esquerda. Retorna 0..9. */
    public static int calcular(long numeroConta) {
        String digitos = Long.toString(numeroConta);
        int soma = 0;
        int peso = 2;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            soma += (digitos.charAt(i) - '0') * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    public static boolean isValido(long numeroConta, int digito) {
        return calcular(numeroConta) == digito;
    }
}