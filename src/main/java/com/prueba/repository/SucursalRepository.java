package com.prueba.repository;

import com.prueba.model.Sucursal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findByFranquiciaId(Long franquiciaId);
}