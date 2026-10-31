package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Comprador;

public class CompradorConsultaResponse {


    public CompradorConsultaResponse(){}
    public CompradorConsultaResponse(Comprador comprador){
        this.cnpjOuCpf = comprador.getCpfouCnpj();
        this.nome = comprador.getNome();
        this.enderecoEntrega = comprador.getEnderecoEntrega();
        this.id = comprador.getId();
        this.status = comprador.getStatus();

    }

    private String nome;
    private String cnpjOuCpf;
    private String enderecoEntrega;

    private Long id;
    private String status;
    public String getNome() {
        return nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getCnpjOuCpf() {
        return cnpjOuCpf;
    }

    public void setCnpjOuCpf(String cnpjOuCpf) {
        this.cnpjOuCpf = cnpjOuCpf;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
