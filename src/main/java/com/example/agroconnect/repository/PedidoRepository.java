package com.example.agroconnect.repository;

import com.example.agroconnect.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatusPagamento (String statusPagamento);
}
