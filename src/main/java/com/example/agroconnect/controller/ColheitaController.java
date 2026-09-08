package com.example.agroconnect.controller;


import com.example.agroconnect.DTO.AtualizarEstoqueColheitaRequest;
import com.example.agroconnect.DTO.ColheitaRequest;
import com.example.agroconnect.DTO.ColheitaResponse;
import com.example.agroconnect.entities.Colheita;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Colheita")

public class ColheitaController{
    @GetMapping
public String consultaColheita(){
    return "Pagina colheita";
    }

    @GetMapping("/{id}")
    public Colheita consultaColheitaId(@PathVariable long id){
        Colheita colheita = new Colheita();

        colheita.setId(id);
        colheita.setNomedeProduto("Milho");
        colheita.setQuantidadeEstoque(500);
        colheita.setValorProduto(45.50);
        return colheita;
    }
    @GetMapping("/produtor/{produtorId}")
    public Colheita consultaColheitaPorProdutoId(@PathVariable long produtorId){
        Colheita colheita = new Colheita();

        colheita.setId(1L);
        colheita.setNomedeProduto("Soja");
        colheita.setQuantidadeEstoque(200);
        colheita.setValorProduto(120.0);
        return colheita;
    }

    @PostMapping
    public ResponseEntity<ColheitaResponse> cadastrarColheita(@RequestBody ColheitaRequest colheitaRequest){

        Colheita colheitaBanco = new Colheita();

         colheitaBanco.setNomedeProduto(colheitaRequest.getNomeProduto());
         colheitaBanco.setQuantidadeEstoque(colheitaBanco.getQuantidadeEstoque());
         colheitaBanco.setValorProduto(colheitaBanco.getValorProduto());

         return ResponseEntity.ok(new ColheitaResponse("Colheita cadastrada com sucesso", colheitaBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColheitaResponse> atualizarColheita(@PathVariable long id, @RequestBody ColheitaRequest colheitaRequest){
        Colheita colheitaBanco = new Colheita();
        colheitaBanco.setId(id);

        if (colheitaBanco != null) {
            colheitaBanco.setNomedeProduto(colheitaRequest.getNomeProduto());
            colheitaBanco.setQuantidadeEstoque(colheitaRequest.getQuantidadeEstoque());
            colheitaBanco.setValorProduto(colheitaRequest.getValorProduto());
            colheitaBanco.setProdutorRural(colheitaRequest.getProdutorRural());

            return ResponseEntity.ok(new ColheitaResponse("Colheita atualizada com sucesso", colheitaBanco.getId()));
        }

        return ResponseEntity.notFound().build();
    }
    @PatchMapping("/{id}/estoque")
    public ResponseEntity<ColheitaResponse> AtualizarEstoque(@PathVariable Long id, @RequestBody AtualizarEstoqueColheitaRequest request) {

        // consulta no banco (simulação)
        Colheita colheitaBanco = new Colheita();
        colheitaBanco.setId(id);

        if (colheitaBanco != null) {
            colheitaBanco.setQuantidadeEstoque(request.getQuantidadeEstoque());

            return ResponseEntity.ok(new ColheitaResponse("Estoque atualizado com sucesso", colheitaBanco.getId()));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ColheitaResponse> DeletarColheita(@PathVariable Long id) {

        Colheita colheitaBanco = new Colheita();

        if (colheitaBanco != null) {

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    }


