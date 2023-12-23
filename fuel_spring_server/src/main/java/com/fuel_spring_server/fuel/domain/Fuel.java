package com.fuel_spring_server.fuel.domain;

import com.fuel_spring_server.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity @Data
@AllArgsConstructor @NoArgsConstructor
public class Fuel {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Type type;
        @Column(nullable = false)
 //       @NotEmpty(message = "price cannot be empty")
        private double price;

        @Column(nullable = false)
   //     @NotEmpty(message = "totale cannot be empty")
        private double totale;

        @Column(nullable = false)
   //     @NotEmpty(message = "litre cannot be empty")
        private double litre;
        @Column(nullable = false)
    //    @NotEmpty(message = "date cannot be empty")
        private Date date;

        @ManyToOne(fetch = FetchType.EAGER)
        private User user;
}
