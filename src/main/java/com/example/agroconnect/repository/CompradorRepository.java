package com.example.agroconnect.repository;

import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    List<Comprador> findByCpfouCnpj(String cpfOuCnpj);
}
