package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Product {
    private UUID id;
    private String name;
    private double price;

    public Product(String name, double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
    }


}