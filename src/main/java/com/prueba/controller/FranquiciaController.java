package com.prueba.controller;

import com.prueba.dto.request.ActualizarNombreDTO;
import com.prueba.dto.response.FranquiciaDTO;
import com.prueba.mapper.FranquiciaMapper;
import com.prueba.model.Franquicia;
import com.prueba.service.FranquiciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/franquicias")
@RequiredArgsConstructor
public class FranquiciaController {

    private final FranquiciaService franquiciaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FranquiciaDTO agregar(@RequestBody Franquicia franquicia) {
        return FranquiciaMapper.toDTO(franquiciaService.agregar(franquicia));
    }

    @PatchMapping("/{id}/nombre")
    public FranquiciaDTO actualizarNombre(@PathVariable Long id, @RequestBody ActualizarNombreDTO dto) {
        return FranquiciaMapper.toDTO(franquiciaService.actualizarNombre(id, dto.getNombre()));
    }
    @GetMapping
    public List<FranquiciaDTO> listar() {
        return franquiciaService.listar().stream()
                .map(FranquiciaMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FranquiciaDTO buscarPorId(@PathVariable Long id) {
        return FranquiciaMapper.toDTO(franquiciaService.buscarPorId(id));
    }
}