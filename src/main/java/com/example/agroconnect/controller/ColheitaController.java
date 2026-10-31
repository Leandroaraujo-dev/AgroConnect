package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.*;
import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.ColheitaRepository;
import com.example.agroconnect.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
// Define que todos os caminhos da controller
@RequestMapping("/colheita")
@Tag(name = "Colheita Controller", description = "Classe responsável pelo gerenciamento e controle de CRUD das Colheitas (Estoque de Produtos).")
public class ColheitaController {

    //  Faz a "injeção de dependência". repositórios para usarmos o banco.
    @Autowired
    private ColheitaRepository colheitaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    // LISTAR TODAS AS COLHEITAS
    @GetMapping
    @Operation(summary = "Listar todas as colheitas", description = "Busca no banco de dados e retorna todos os registros de colheitas cadastrados no sistema, formatados através do DTO.")
    public List<ColheitaConsultaResponse> consultaColheita() {
        // Vai no banco (PostgreSQL) e traz todos os registros da tabela colheita.
        return colheitaRepository.findAll().stream().map(ColheitaConsultaResponse::new).toList();
    }

    // BUSCAR COLHEITA POR ID ESPECÍFICO
    @GetMapping("/{id}")
    @Operation(summary = "Buscar colheita por ID", description = "Pesquisa uma colheita específica usando o seu ID único. Retorna erro 404 (Not Found) se não for encontrada.")
    public ResponseEntity<ColheitaConsultaResponse> consultaColheitaId(@PathVariable long id) {

        var colheita = colheitaRepository.findById(id).orElse(null);

        if (colheita == null) {
            return ResponseEntity.notFound().build(); // Retorna Erro 404 se não existir.
        }
        return ResponseEntity.ok(new ColheitaConsultaResponse(colheita)); // Retorna Status 200 (OK) e os dados da colheita.
    }

    //  BUSCAR COLHEITAS DE UM PRODUTOR
    @GetMapping("/produtor/{produtorId}")
    @Operation(summary = "Buscar colheitas por Produtor (Usuário)", description = "Retorna uma lista de todas as colheitas vinculadas ao ID de um produtor específico cadastrado no sistema.")
    public List<Colheita> consultaColheitaPorProdutoId(@PathVariable long produtorId) {

        return colheitaRepository.findByUsuarioColheitaId(produtorId);
    }


    //  CADASTRAR NOVA COLHEITA
    @PostMapping
    @Operation(summary = "Cadastrar nova colheita", description = "Cria um novo registro de colheita no sistema. Exige o CPF do produtor no corpo da requisição para vincular a colheita ao seu respectivo dono.")
    public ResponseEntity<ColheitaResponse> cadastrarColheita(@RequestBody ColheitaRequest request) {

        Colheita colheitaBanco = new Colheita();


        colheitaBanco.setNomedeProduto(request.getNomeProduto());
        colheitaBanco.setQuantidadeEstoque(request.getQuantidadeEstoque());
        colheitaBanco.setValorProduto(request.getValorProduto());


        colheitaBanco.setStatus("A");


        Usuario donoColheita = usuarioRepository.getUsuarioByCpf(request.getCpfProdutor()).orElse(null);


        if(donoColheita == null) {
            return ResponseEntity.badRequest().build();
        }


        colheitaBanco.setUsuario(donoColheita);


        Colheita colheitaSalva = colheitaRepository.save(colheitaBanco);

        return ResponseEntity.ok(new ColheitaResponse("Colheita cadastrada com sucesso", colheitaSalva.getId()));
    }


    // ATUALIZAR COLHEITA INTEIRA
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar colheita existente", description = "Substitui os dados completos de uma colheita existente (nome, quantidade em estoque e valor) com base no ID fornecido.")
    public ResponseEntity<ColheitaResponse> atualizarColheita(@PathVariable long id, @RequestBody ColheitaRequest request) {

        Colheita colheitaBanco = colheitaRepository.findById(id).orElse(null);

        if (colheitaBanco != null) {

            colheitaBanco.setNomedeProduto(request.getNomeProduto());
            colheitaBanco.setQuantidadeEstoque(request.getQuantidadeEstoque());
            colheitaBanco.setValorProduto(request.getValorProduto());


            colheitaRepository.save(colheitaBanco);

            return ResponseEntity.ok(new ColheitaResponse("Colheita atualizada com sucesso", colheitaBanco.getId()));
        }

        return ResponseEntity.notFound().build();
    }


    //  ATUALIZAR APENAS O ESTOQUE
    @PatchMapping("/{id}/estoque")
    @Operation(summary = "Atualizar quantidade em estoque", description = "Atualiza parcialmente a colheita, modificando apenas a quantidade disponível em estoque. Ideal para ajustes rápidos.")
    public ResponseEntity<ColheitaResponse> AtualizarEstoque(@PathVariable Long id, @RequestBody AtualizarEstoqueColheitaRequest request) {
        Colheita colheitaBanco = colheitaRepository.findById(id).orElse(null);

        if (colheitaBanco != null) {

            colheitaBanco.setQuantidadeEstoque(request.getQuantidadeEstoque());
            colheitaRepository.save(colheitaBanco);

            return ResponseEntity.ok(new ColheitaResponse("Estoque atualizado com sucesso", colheitaBanco.getId()));
        }

        return ResponseEntity.notFound().build();
    }


    //  EXCLUSÃO LÓGICA (SOFT DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir colheita (Exclusão Lógica)", description = "Altera o status da colheita para 'D' (Deletada/Inativa) em vez de apagá-la do banco, preservando o histórico de pedidos que dependem dela.")
    public ResponseEntity<ColheitaResponse> deletarColheita(@PathVariable Long id) {

        Colheita colheitaBanco = colheitaRepository.findById(id).orElse(null);

        if (colheitaBanco != null) {
            // Em vez de excluir do banco fisicamente (repository.delete), alteramos o status para "D" (Deletado/Inativo).
            // Isso evita quebrar o histórico de pedidos que dependem desta colheita (Soft Delete).
            colheitaBanco.setStatus("D");
            colheitaRepository.save(colheitaBanco);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}