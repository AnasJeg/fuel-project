package com.fuel_spring_server.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuel_spring_server.fuel.domain.Fuel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "prenom cannot be empty")
    private String prenom;

    @Column(nullable = false)
    @NotEmpty(message = "nom cannot be empty")
    private String nom;
    @Column(nullable = false)
    @NotEmpty(message = "city cannot be empty")
    private String city;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "email cannot be empty")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "password cannot be empty")
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Fuel> fuels=new ArrayList<>();
}
