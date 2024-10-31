package com.cinema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="Spaceship")
@Data
@Builder
@AllArgsConstructor

public class Spaceship {

    public Spaceship(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Long id;

    @Column(name = "Name")
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(name="Origin")
    private SpaceShipOrigin origin;


}
