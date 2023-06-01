package com.example.spring5mvcstoreapp.mapper;

import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;
import com.example.spring5mvcstoreapp.domain.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDTOToItem(ItemDTO itemDTO);
}
