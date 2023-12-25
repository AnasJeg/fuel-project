package com.fuel_spring_server.fuel.repository;


import com.fuel_spring_server.fuel.domain.Fuel;
import com.fuel_spring_server.fuel.dto.PieChartDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface FuelRepository extends JpaRepository<Fuel,Long> {

    @Query(nativeQuery = true, value = "SELECT f.type, SUM(f.litre) AS litres, SUM(f.totale) AS totale FROM fuel f WHERE f.user_id = ?1 GROUP BY f.type;")
    List<ArrayList> getChart(Long id);
}
