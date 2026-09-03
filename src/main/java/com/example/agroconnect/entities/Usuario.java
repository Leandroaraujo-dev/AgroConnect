package com.example.agroconnect.entities;

import java.time.LocalDateTime;

public class Usuario {

    public Usuario(){
        this.id = 1L;

    }

    public Usuario(String nome, String cpf, String dataNascimento){
        this.id = 1000L;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;

    }

    private Long id;

    private String nome;

    private String cpf;

    private String dataNascimento;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getId(){
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }
    public void setnome(String nome){
        this.nome = nome;
    }
    public String getCpf(){
        return this.cpf;
    }

    public void setCpf(String cpf){
        this.cpf = cpf;
    }

    public String getDataNascimento(){
        return this.dataNascimento;
    }

    public void  setDataNascimento(String dataNascimento){
        this.dataNascimento = dataNascimento;
    }
}

