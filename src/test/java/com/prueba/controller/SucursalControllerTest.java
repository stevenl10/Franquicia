package com.prueba.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import com.prueba.dto.request.ActualizarNombreDTO;
import com.prueba.dto.response.SucursalDTO;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Sucursal;
import com.prueba.service.SucursalService;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SucursalControllerTest {

    @Mock
    private SucursalService sucursalService;

    @InjectMocks
    private SucursalController sucursalController;

    private Sucursal sucursalModel;
    private SucursalDTO sucursalDTO;

    @BeforeEach
    void setUp() {
        sucursalModel = Sucursal.builder()
                .id(1L)
                .nombre("Sucursal A")
                .productos(Collections.emptyList())
                .build();

        sucursalDTO = new SucursalDTO(1L, "Sucursal A", Collections.emptyList());
    }

    @Test
    void testAgregarSucursal_ReturnsCreatedSucursalDTO() {
        Long franquiciaId = 10L;
        when(sucursalService.agregar(eq(franquiciaId), any(Sucursal.class))).thenReturn(sucursalModel);

        SucursalDTO result = sucursalController.agregar(franquiciaId, new Sucursal());
        verify(sucursalService, times(1)).agregar(eq(franquiciaId), any(Sucursal.class));
        assertNotNull(result);
        assertEquals(sucursalModel.getId(), result.getId());
        assertEquals(sucursalModel.getNombre(), result.getNombre());
        assertEquals(0, result.getProductos().size());
    }

    @Test
    void testActualizarNombre_SuccessfulUpdate_ReturnsUpdatedDTO() {
        Long id = 5L;
        String nuevoNombre = "Nuevo Nombre";
        ActualizarNombreDTO dto = new ActualizarNombreDTO(nuevoNombre);

        Sucursal updatedModel = Sucursal.builder()
                .id(id)
                .nombre(nuevoNombre)
                .productos(List.of(new com.prueba.model.Producto()))
                .build();

        when(sucursalService.actualizarNombre(eq(id), eq(nuevoNombre))).thenReturn(updatedModel);

        SucursalDTO response = sucursalController.actualizarNombre(id, dto);

        verify(sucursalService, times(1)).actualizarNombre(eq(id), eq(nuevoNombre));
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(nuevoNombre, response.getNombre());
        assertNotNull(response.getProductos());
    }

    @Test
    void testAgregarSucursal_ReturnsHttpStatusCreated() {
        Long franquiciaId = 2L;
        when(sucursalService.agregar(eq(franquiciaId), any(Sucursal.class))).thenReturn(sucursalModel);
        SucursalDTO dto = sucursalController.agregar(franquiciaId, new Sucursal());
        assertNotNull(dto);
        assertEquals(sucursalModel.getId(), dto.getId());
        try {
            java.lang.reflect.Method m = SucursalController.class.getMethod("agregar", Long.class, Sucursal.class);
            ResponseStatus ann = m.getAnnotation(ResponseStatus.class);
            assertNotNull(ann, "agregar should be annotated with @ResponseStatus");
            assertEquals(HttpStatus.CREATED, ann.value());
        } catch (NoSuchMethodException e) {
            fail("agregar method not found via reflection");
        }
    }

    @Test
    void testAgregarSucursal_FranquiciaNotFound_ThrowsResourceNotFoundException() {
        Long franquiciaId = 99L;
        when(sucursalService.agregar(eq(franquiciaId), any(Sucursal.class)))
                .thenThrow(new ResourceNotFoundException("Franquicia no encontrada: " + franquiciaId));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                sucursalController.agregar(franquiciaId, new Sucursal()));

        assertTrue(ex.getMessage().contains(franquiciaId.toString()));
        verify(sucursalService, times(1)).agregar(eq(franquiciaId), any(Sucursal.class));
    }

    @Test
    void testActualizarNombre_SucursalNotFound_ThrowsResourceNotFoundException() {
        Long id = 123L;
        ActualizarNombreDTO dto = new ActualizarNombreDTO("Any");

        when(sucursalService.actualizarNombre(eq(id), anyString()))
                .thenThrow(new ResourceNotFoundException("Sucursal no encontrada: " + id));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                sucursalController.actualizarNombre(id, dto));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(sucursalService, times(1)).actualizarNombre(eq(id), anyString());
    }

    @Test
    void testActualizarNombre_InvalidNombreInput_ValidationOrErrorHandled() {
        Long id = 77L;

        ActualizarNombreDTO dtoNull = new ActualizarNombreDTO(null);
        Sucursal modelWithNullName = Sucursal.builder().id(id).nombre(null).productos(Collections.emptyList()).build();
        when(sucursalService.actualizarNombre(eq(id), isNull())).thenReturn(modelWithNullName);

        SucursalDTO responseNull = sucursalController.actualizarNombre(id, dtoNull);
        assertNotNull(responseNull);
        assertNull(responseNull.getNombre());

        verify(sucursalService, times(1)).actualizarNombre(eq(id), isNull());

        ActualizarNombreDTO dtoBlank = new ActualizarNombreDTO("   ");
        Sucursal modelWithBlank = Sucursal.builder().id(id).nombre("   ").productos(Collections.emptyList()).build();
        when(sucursalService.actualizarNombre(eq(id), eq("   "))).thenReturn(modelWithBlank);

        SucursalDTO responseBlank = sucursalController.actualizarNombre(id, dtoBlank);
        assertNotNull(responseBlank);
        assertEquals("   ", responseBlank.getNombre());

        verify(sucursalService, times(1)).actualizarNombre(eq(id), eq("   "));
    }
}