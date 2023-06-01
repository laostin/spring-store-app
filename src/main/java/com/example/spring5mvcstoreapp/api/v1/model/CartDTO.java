package com.example.spring5mvcstoreapp.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CartDTO {
    private List<ItemDTO> items;
    private Double subtotal;
}
