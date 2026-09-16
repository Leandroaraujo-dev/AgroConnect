package com.example.agroconnect.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


// não é apenas um código comum, mas sim uma Tabela real no banco de dados PostgreSQL.
@Entity
public class Colheita {

    //  Define que o atributo abaixo é a Chave Primária (Primary Key) da tabela.
    @Id
    //  Delega ao banco de dados a responsabilidade de gerar e gerenciar esse ID.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atributos da classe. O Spring transformará cada um deles em uma coluna na tabela do banco.
    private String nomedeProduto;
    private Integer quantidadeEstoque;
    private Double valorProduto;
    private String status;


    // RELACIONAMENTO COM A TABELA USUARIO

    //  Traduz-se como "Muitos para Um". Significa que VÁRIAS colheitas
    // podem pertencer a UM único usuário produtor.
    @ManyToOne
    //  Cria fisicamente a coluna da Chave Estrangeira (Foreign Key) no banco de dados.
    // O nome dessa coluna lá no PostgreSQL será "usuario_id".
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioColheita;



    // GETTERS E SETTERS
    // Métodos para encapsulamento: permitem ler (Get) e gravar (Set) os dados privados da entidade.

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

}