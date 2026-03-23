package com.prueba.repository;


import com.prueba.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findBySucursalId(Long sucursalId);

    // Query para obtener el producto con más stock por sucursal de una franquicia
    @Query("""
                SELECT p FROM Producto p
                WHERE p.sucursal.franquicia.id = :franquiciaId
                AND p.stock = (
                    SELECT MAX(p2.stock) FROM Producto p2
                    WHERE p2.sucursal.id = p.sucursal.id
                    AND p2.sucursal.franquicia.id = :franquiciaId
                )
                ORDER BY p.sucursal.id
            """)
    List<Producto> findTopStockPorFranquicia(Long franquiciaId);
}