package org.revature.Alcott_P1_Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "magics")
public class Magic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String name;

    // private Blob? image;

    private String description;

    private int stock;

    private float price;

    private String category;

    private String imageUrl;

}
