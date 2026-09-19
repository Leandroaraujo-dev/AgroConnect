package com.example.agroconnect.repository;

import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    Optional<Comprador> findByCpfouCnpj(String cpfouCnpj);
}
