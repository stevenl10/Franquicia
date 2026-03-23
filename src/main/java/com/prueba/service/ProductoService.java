package com.prueba.service;

import com.prueba.dto.response.ProductosStockDTO;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Producto;
import com.prueba.model.Sucursal;
import com.prueba.repository.ProductoRepository;
import com.prueba.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;

    public Producto agregar(Long sucursalId, Producto producto) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));
        producto.setSucursal(sucursal);
        return productoRepository.save(producto);
    }

    public void eliminar(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado: " + productoId);
        }
        productoRepository.deleteById(productoId);
    }

    public Producto actualizarStock(Long productoId, int nuevoStock) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + productoId));
        p.setStock(nuevoStock);
        return productoRepository.save(p);
    }

    public Producto actualizarNombre(Long productoId, String nuevoNombre) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + productoId));
        p.setNombre(nuevoNombre);
        return productoRepository.save(p);
    }

    public List<ProductosStockDTO> topStockPorFranquicia(Long franquiciaId) {
        return productoRepository.findTopStockPorFranquicia(franquiciaId)
                .stream()
                .map(p -> new ProductosStockDTO(
                        p.getSucursal().getNombre(),
                        p.getNombre(),
                        p.getStock()
                ))
                .collect(Collectors.toList());
    }
}
