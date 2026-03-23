package com.prueba.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Franquicia;
import com.prueba.model.Sucursal;
import com.prueba.repository.FranquiciaRepository;
import com.prueba.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private FranquiciaRepository franquiciaRepository;

    private SucursalService sucursalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sucursalService = new SucursalService(sucursalRepository, franquiciaRepository);
    }

    @Test
    void testAgregarSucursalConFranquiciaExistente() {
        Long franquiciaId = 1L;
        Franquicia franquicia = Franquicia.builder().id(franquiciaId).nombre("F1").build();
        Sucursal entrada = Sucursal.builder().nombre("Sucursal A").build();
        Sucursal guardada = Sucursal.builder().id(10L).nombre("Sucursal A").franquicia(franquicia).build();

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Optional.of(franquicia));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(guardada);

        Sucursal resultado = sucursalService.agregar(franquiciaId, entrada);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(franquicia, resultado.getFranquicia());

        ArgumentCaptor<Sucursal> captor = ArgumentCaptor.forClass(Sucursal.class);
        verify(sucursalRepository).save(captor.capture());
        Sucursal toSave = captor.getValue();
        assertEquals("Sucursal A", toSave.getNombre());
        assertEquals(franquicia, toSave.getFranquicia());
    }

    @Test
    void testActualizarNombreSucursalExistente() {
        Long sucursalId = 5L;
        Sucursal existente = Sucursal.builder().id(sucursalId).nombre("Antiguo").build();
        Sucursal actualizado = Sucursal.builder().id(sucursalId).nombre("Nuevo").build();

        when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.of(existente));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(actualizado);

        Sucursal resultado = sucursalService.actualizarNombre(sucursalId, "Nuevo");

        assertNotNull(resultado);
        assertEquals(sucursalId, resultado.getId());
        assertEquals("Nuevo", resultado.getNombre());

        ArgumentCaptor<Sucursal> captor = ArgumentCaptor.forClass(Sucursal.class);
        verify(sucursalRepository).save(captor.capture());
        assertEquals("Nuevo", captor.getValue().getNombre());
    }

    @Test
    void testAgregarSucursalRetornaEntidadPersistida() {
        Long franquiciaId = 2L;
        Franquicia franquicia = Franquicia.builder().id(franquiciaId).nombre("F2").build();
        Sucursal entrada = Sucursal.builder().nombre("Sucursal B").build();
        Sucursal persistida = Sucursal.builder().id(20L).nombre("Sucursal B").franquicia(franquicia).build();

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Optional.of(franquicia));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(persistida);

        Sucursal resultado = sucursalService.agregar(franquiciaId, entrada);

        // Verifica que el servicio retorna exactamente la instancia que devuelve el repositorio
        assertSame(persistida, resultado);
        assertEquals(20L, resultado.getId());
        assertEquals(franquicia, resultado.getFranquicia());
    }

    @Test
    void testAgregarSucursalCuandoFranquiciaNoExisteLanzaResourceNotFound() {
        Long franquiciaId = 999L;
        Sucursal entrada = Sucursal.builder().nombre("Sucursal X").build();

        when(franquiciaRepository.findById(franquiciaId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> sucursalService.agregar(franquiciaId, entrada));
        assertTrue(ex.getMessage().contains("Franquicia no encontrada"));
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void testActualizarNombreSucursalInexistenteLanzaResourceNotFound() {
        Long sucursalId = 77L;
        when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> sucursalService.actualizarNombre(sucursalId, "Cualquier"));
        assertTrue(ex.getMessage().contains("Sucursal no encontrada"));
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void testActualizarNombreConValorNuloOEnBlanco() {
        Long sucursalId = 33L;
        Sucursal existente = Sucursal.builder().id(sucursalId).nombre("Original").build();

        when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.of(existente));
        // Simular que el repositorio guarda y retorna la entidad con el nombre tal cual fue seteado
        when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Caso: nombre nulo
        Sucursal resultadoNulo = sucursalService.actualizarNombre(sucursalId, null);
        assertNull(resultadoNulo.getNombre());
        verify(sucursalRepository).save(any(Sucursal.class));

        // Reset mock interactions for next sub-case
        reset(sucursalRepository);
        when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.of(existente));
        when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Caso: nombre en blanco
        Sucursal resultadoBlanco = sucursalService.actualizarNombre(sucursalId, "");
        assertEquals("", resultadoBlanco.getNombre());
        verify(sucursalRepository).save(any(Sucursal.class));
    }
}