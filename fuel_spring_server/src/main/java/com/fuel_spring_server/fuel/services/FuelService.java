package com.fuel_spring_server.fuel.services;


import com.fuel_spring_server.fuel.domain.Fuel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface FuelService {
    Fuel save(Fuel fuel);
    Page<Fuel> getAll(Pageable pageable);
    Fuel getTransactionById(Long id);
    Boolean deleteTransaction(Long fuel);
}
