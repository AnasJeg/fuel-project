package com.fuel_spring_server.fuel.repository;


import com.fuel_spring_server.fuel.domain.Fuel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface FuelRepository extends JpaRepository<Fuel,Long> {

    @Query(nativeQuery = true, value = "SELECT f.type, SUM(f.litre) AS litres, SUM(f.totale) AS totale FROM fuel f WHERE f.user_id = ?1 GROUP BY f.type;")
    List<ArrayList> getChart(Long id);

    @Query(nativeQuery = true,value = "SELECT f.type, SUM(f.litre) AS litres, SUM(f.totale) AS totale, f.date FROM fuel f WHERE f.user_id = ?1 GROUP BY f.type, f.date;")
    List<ArrayList> getLineChart(Long id);

    Page<Fuel> getFuelByUserId(Long id, Pageable pageable);

}
