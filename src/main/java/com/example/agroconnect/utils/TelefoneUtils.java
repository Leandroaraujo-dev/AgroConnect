package com.example.agroconnect.utils;

public class TelefoneUtils {

    /**
     * Valida números de telefone fixo (10 dígitos) ou celular (11 dígitos) do Brasil.
     */
    public static boolean isValid(String telefone) {
        telefone = NumberUtils.onlyNumbers(telefone);

        if (telefone == null) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 11999999999, 0000000000)
        if (telefone.matches("(\\d)\\1+")) {
            return false;
        }

        // Fixo no Brasil: DDD (2) + Número (8) = 10 dígitos
        if (telefone.length() == 10) {
            // Opcional: Telefones fixos normalmente começam com números de 2 a 5
            char primeiroDigitoNumero = telefone.charAt(2);
            return primeiroDigitoNumero >= '2' && primeiroDigitoNumero <= '5';
        }
        // Celular no Brasil: DDD (2) + 9 + Número (8) = 11 dígitos
        else if (telefone.length() == 11) {
            // Regra da Anatel: celulares têm 11 dígitos e devem começar com o 9 logo após o DDD
            return telefone.charAt(2) == '9';
        }

        return false; // Se não tiver 10 nem 11 dígitos, é inválido
    }
}

