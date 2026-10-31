package com.example.agroconnect.utils;

public class NumberUtils {

    /**
     * Remove todos os caracteres não numéricos de uma string.
     * Ideal para limpar CPFs, CNPJs, CEPs e Telefones.
     *
     * @param texto String original com formatação (ex: "123.456.789-00")
     * @return String contendo apenas os números (ex: "12345678900")
     */
    public static String onlyNumbers(String texto) {
        if (texto == null) {
            return null; // Ou retorne "" (string vazia) se preferir
        }

        // Substitui tudo que NÃO for número por "nada" (vazio)
        return texto.replaceAll("\\D", "");
    }
}