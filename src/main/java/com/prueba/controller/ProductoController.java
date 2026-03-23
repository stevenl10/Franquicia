package com.prueba.controller;

import com.prueba.dto.request.ActualizarNombreDTO;
import com.prueba.dto.request.ActualizarStockDTO;
import com.prueba.dto.response.ProductoDTO;
import com.prueba.dto.response.ProductosStockDTO;
import com.prueba.dto.response.ResponseMensajeDTO;
import com.prueba.mapper.FranquiciaMapper;
import com.prueba.model.Producto;
import com.prueba.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping("/sucursales/{sucursalId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO agregar(@PathVariable Long sucursalId, @RequestBody Producto producto) {
        return FranquiciaMapper.toDTO(productoService.agregar(sucursalId, producto));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseMensajeDTO eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return new ResponseMensajeDTO("Producto con id " + id + " eliminado correctamente");
    }

    @PatchMapping("/productos/{id}/stock")
    public ProductoDTO actualizarStock(@PathVariable Long id, @RequestBody ActualizarStockDTO dto) {
        return FranquiciaMapper.toDTO(productoService.actualizarStock(id, dto.getStock()));
    }

    @PatchMapping("/productos/{id}/nombre")
    public ProductoDTO actualizarNombre(@PathVariable Long id, @RequestBody ActualizarNombreDTO dto) {
        return FranquiciaMapper.toDTO(productoService.actualizarNombre(id, dto.getNombre()));
    }

    @GetMapping("/franquicias/{franquiciaId}/productos/top-stock")
    public List<ProductosStockDTO> topStock(@PathVariable Long franquiciaId) {
        return productoService.topStockPorFranquicia(franquiciaId);
    }
}
