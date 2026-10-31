package com.example.agroconnect.utils;

public class CnpjUtils {


    // Arrays com os pesos para calcular os dígitos verificadores
    private static final int[] PESO_DIGITO_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESO_DIGITO_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean isValid(String cnpj) {
        // Limpa a string usando nossa classe de utilitários
        cnpj = NumberUtils.onlyNumbers(cnpj);

        if (cnpj == null || cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            // Cálculo do 1º dígito verificador
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += (cnpj.charAt(i) - '0') * PESO_DIGITO_1[i];
            }

            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : (11 - resto);

            if (digito1 != (cnpj.charAt(12) - '0')) {
                return false;
            }

            // Cálculo do 2º dígito verificador
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += (cnpj.charAt(i) - '0') * PESO_DIGITO_2[i];
            }

            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : (11 - resto);

            return digito2 == (cnpj.charAt(13) - '0');

        } catch (Exception e) {
            return false;
        }
    }
}

