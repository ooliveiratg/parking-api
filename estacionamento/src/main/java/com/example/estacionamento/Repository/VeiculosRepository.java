package com.example.estacionamento.Repository;

import com.example.estacionamento.Entity.Veiculos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VeiculosRepository extends JpaRepository<Veiculos, Integer> {
    List<Veiculos> findByPlaca(String placa);

    @Query("SELECT v FROM Veiculos v WHERE dataSaida IS NULL")
    List<Veiculos> findByDataEqualNull();

    @Query("SELECT v FROM Veiculos v WHERE dataSaida IS NULL AND placa = :placa")
    Optional<Veiculos> findByPlacaActive(@Param("placa") String placa);
}
