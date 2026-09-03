package com.example.agroconnect.entities;

public class Comprador {

    private Long id;

    private String nome;

    private String CpfouCnpj;

    private String enderecoEntrega;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfouCnpj() {
        return this.CpfouCnpj;
    }

    public void setCpfouCnpj(String cpfouCnpj) {
        CpfouCnpj = cpfouCnpj;
    }

    public String getEnderecoEntrega() {
        return this.enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

}
