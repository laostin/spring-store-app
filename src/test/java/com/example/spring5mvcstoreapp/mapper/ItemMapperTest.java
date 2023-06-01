package com.example.spring5mvcstoreapp.mapper;

import com.example.spring5mvcstoreapp.api.v1.model.ItemDTO;
import com.example.spring5mvcstoreapp.domain.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ItemMapperImpl.class})
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void itemDTOToItemTest() {
        Item item = Item.builder().productName("chocolate").productId(1L).quantity(2).build();
        ItemDTO itemDTO = ItemDTO.builder().productId(1L).productName("chocolate").quantity(2).build();
        Item result = itemMapper.itemDTOToItem(itemDTO);
        assertEquals(result, item);
    }
}