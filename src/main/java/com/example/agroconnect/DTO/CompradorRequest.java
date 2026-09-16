package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Comprador;

public class CompradorRequest {

    public CompradorRequest(){}

    private String nome;
    private String cnpjOuCpf;
    private String enderecoEntrega;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpjOuCpf() {
        return cnpjOuCpf;
    }

    public void setCnpjOuCpf(String cnpjOuCpf) {
        this.cnpjOuCpf = cnpjOuCpf;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }
}
