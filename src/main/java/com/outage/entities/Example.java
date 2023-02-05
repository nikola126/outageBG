package com.outage.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "example")
public class Example {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
