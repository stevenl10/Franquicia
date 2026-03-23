package com.prueba.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.prueba.dto.response.ProductosStockDTO;
import com.prueba.exception.ResourceNotFoundException;
import com.prueba.model.Producto;
import com.prueba.model.Sucursal;
import com.prueba.repository.ProductoRepository;
import com.prueba.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAgregarProducto_AsociaSucursalYGuarda() {
        Long sucursalId = 1L;
        Sucursal sucursal = Sucursal.builder().id(sucursalId).nombre("Sucursal A").build();
        Producto productoEntrada = Producto.builder().nombre("Producto X").stock(5).build();
        Producto productoGuardado = Producto.builder().id(10L).nombre("Producto X").stock(5).sucursal(sucursal).build();

        when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.of(sucursal));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto resultado = productoService.agregar(sucursalId, productoEntrada);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(productoGuardado.getId(), resultado.getId());
        assertEquals(sucursal, resultado.getSucursal());
        verify(sucursalRepository, times(1)).findById(sucursalId);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testEliminarProducto_Existe_EliminadoConExito() {
        Long productoId = 20L;

        when(productoRepository.existsById(productoId)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(productoId);

        assertDoesNotThrow(() -> productoService.eliminar(productoId));

        verify(productoRepository, times(1)).existsById(productoId);
        verify(productoRepository, times(1)).deleteById(productoId);
    }

    @Test
    void testTopStockPorFranquicia_MapeaAProductosStockDTO() {
        Sucursal s1 = Sucursal.builder().id(1L).nombre("Suc A").build();
        Sucursal s2 = Sucursal.builder().id(2L).nombre("Suc B").build();

        Producto p1 = Producto.builder().id(1L).nombre("Prod1").stock(50).sucursal(s1).build();
        Producto p2 = Producto.builder().id(2L).nombre("Prod2").stock(30).sucursal(s2).build();

        when(productoRepository.findTopStockPorFranquicia(100L)).thenReturn(Arrays.asList(p1, p2));

        List<ProductosStockDTO> dtos = productoService.topStockPorFranquicia(100L);

        assertEquals(2, dtos.size());
        assertEquals("Suc A", dtos.get(0).getSucursalNombre());
        assertEquals("Prod1", dtos.get(0).getProductoNombre());
        assertEquals(50, dtos.get(0).getStock());
        assertEquals("Suc B", dtos.get(1).getSucursalNombre());
        assertEquals("Prod2", dtos.get(1).getProductoNombre());
        assertEquals(30, dtos.get(1).getStock());

        verify(productoRepository, times(1)).findTopStockPorFranquicia(100L);
    }

    @Test
    void testAgregarProducto_SucursalNoEncontrada_LanzaResourceNotFoundException() {
        Long sucursalId = 5L;
        Producto productoEntrada = Producto.builder().nombre("Producto Y").stock(2).build();

        when(sucursalRepository.findById(sucursalId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoService.agregar(sucursalId, productoEntrada));

        assertTrue(ex.getMessage().contains("Sucursal no encontrada"));
        verify(sucursalRepository, times(1)).findById(sucursalId);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testEliminarProducto_NoExiste_LanzaResourceNotFoundException() {
        Long productoId = 99L;

        when(productoRepository.existsById(productoId)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoService.eliminar(productoId));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(productoRepository, times(1)).existsById(productoId);
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testActualizarProducto_NoExiste_LanzaResourceNotFoundException() {
        Long productoId = 77L;

        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exStock = assertThrows(ResourceNotFoundException.class,
                () -> productoService.actualizarStock(productoId, 10));
        assertTrue(exStock.getMessage().contains("Producto no encontrado"));

        ResourceNotFoundException exNombre = assertThrows(ResourceNotFoundException.class,
                () -> productoService.actualizarNombre(productoId, "NuevoNombre"));
        assertTrue(exNombre.getMessage().contains("Producto no encontrado"));

        verify(productoRepository, times(2)).findById(productoId);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}