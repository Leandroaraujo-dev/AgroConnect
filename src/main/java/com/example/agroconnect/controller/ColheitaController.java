package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizarEstoqueColheitaRequest;
import com.example.agroconnect.DTO.ColheitaRequest;
import com.example.agroconnect.DTO.ColheitaResponse;
import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.ColheitaRepository;
import com.example.agroconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
// Define que todos os caminhos da controller
@RequestMapping("/Colheita")
public class ColheitaController {

    //  Faz a "injeção de dependência". repositórios para usarmos o banco.
    @Autowired
    private ColheitaRepository colheitaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    // LISTAR TODAS AS COLHEITAS
    @GetMapping
    public List<Colheita> consultaColheita() {
        // Vai no banco (PostgreSQL) e traz todos os registros da tabela colheita.
        return colheitaRepository.findAll();
    }

    // BUSCAR COLHEITA POR ID ESPECÍFICO
    @GetMapping("/{id}")
    public ResponseEntity<Colheita> consultaColheitaId(@PathVariable long id) {

        var colheita = colheitaRepository.findById(id).orElse(null);

        if (colheita == null) {
            return ResponseEntity.notFound().build(); // Retorna Erro 404 se não existir.
        }
        return ResponseEntity.ok(colheita); // Retorna Status 200 (OK) e os dados da colheita.
    }

    //  BUSCAR COLHEITAS DE UM PRODUTOR
    @GetMapping("/produtor/{produtorId}")
    public List<Colheita> consultaColheitaPorProdutoId(@PathVariable long produtorId) {

        return colheitaRepository.findByUsuarioColheitaId(produtorId);
    }


    //  CADASTRAR NOVA COLHEITA
    @PostMapping
    public ResponseEntity<ColheitaResponse> cadastrarColheita(@RequestBody ColheitaRequest request) {

        Colheita colheitaBanco = new Colheita();


        colheitaBanco.setNomedeProduto(request.getNomeProduto());
        colheitaBanco.setQuantidadeEstoque(request.getQuantidadeEstoque());
        colheitaBanco.setValorProduto(request.getValorProduto());


        colheitaBanco.setStatus("A");


        Usuario donoColheita = usuarioRepository.findById(request.getUsuarioId()).orElse(null);


        if(donoColheita == null) {
            return ResponseEntity.badRequest().build();
        }


        colheitaBanco.setUsuario(donoColheita);


        Colheita colheitaSalva = colheitaRepository.save(colheitaBanco);

        return ResponseEntity.ok(new ColheitaResponse("Colheita cadastrada com sucesso", colheitaSalva.getId()));
    }


    // ATUALIZAR COLHEITA INTEIRA
    @PutMapping("/{id}")
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