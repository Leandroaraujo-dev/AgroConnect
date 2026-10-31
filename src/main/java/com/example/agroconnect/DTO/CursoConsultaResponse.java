package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Curso;

import java.util.List;

public class CursoConsultaResponse {
    public CursoConsultaResponse() {
    }

    public CursoConsultaResponse(Curso curso) {
        this.id = curso.getId();
        this.descricao = curso.getDescricao();
        this.titulo = curso.getTitulo();
        this.usuarios = curso.getAlunos().stream().map(UsuarioConsultaResponse::new).toList();
    }

    private Long id;
    private String titulo;
    private String descricao;

    private List<UsuarioConsultaResponse> usuarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<UsuarioConsultaResponse> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioConsultaResponse> usuarios) {
        this.usuarios = usuarios;
    }
}
