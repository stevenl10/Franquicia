package com.prueba.controller;

import com.prueba.dto.request.ActualizarNombreDTO;
import com.prueba.dto.response.FranquiciaDTO;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Franquicia;
import com.prueba.model.Sucursal;
import com.prueba.service.FranquiciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FranquiciaControllerTest {

    private FranquiciaService franquiciaService;
    private FranquiciaController controller;

    @BeforeEach
    public void setup() {
        franquiciaService = mock(FranquiciaService.class);
        controller = new FranquiciaController(franquiciaService);
    }

    @Test
    public void testAgregarFranquicia_returnsCreatedFranquiciaDto() {

        Franquicia input = Franquicia.builder().nombre("Nueva").sucursales(Collections.emptyList()).build();
        Franquicia saved = Franquicia.builder().id(1L).nombre("Nueva").sucursales(Collections.emptyList()).build();

        when(franquiciaService.agregar(input)).thenReturn(saved);

        FranquiciaDTO dto = controller.agregar(input);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Nueva", dto.getNombre());
        assertNotNull(dto.getSucursales());
        verify(franquiciaService, times(1)).agregar(input);
    }

    @Test
    public void testActualizarNombre_updatesAndReturnsFranquiciaDto() {
        Long id = 5L;
        ActualizarNombreDTO dtoRequest = new ActualizarNombreDTO("NombreActualizado");
        Franquicia updated = Franquicia.builder().id(id).nombre("NombreActualizado").sucursales(Collections.emptyList()).build();

        when(franquiciaService.actualizarNombre(eq(id), eq("NombreActualizado"))).thenReturn(updated);
        FranquiciaDTO result = controller.actualizarNombre(id, dtoRequest);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("NombreActualizado", result.getNombre());
        verify(franquiciaService, times(1)).actualizarNombre(id, "NombreActualizado");
    }

    @Test
    public void testListar_returnsListOfFranquiciaDtos() {
        Franquicia f1 = Franquicia.builder().id(1L).nombre("F1").sucursales(Collections.emptyList()).build();
        Sucursal s = new Sucursal();
        Franquicia f2 = Franquicia.builder().id(2L).nombre("F2").sucursales(Arrays.asList(s)).build();
        when(franquiciaService.listar()).thenReturn(Arrays.asList(f1, f2));
        List<FranquiciaDTO> list = controller.listar();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("F1", list.get(0).getNombre());
        assertEquals(2L, list.get(1).getId());
        assertEquals("F2", list.get(1).getNombre());
        verify(franquiciaService, times(1)).listar();
    }

    @Test
    public void testBuscarPorId_nonExistentId_throwsResourceNotFound() {

        Long missingId = 999L;
        when(franquiciaService.buscarPorId(missingId)).thenThrow(new ResourceNotFoundException("Franquicia no encontrada: " + missingId));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> controller.buscarPorId(missingId));
        assertTrue(ex.getMessage().contains(String.valueOf(missingId)));
        verify(franquiciaService, times(1)).buscarPorId(missingId);
    }

    @Test
    public void testActualizarNombre_withNullOrEmptyNombre_handlesInvalidInput() {
        Long id = 10L;
        ActualizarNombreDTO nullDto = new ActualizarNombreDTO(null);

        when(franquiciaService.actualizarNombre(eq(id), isNull())).thenThrow(new IllegalArgumentException("Nombre inválido"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> controller.actualizarNombre(id, nullDto));
        assertEquals("Nombre inválido", ex.getMessage());
        verify(franquiciaService, times(1)).actualizarNombre(id, null);

        ActualizarNombreDTO emptyDto = new ActualizarNombreDTO("");
        when(franquiciaService.actualizarNombre(eq(id), eq(""))).thenThrow(new IllegalArgumentException("Nombre inválido"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> controller.actualizarNombre(id, emptyDto));
        assertEquals("Nombre inválido", ex2.getMessage());
        verify(franquiciaService, times(1)).actualizarNombre(id, "");
    }

    @Test
    public void testAgregar_withInvalidPayload_returnsBadRequest() {
        Franquicia invalid = Franquicia.builder().nombre("").sucursales(Collections.emptyList()).build();
        when(franquiciaService.agregar(invalid)).thenThrow(new IllegalArgumentException("nombre cannot be blank"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> controller.agregar(invalid));
        assertEquals("nombre cannot be blank", ex.getMessage());
        verify(franquiciaService, times(1)).agregar(invalid);
    }
}