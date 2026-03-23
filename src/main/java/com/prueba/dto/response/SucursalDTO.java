package com.prueba.dto.response;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SucursalDTO {
    private Long id;
    private String nombre;
    private List<ProductoDTO> productos;
}
