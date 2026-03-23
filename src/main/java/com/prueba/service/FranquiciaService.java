package com.prueba.service;

import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Franquicia;
import com.prueba.repository.FranquiciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;

    public Franquicia agregar(Franquicia franquicia) {
        return franquiciaRepository.save(franquicia);
    }

    public Franquicia actualizarNombre(Long id, String nuevoNombre) {
        Franquicia f = franquiciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Franquicia no encontrada: " + id));
        f.setNombre(nuevoNombre);
        return franquiciaRepository.save(f);
    }

    public List<Franquicia> listar() {
        return franquiciaRepository.findAll();
    }

    public Franquicia buscarPorId(Long id) {
        return franquiciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Franquicia no encontrada: " + id));
    }
}
