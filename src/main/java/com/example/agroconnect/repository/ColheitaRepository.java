package com.example.agroconnect.repository;

import com.example.agroconnect.entities.Colheita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColheitaRepository extends JpaRepository<Colheita,Long> {

    List<Colheita> findByUsuarioColheitaId(Long usuarioId);
    Optional<Colheita> findByNomedeProduto(String nomedeProduto);
}
