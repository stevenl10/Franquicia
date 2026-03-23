package com.prueba.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private int stock;
}
