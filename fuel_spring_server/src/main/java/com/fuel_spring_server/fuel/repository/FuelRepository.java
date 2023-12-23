package com.fuel_spring_server.fuel.repository;


import com.fuel_spring_server.fuel.domain.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuelRepository extends JpaRepository<Fuel,Long> {
}
