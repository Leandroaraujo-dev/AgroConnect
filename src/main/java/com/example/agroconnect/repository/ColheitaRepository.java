package com.example.agroconnect.repository;

import com.example.agroconnect.entities.Colheita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColheitaRepository extends JpaRepository<Colheita,Long> {

    List<Colheita> findByUsuarioColheitaId(Long usuarioId);
}
