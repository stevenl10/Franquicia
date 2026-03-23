package com.prueba.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductosStockDTO {
    private String sucursalNombre;
    private String productoNombre;
    private int stock;
}
