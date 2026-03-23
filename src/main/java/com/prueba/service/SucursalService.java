package com.prueba.service;

import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Franquicia;
import com.prueba.model.Sucursal;
import com.prueba.repository.FranquiciaRepository;
import com.prueba.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final FranquiciaRepository franquiciaRepository;

    public Sucursal agregar(Long franquiciaId, Sucursal sucursal) {
        Franquicia franquicia = franquiciaRepository.findById(franquiciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Franquicia no encontrada: " + franquiciaId));
        sucursal.setFranquicia(franquicia);
        return sucursalRepository.save(sucursal);
    }

    public Sucursal actualizarNombre(Long id, String nuevoNombre) {
        Sucursal s = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id));
        s.setNombre(nuevoNombre);
        return sucursalRepository.save(s);
    }
}
