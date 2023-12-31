package com.fuel_spring_server.fuel.services.impl;

import com.fuel_spring_server.fuel.domain.Fuel;
import com.fuel_spring_server.fuel.dto.LineChartDTO;
import com.fuel_spring_server.fuel.dto.PieChartDTO;
import com.fuel_spring_server.fuel.repository.FuelRepository;
import com.fuel_spring_server.user.domain.User;
import com.fuel_spring_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FuelServiceImpl {
    private final FuelRepository fuelRepository;

    private final UserRepository userRepository;

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
        Double fuelPrice = fuelPrices.get(fuel.getType());

        if (fuelPrice != null && fuel.getTotale() != 0) {
            double litres = fuel.getTotale() / fuelPrice;
            fuel.setLitre(litres);
            fuel.setDate(new Date());
            fuel.setType(fuel.getType());
            fuel.setPrice(fuelPrice);
            return fuelRepository.save(fuel);
        } else {
            log.warn("Failed to calculate litres fuel price {}",fuelPrice);
            throw new RuntimeException("Failed to calculate litres. Invalid fuel price or totale.");
        }
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
/*
    public List<PieChartDTO> pieChartDTOS(Long id){
        return fuelRepository.getChart(id);
    }

 */

       public List<PieChartDTO> pieChartDTOS(Long id) {
        List<ArrayList> result = fuelRepository.getChart(id);

        return result.stream()
                .map(row -> {
                    String type = (String) row.get(0);
                    Double litres = (Double) row.get(1);
                    Double totale = (Double) row.get(2);
                    return new PieChartDTO(type, litres, totale);
                })
                .collect(Collectors.toList());
    }

    public List<LineChartDTO> lineChartDTOS(Long id) {
        List<ArrayList> result = fuelRepository.getLineChart(id);
        return result.stream()
                .map(row -> {
                    String type = (String) row.get(0);
                    Double litres = (Double) row.get(1);
                    Double totale = (Double) row.get(2);
                    Date date = (Date) row.get(3);
                    return new LineChartDTO(type, litres, totale,date);
                })
                .collect(Collectors.toList());
    }

    public Page<Fuel> getTransactionByUserId(Long id, Pageable pageable) {
        if (userRepository.existsById(id)) {
            log.info("User Transaction with id {}", id);
            return fuelRepository.getFuelByUserId(id, pageable);
        } else {
            log.warn("User Transaction with id {} not found", id);
            return null;
        }
    }

}
