package com.example.agroconnect.controller;


import com.example.agroconnect.DTO.CursoConsultaResponse;
import com.example.agroconnect.DTO.CursoRequest;
import com.example.agroconnect.DTO.CursoResponse;
import com.example.agroconnect.DTO.MatriculaRequest;
import com.example.agroconnect.entities.Curso;
import com.example.agroconnect.repository.CursoRepository;
import com.example.agroconnect.repository.UsuarioRepository;
import com.example.agroconnect.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/matricula")
    public ResponseEntity<CursoResponse> matricular(@RequestBody MatriculaRequest matriculaRequest){
        var  usuarioBanco = usuarioRepository.findById(matriculaRequest.getUsuario_id()).orElse(null);

        var cursoBanco = cursoRepository.findById(matriculaRequest.getCurso_id()).orElse(null);
        if (usuarioBanco == null || cursoBanco == null){
            return ResponseEntity.notFound().build();
        }

        if (cursoBanco.getAlunos().contains(usuarioBanco)){
            throw  new RuntimeException("Aluno ja cadastrado no curso!");
        }

        cursoBanco.adicionarAluno(usuarioBanco);

        cursoRepository.save(cursoBanco);

        return ResponseEntity.ok(new CursoResponse("Cadastrado com sucesso",cursoBanco.getId()));
    }


    @PostMapping
    public ResponseEntity<CursoResponse> cadastroCurso(@RequestBody CursoRequest cursoRequest) {
        Curso cursoBanco = new Curso();

        cursoBanco.setDescricao(cursoRequest.getDescricao());
        cursoBanco.setTitulo(cursoRequest.getTitulo());

        cursoRepository.save(cursoBanco);

        return ResponseEntity.ok(new CursoResponse("Cadastrado com sucesso",cursoBanco.getId()));

    }


    @GetMapping
    public ResponseEntity<List<CursoConsultaResponse>> listarTodos(){


        var cpf = CpfUtils.isValid("11111111111");
        var listaCurso = cursoRepository.findAll()
                .stream()
                .map(CursoConsultaResponse::new).toList();

        return ResponseEntity.ok(listaCurso);
    }
}
