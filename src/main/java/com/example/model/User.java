package com.example.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Component
public class User {
    private UUID id;
    private String name;
    private List<Order> orders=new ArrayList<>();


}
