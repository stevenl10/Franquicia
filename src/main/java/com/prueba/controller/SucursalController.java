package com.prueba.controller;

import com.prueba.dto.request.ActualizarNombreDTO;
import com.prueba.dto.response.SucursalDTO;
import com.prueba.mapper.FranquiciaMapper;
import com.prueba.model.Sucursal;
import com.prueba.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/franquicias")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping("/{franquiciaId}/sucursales")
    @ResponseStatus(HttpStatus.CREATED)
    public SucursalDTO agregar(@PathVariable Long franquiciaId, @RequestBody Sucursal sucursal) {
        return FranquiciaMapper.toDTO(sucursalService.agregar(franquiciaId, sucursal));
    }

    @PatchMapping("/sucursales/{id}/nombre")
    public SucursalDTO actualizarNombre(@PathVariable Long id, @RequestBody ActualizarNombreDTO dto) {
        return FranquiciaMapper.toDTO(sucursalService.actualizarNombre(id, dto.getNombre()));
    }
}
