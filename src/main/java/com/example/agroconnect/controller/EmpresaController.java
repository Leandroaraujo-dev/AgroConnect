package com.example.agroconnect.controller;


import com.example.agroconnect.DTO.EmpresaConsultaResponse;
import com.example.agroconnect.DTO.EmpresaRequest;
import com.example.agroconnect.DTO.EmpresaResponse;
import com.example.agroconnect.DTO.UsuarioConsultaResponse;
import com.example.agroconnect.entities.Empresa;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.EmpresaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresa")
@Tag(name = "Empresa Controller", description = "Classe responsável pelo controle de CRUD de Empresas (Fazendas/Cooperativas) e gestão de vínculos com usuários.")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping
    @Operation(summary = "Listar todas as empresas", description = "Busca no banco de dados e retorna todos os registros de empresas cadastradas no sistema, formatados através do DTO.")
    public List<EmpresaConsultaResponse> listarTodos() {

        return empresaRepository.findAll().stream().map(EmpresaConsultaResponse::new).toList();
    }


    @PostMapping
    @Operation(summary = "Cadastrar nova empresa", description = "Cria um novo registro de empresa no sistema com dados como Razão Social, Nome Fantasia, CNPJ e Inscrição Estadual.")
    public ResponseEntity<EmpresaResponse> CadastrarEmpresa(@RequestBody EmpresaRequest empresaRequest) {

        Empresa empresaBanco = new Empresa();

        empresaBanco.setRazaoSocial(empresaRequest.getRazaoSocial());
        empresaBanco.setCnpj(empresaRequest.getCnpj());
        empresaBanco.setInscricaoEstadual(empresaRequest.getInscricaoEstadual());
        empresaBanco.setNomeFantasia(empresaRequest.getNomeFantasia());

        empresaRepository.save(empresaBanco);

        EmpresaResponse empresaResponse = new EmpresaResponse();

        empresaResponse.setId(empresaBanco.getId());
        empresaResponse.setMensagem("Cadastrado da empresa realizado com sucesso");

        return ResponseEntity.ok(empresaResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa existente", description = "Substitui os dados completos de uma empresa existente (Razão Social, Nome Fantasia, CNPJ, Inscrição Estadual) com base no ID fornecido.")
    public ResponseEntity<EmpresaResponse> atualizarEmpresa(
            @PathVariable Long id,
            @RequestBody EmpresaRequest empresaRequest) {
        Empresa empresaBanco = empresaRepository.findById(id).orElse(null);

        if (empresaBanco == null) {
            return ResponseEntity.notFound().build();
        }

        empresaBanco.setRazaoSocial(empresaRequest.getRazaoSocial());
        empresaBanco.setNomeFantasia(empresaRequest.getNomeFantasia());
        empresaBanco.setCnpj(empresaRequest.getCnpj());
        empresaBanco.setInscricaoEstadual(empresaRequest.getInscricaoEstadual());
        empresaRepository.save(empresaBanco);

        EmpresaResponse empresaResponse = new EmpresaResponse();
        empresaResponse.setId(empresaBanco.getId());
        empresaResponse.setMensagem("Empresa atualizada com sucesso");
        return ResponseEntity.ok(empresaResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir empresa (Exclusão Física)", description = "Apaga a empresa fisicamente do banco de dados. Possui uma trava de segurança que impede a exclusão caso existam usuários ativos vinculados a esta empresa, retornando um erro 409 (Conflict).")
    public ResponseEntity<EmpresaResponse> excluirEmpresa(@PathVariable Long id) {
        Empresa empresaBanco = empresaRepository.findById(id).orElse(null);

        if (empresaBanco == null) {
            return ResponseEntity.notFound().build();
        }

        if (empresaBanco.getUsuarios() != null && !empresaBanco.getUsuarios().isEmpty()) {
            EmpresaResponse empresaResponse = new EmpresaResponse();
            empresaResponse.setId(empresaBanco.getId());
            empresaResponse.setMensagem("Não é possível excluir uma empresa que possui usuários vinculados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(empresaResponse);
        }

        empresaRepository.delete(empresaBanco);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cnpj/{cnpj}/usuarios")
    @Operation(summary = "Buscar usuários pelo CNPJ da empresa", description = "Pesquisa uma empresa específica pelo seu CNPJ e retorna uma lista formatada de todos os usuários que estão vinculados a ela.")
    public ResponseEntity<List<UsuarioConsultaResponse>> buscarUsuariosCnpjEmpressa(@PathVariable String cnpj){

        var empresaBanco = empresaRepository.getEmpresasByCnpj(cnpj).orElse(null);

        if(empresaBanco ==null)
            return ResponseEntity.notFound().build();

        var usuarioEmpresaBanco = empresaBanco.getUsuarios().stream().map(UsuarioConsultaResponse::new).toList();

        return ResponseEntity.ok(usuarioEmpresaBanco);
    }
}