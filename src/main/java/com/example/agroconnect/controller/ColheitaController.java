package com.example.agroconnect.controller;


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
    public Colheita consultaColheitaPorid(@PathVariable long id){
        Colheita colheita = new Colheita();

        colheita.setId(id);
        colheita.setNomedeProduto("Milho");
        colheita.setQuantidadeEstoque(500);
        colheita.setValorProduto(45.50);
        return colheita;
    }
    @GetMapping("/produtor/{produtorId}")
    public Colheita consultaColheitaPorProduto(@PathVariable long produtorId){
        Colheita colheita = new Colheita();

        colheita.setId(1L);
        colheita.setNomedeProduto("Soja");
        colheita.setQuantidadeEstoque(200);
        colheita.setValorProduto(120.0);
        return colheita;
    }

    @PostMapping
    public ResponseEntity<Colheita> cadastroColheita(@RequestBody Colheita colheitaRequest){
            return ResponseEntity.ok(colheitaRequest);
    }
}


