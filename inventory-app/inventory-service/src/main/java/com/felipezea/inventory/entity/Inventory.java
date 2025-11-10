package com.felipezea.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class Inventory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id",  nullable = false)
    private Long productFK;

    @Column
    private Integer quantity;
}
