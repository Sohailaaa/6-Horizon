package com.example.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Data
@Component
public class User {
    private UUID id;
    private String name;
    private List<Order> orders=new ArrayList<>();
}