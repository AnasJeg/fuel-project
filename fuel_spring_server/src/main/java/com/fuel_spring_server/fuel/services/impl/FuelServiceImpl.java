package com.fuel_spring_server.fuel.services.impl;

import com.fuel_spring_server.fuel.domain.Fuel;
import com.fuel_spring_server.fuel.repository.FuelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FuelServiceImpl {
    private final FuelRepository fuelRepository;

    private static final String flaskUrl = "http://localhost:8888/FUEL-FLASK/prices";


    private Map<String, Double> fetchFuelPrices() {
        try {
            ResponseEntity<Map> responseEntity = new RestTemplate().getForEntity(flaskUrl, Map.class);
            log.info("fetching fuel prices from: {}", flaskUrl);
            log.info("response with status code: {}", responseEntity.getStatusCode());

            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP client error while fetching fuel prices. Status code: {}", e.getRawStatusCode());
            throw new RuntimeException("Failed to fetch fuel prices. Client error.", e);
        } catch (HttpServerErrorException e) {
            log.error("HTTP server error while fetching fuel prices. Status code: {}", e.getRawStatusCode());
            throw new RuntimeException("Failed to fetch fuel prices. Server error.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching fuel prices from Flask", e);
            throw new RuntimeException("Unexpected error during fuel price fetch.", e);
        }

    }

    public Fuel save(Fuel fuel) {
        Map<String, Double> fuelPrices = fetchFuelPrices();
        double litres = fuelPrices.get(fuel.getType().name()) / fuel.getTotale();
        fuel.setLitre(litres);
        fuel.setDate(new Date());
        fuel.setType(fuel.getType());
        fuel.setPrice(fuelPrices.get(fuel.getType().name()) );
        return fuelRepository.save(fuel);
    }

    public Page<Fuel> getAll(Pageable pageable) {
        log.info("Get all fuel page {} size {}", pageable.getPageNumber(), pageable.getPageSize());
        return fuelRepository.findAll(pageable);
    }

    public Fuel getTransactionById(Long id) {
        if (fuelRepository.existsById(id)) {
            log.info("Transaction with id {}", id);
            return fuelRepository.findById(id).orElse(null);
        } else {
            log.warn("Transaction with id {} not found", id);
            return null;
        }
    }

    public Boolean deleteTransaction(Long id) {
        if (fuelRepository.existsById(id)) {
            log.info("Deleting transaction with id = {}", id);
            fuelRepository.deleteById(id);
            return true;
        }
        return false;
    }
}