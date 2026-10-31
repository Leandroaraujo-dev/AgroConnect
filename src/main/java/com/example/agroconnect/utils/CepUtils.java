package com.example.agroconnect.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CepUtils {
    // Instância única para processar o JSON retornado pela API
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Busca o endereço no ViaCEP e retorna uma String formatada.
     *
     * @param cep CEP com ou sem formatação (ex: 01001-000)
     * @return String contendo "Logradouro, Bairro, Cidade - UF"
     */
    public static String getEnderecoCompleto(String cep) {
        // Limpa a string usando a nossa classe utilitária
        cep = NumberUtils.onlyNumbers(cep);

        if (cep == null || cep.length() != 8) {
            throw new IllegalArgumentException("CEP inválido. O CEP deve conter exatamente 8 dígitos.");
        }

        try {
            // 1. Cria o cliente HTTP nativo do Java
            HttpClient client = HttpClient.newHttpClient();

            // 2. Monta a requisição para a URL do ViaCEP
            String url = "https://viacep.com.br/ws/" + cep + "/json/";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // 3. Dispara a requisição e guarda a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Lê o JSON de resposta
            JsonNode jsonNode = objectMapper.readTree(response.body());

            // Se o CEP tiver 8 números mas não existir, o ViaCEP retorna { "erro": true }
            if (jsonNode.has("erro") && jsonNode.get("erro").asBoolean()) {
                throw new IllegalArgumentException("CEP não encontrado na base dos Correios.");
            }

            // 5. Extrai os dados do JSON
            String logradouro = jsonNode.get("logradouro").asText("");
            String bairro = jsonNode.get("bairro").asText("");
            String localidade = jsonNode.get("localidade").asText(""); // Cidade
            String uf = jsonNode.get("uf").asText("");

            // 6. Formata e retorna a String final
            // Exemplo: "Praça da Sé, Sé, São Paulo - SP"
            return String.format("%s, %s, %s - %s", logradouro, bairro, localidade, uf);

        } catch (IllegalArgumentException e) {
            throw e; // Repassa a mensagem de validação
        } catch (Exception e) {
            // Cai aqui se a API estiver fora do ar ou sem internet
            throw new RuntimeException("Erro inesperado ao consultar o ViaCEP: " + e.getMessage(), e);
        }
    }
}