package com.example.agroconnect.controller;


import com.example.agroconnect.DTO.AtualizarEstoqueColheitaRequest;
import com.example.agroconnect.DTO.ColheitaRequest;
import com.example.agroconnect.DTO.ColheitaResponse;
import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.ColheitaRepository;
import com.example.agroconnect.repository.UsuarioRepository;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Colheita")
public class ColheitaController{

    @Autowired
    private ColheitaRepository colheitaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
public List<Colheita> consultaColheita()
    {
    return colheitaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colheita> consultaColheitaId(@PathVariable long id){
        var colheita = colheitaRepository.findById(id).orElse(null);
        if (colheita==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(colheita);
    }

    @GetMapping("/produtor/{produtorId}")
    public List<Colheita> consultaColheitaPorProdutoId(@PathVariable long produtorId) {
        return colheitaRepository.findByUsuarioColheitaId(produtorId);
    }

    @PostMapping
    public ResponseEntity<ColheitaResponse> cadastrarColheita(@RequestBody ColheitaRequest request){

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

         colheitaRepository.save(colheitaBanco);

         return ResponseEntity.ok(new ColheitaResponse("Colheita cadastrada com sucesso", colheitaBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColheitaResponse> atualizarColheita(@PathVariable long id, @RequestBody ColheitaRequest request){
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ColheitaResponse> deletarColheita(@PathVariable Long id) {

        Colheita colheitaBanco = colheitaRepository.findById(id).orElse(null);

        if (colheitaBanco != null) {
            colheitaBanco.setStatus("D");
            colheitaRepository.save(colheitaBanco);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }}

