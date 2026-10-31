package com.example.agroconnect.utils;

public class CpfUtils {


    /**
     * Valida se um CPF é válido segundo o algoritmo da Receita Federal.
     *
     * @param cpf CPF com ou sem pontuação.
     * @return true se o CPF for válido, false caso contrário.
     */
    public static boolean isValid(String cpf) {
        cpf = NumberUtils.onlyNumbers(cpf);
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        // Verifica o tamanho e se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Cálculo do 1º dígito verificador
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }

            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : (11 - resto);

            if (digito1 != (cpf.charAt(9) - '0')) {
                return false;
            }

            // Cálculo do 2º dígito verificador
            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }

            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : (11 - resto);

            return digito2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }
}