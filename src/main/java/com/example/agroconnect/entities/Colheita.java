package com.example.agroconnect.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Colheita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomedeProduto;

    private Integer quantidadeEstoque;

    private Double valorProduto;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioColheita;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomedeProduto(){
        return this.nomedeProduto;
    }

    public void setNomedeProduto(String nomedeProduto){
        this.nomedeProduto = nomedeProduto;
    }

    public Integer getQuantidadeEstoque(){
        return this.quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque){
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Usuario getUsuarioColheita(){
        return this.usuarioColheita;
    }

    public void setUsuario(Usuario usuario){

        this.usuarioColheita = usuario;
    }

    public Double getValorProduto() {
        return this.valorProduto;
    }

    public void setValorProduto(Double valorProduto) {
        this.valorProduto = valorProduto;
    }

    public void setProdutorRural(String produtorRural) {

    }
}