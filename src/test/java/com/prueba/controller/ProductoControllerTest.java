package com.prueba.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.prueba.dto.request.ActualizarNombreDTO;
import com.prueba.dto.request.ActualizarStockDTO;
import com.prueba.dto.response.ProductoDTO;
import com.prueba.dto.response.ResponseMensajeDTO;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.mapper.FranquiciaMapper;
import com.prueba.model.Producto;
import com.prueba.service.ProductoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @Mock
    private FranquiciaMapper franquiciaMapper;

    @InjectMocks
    private ProductoController productoController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAgregarProductToExistingSucursalReturnsProductoDTO() {
        Long sucursalId = 1L;
        Producto incoming = Producto.builder().nombre("Pan").stock(10).build();
        Producto saved = Producto.builder().id(100L).nombre("Pan").stock(10).build();
        ProductoDTO expectedDto = new ProductoDTO(100L, "Pan", 10);

        when(productoService.agregar(eq(sucursalId), any(Producto.class))).thenReturn(saved);
        ProductoDTO result = productoController.agregar(sucursalId, incoming);

        verify(productoService).agregar(eq(sucursalId), any(Producto.class));
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getNombre(), result.getNombre());
        assertEquals(expectedDto.getStock(), result.getStock());
    }

    @Test
    void testEliminarExistingProductReturnsSuccessMessage() {
        Long id = 5L;

        doNothing().when(productoService).eliminar(id);

        ResponseMensajeDTO response = productoController.eliminar(id);

        verify(productoService).eliminar(id);
        assertNotNull(response);
        assertEquals("Producto con id " + id + " eliminado correctamente", response.getMensaje());
    }

    @Test
    void testActualizarStockUpdatesAndReturnsProductoDTO() {
        Long id = 7L;
        ActualizarStockDTO dto = new ActualizarStockDTO(25);
        Producto updated = Producto.builder().id(id).nombre("Leche").stock(25).build();
        ProductoDTO expectedDto = new ProductoDTO(id, "Leche", 25);

        when(productoService.actualizarStock(id, dto.getStock())).thenReturn(updated);

        ProductoDTO result = productoController.actualizarStock(id, dto);

        verify(productoService).actualizarStock(id, dto.getStock());
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getNombre(), result.getNombre());
        assertEquals(expectedDto.getStock(), result.getStock());
    }

    @Test
    void testAgregarProductToNonExistingSucursalThrowsResourceNotFound() {
        Long sucursalId = 99L;
        Producto incoming = Producto.builder().nombre("Queso").stock(5).build();

        when(productoService.agregar(eq(sucursalId), any(Producto.class)))
                .thenThrow(new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoController.agregar(sucursalId, incoming));

        assertTrue(ex.getMessage().contains("Sucursal no encontrada"));
        verify(productoService).agregar(eq(sucursalId), any(Producto.class));
    }

    @Test
    void testEliminarNonExistingProductThrowsResourceNotFound() {
        Long id = 88L;

        doThrow(new ResourceNotFoundException("Producto no encontrado: " + id))
                .when(productoService).eliminar(id);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoController.eliminar(id));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(productoService).eliminar(id);
    }

    @Test
    void testActualizarWithInvalidDtoReturnsValidationError() {
        Long id = 11L;
        ActualizarStockDTO invalidStockDto = new ActualizarStockDTO(-5);

        when(productoService.actualizarStock(id, invalidStockDto.getStock()))
                .thenThrow(new IllegalArgumentException("Stock must be >= 0"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoController.actualizarStock(id, invalidStockDto));

        assertTrue(ex.getMessage().contains("Stock must be"));
        verify(productoService).actualizarStock(id, invalidStockDto.getStock());

        ActualizarNombreDTO invalidNameDto = new ActualizarNombreDTO(null);
        when(productoService.actualizarNombre(id, null))
                .thenThrow(new IllegalArgumentException("Nombre no puede ser nulo o vacío"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> productoController.actualizarNombre(id, invalidNameDto));

        assertTrue(ex2.getMessage().contains("Nombre"));
        verify(productoService).actualizarNombre(id, null);
    }
}