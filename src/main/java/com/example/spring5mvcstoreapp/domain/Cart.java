package com.example.spring5mvcstoreapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double subtotal;
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        item.setCart(this);
        this.items.add(item);
    }

    public void removeItem(Item item) {
        item.setCart(null);
        this.items.remove(item);
    }

    public void emptyCart() {
        for (Item item : items) {
            item.setCart(null);
        }
        this.items.clear();
        this.subtotal = 0.0;
    }
}
