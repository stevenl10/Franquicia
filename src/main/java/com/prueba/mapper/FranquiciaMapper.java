package com.prueba.mapper;

import com.prueba.dto.response.FranquiciaDTO;
import com.prueba.dto.response.ProductoDTO;
import com.prueba.dto.response.SucursalDTO;
import com.prueba.model.Franquicia;
import com.prueba.model.Producto;
import com.prueba.model.Sucursal;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FranquiciaMapper {

    public static ProductoDTO toDTO(Producto p) {
        return new ProductoDTO(p.getId(), p.getNombre(), p.getStock());
    }

    public static SucursalDTO toDTO(Sucursal s) {
        List<ProductoDTO> productos = s.getProductos() == null ? Collections.emptyList() :
                s.getProductos().stream().map(FranquiciaMapper::toDTO).collect(Collectors.toList());
        return new SucursalDTO(s.getId(), s.getNombre(), productos);
    }

    public static FranquiciaDTO toDTO(Franquicia f) {
        List<SucursalDTO> sucursales = f.getSucursales() == null ? Collections.emptyList() :
                f.getSucursales().stream().map(FranquiciaMapper::toDTO).collect(Collectors.toList());
        return new FranquiciaDTO(f.getId(), f.getNombre(), sucursales);
    }
}
