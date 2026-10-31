package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.*;
import com.example.agroconnect.entities.Comprador;
import com.example.agroconnect.repository.CompradorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comprador")
@Tag(name = "Comprador Controller", description = "Classe responsável por fazer o controle do CRUD de compradores (clientes).")
public class CompradorController {


    @Autowired
    private CompradorRepository compradorRepository;

    @GetMapping
    @Operation(summary = "Listar todos os compradores", description = "Busca no banco de dados e retorna todos os registros de compradores cadastrados no sistema, formatados através do DTO.")
    public List<CompradorConsultaResponse> consultaComprador() {
        return compradorRepository.findAll().stream().map(CompradorConsultaResponse::new).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar comprador por ID", description = "Pesquisa um comprador específico usando o seu ID único. Retorna erro 404 (Not Found) se não for encontrado.")
    public ResponseEntity<CompradorConsultaResponse> consultaCompradorId(@PathVariable long id) {
        var comprador = compradorRepository.findById(id).orElse(null);
        if (comprador == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new CompradorConsultaResponse(comprador));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo comprador", description = "Cria um novo registro de comprador no sistema com nome, endereço de entrega e documento (CPF/CNPJ). O status inicial é definido como 'A' (Ativo).")
    public ResponseEntity<CompradorResponse> cadastraComprador(@RequestBody CompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        compradorBanco.setNome(request.getNome());
        compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());
        compradorBanco.setCpfouCnpj(request.getCnpjOuCpf());
        compradorBanco.setStatus("A");
        compradorRepository.save(compradorBanco);

        return ResponseEntity.ok(new CompradorResponse("Comprador cadastrado com sucesso", compradorBanco.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar comprador existente", description = "Substitui os dados completos de um comprador existente (nome, endereço de entrega e documento) com base no ID fornecido.")
    public ResponseEntity<CompradorResponse> atualizarComprador(@PathVariable Long id, @RequestBody CompradorRequest request) {

        Comprador compradorBanco = compradorRepository.findById(id).orElse(null);

        if (compradorBanco != null) {
            compradorBanco.setNome(request.getNome());
            compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());
            compradorBanco.setCpfouCnpj(request.getCnpjOuCpf());

            compradorRepository.save(compradorBanco);
            return ResponseEntity.ok(new CompradorResponse("Comprador atualizado com sucesso", compradorBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/endereco")
    @Operation(summary = "Atualizar apenas o endereço", description = "Atualiza parcialmente o comprador, modificando especificamente o seu endereço de entrega. Ideal para ajustes rápidos sem precisar enviar todos os dados novamente.")
    public ResponseEntity<CompradorResponse> atualizarEndereco(@PathVariable Long id, @RequestBody AtualizarEnderecoCompradorRequest request) {

        Comprador compradorBanco = compradorRepository.findById(id).orElse(null);

        if (compradorBanco != null) {
            compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());

            compradorRepository.save(compradorBanco);
            return ResponseEntity.ok(new CompradorResponse("Endereço atualizado com sucesso", compradorBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir comprador (Exclusão Lógica)", description = "Altera o status do comprador para 'D' (Deletado/Inativo) em vez de apagá-lo do banco, preservando o histórico de pedidos vinculados a ele.")
    public ResponseEntity<CompradorResponse> AtualizarStatus(@PathVariable Long id) {

        Comprador compradorBanco = compradorRepository.findById(id).orElse(null);

        if (compradorBanco != null) {
            compradorBanco.setStatus("D");
            compradorRepository.save(compradorBanco);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}