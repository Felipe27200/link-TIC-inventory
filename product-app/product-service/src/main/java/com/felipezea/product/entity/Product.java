package com.felipezea.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @Column(nullable = false)
    private  String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
}
