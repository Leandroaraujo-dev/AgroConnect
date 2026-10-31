package com.example.agroconnect.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.agroconnect.DTO.LoginRequest;
import com.example.agroconnect.DTO.LoginResponse;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequestMapping("/login")
@RestController
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${spring.secret}")
    private String secret;


    @Value("${spring.emissor}")
    private String emissor;


    @Value("${spring.tempo}")
    private Long tempo;


    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest){



        if (usuarioRepository.existsUsuarioByCpfAndSenha(loginRequest.getLogin(),
                loginRequest.getSenha())){


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMensagem("Bem vindo! Ao sistema!");


        //JWT Metodo
            Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                        .withIssuer(emissor)
                                .withSubject(loginRequest.getLogin())
                                        .withExpiresAt(gerarDataExpericao())
                                            .sign(algorithm);

        loginResponse.setToken(token);

            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private Instant gerarDataExpericao(){

        //pega data atual
        LocalDateTime dataAtual = LocalDateTime.now();

        // data do futuro
        LocalDateTime dataFuturoSoma = dataAtual.plusMinutes(tempo);


        return dataFuturoSoma.toInstant(ZoneOffset.of("-03:00"));

    }

}
