package com.prueba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Franquicia;
import com.prueba.repository.FranquiciaRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceTest {

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @InjectMocks
    private FranquiciaService franquiciaService;

    private Franquicia sampleFranquicia;

    @BeforeEach
    void setUp() {
        sampleFranquicia = Franquicia.builder()
                .id(1L)
                .nombre("Original")
                .build();
    }

    @Test
    void testAgregarPersistsAndReturnsFranquicia() {
        when(franquiciaRepository.save(sampleFranquicia)).thenReturn(sampleFranquicia);

        Franquicia resultado = franquiciaService.agregar(sampleFranquicia);

        assertNotNull(resultado);
        assertEquals(sampleFranquicia.getId(), resultado.getId());
        assertEquals(sampleFranquicia.getNombre(), resultado.getNombre());
        verify(franquiciaRepository, times(1)).save(sampleFranquicia);
    }

    @Test
    void testActualizarNombreUpdatesAndSavesEntity() {
        when(franquiciaRepository.findById(1L)).thenReturn(Optional.of(sampleFranquicia));
        ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Franquicia actualizado = franquiciaService.actualizarNombre(1L, "NuevoNombre");

        assertNotNull(actualizado);
        assertEquals("NuevoNombre", actualizado.getNombre());
        verify(franquiciaRepository).findById(1L);
        verify(franquiciaRepository).save(captor.capture());
        assertEquals("NuevoNombre", captor.getValue().getNombre());
    }

    @Test
    void testListarReturnsAllFranquicias() {
        Franquicia f2 = Franquicia.builder().id(2L).nombre("F2").build();
        List<Franquicia> lista = Arrays.asList(sampleFranquicia, f2);
        when(franquiciaRepository.findAll()).thenReturn(lista);

        List<Franquicia> resultado = franquiciaService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(sampleFranquicia));
        assertTrue(resultado.contains(f2));
        verify(franquiciaRepository, times(1)).findAll();
    }

    @Test
    void testActualizarNombreThrowsWhenIdNotFound() {
        when(franquiciaRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                franquiciaService.actualizarNombre(999L, "NoExiste"));

        assertTrue(ex.getMessage().contains("Franquicia no encontrada"));
        verify(franquiciaRepository, times(1)).findById(999L);
        verify(franquiciaRepository, never()).save(any());
    }

    @Test
    void testBuscarPorIdThrowsWhenIdNotFound() {
        when(franquiciaRepository.findById(500L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                franquiciaService.buscarPorId(500L));

        assertTrue(ex.getMessage().contains("Franquicia no encontrada"));
        verify(franquiciaRepository, times(1)).findById(500L);
    }

    @Test
    void testAgregarHandlesNullOrInvalidFranquicia() {
        // Case 1: null passed
        when(franquiciaRepository.save(null)).thenThrow(new IllegalArgumentException("entity is null"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                franquiciaService.agregar(null));
        assertEquals("entity is null", ex.getMessage());
        verify(franquiciaRepository).save(null);

        // Case 2: invalid franquicia (missing required fields) - simulate repository behavior
        Franquicia invalid = Franquicia.builder().id(null).nombre("").build();
        when(franquiciaRepository.save(invalid)).thenThrow(new RuntimeException("Validation failed"));

        RuntimeException ex2 = assertThrows(RuntimeException.class, () ->
                franquiciaService.agregar(invalid));
        assertEquals("Validation failed", ex2.getMessage());
        verify(franquiciaRepository).save(invalid);
    }
}