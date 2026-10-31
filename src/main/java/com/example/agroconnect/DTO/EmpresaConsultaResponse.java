package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Empresa;

public class EmpresaConsultaResponse {
    public EmpresaConsultaResponse() {}

    public EmpresaConsultaResponse(Empresa empresa){
        this.cnpj = empresa.getCnpj();
        this.razaoSocial = empresa.getRazaoSocial();
        this.nomeFantasia = empresa.getNomeFantasia();
        this.inscricaoEstadual = empresa.getInscricaoEstadual();
        this.id = empresa.getId();
        this.status = empresa.getStatus() == null ? "A" : empresa.getStatus();

        this.quantidadeUsuario = empresa.getUsuarios()!=null ? empresa.getUsuarios().size() : 0;
    }
    //size pra retornar 0 caso nao tenha cadastrado
    private Long id;

    private String razaoSocial;
    private String cnpj;
    private String nomeFantasia;

    private String inscricaoEstadual;
    private String status;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    private int quantidadeUsuario;

    public int getQuantidadeUsuario() {
        return quantidadeUsuario;
    }

    public void setQuantidadeUsuario(int quantidadeUsuario) {
        this.quantidadeUsuario = quantidadeUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
