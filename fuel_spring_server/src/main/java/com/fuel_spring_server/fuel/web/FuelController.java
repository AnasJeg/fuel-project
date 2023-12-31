package com.fuel_spring_server.fuel.web;


import com.fuel_spring_server.fuel.domain.Fuel;
import com.fuel_spring_server.fuel.dto.PieChartDTO;
import com.fuel_spring_server.fuel.services.impl.FuelServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel")
@CrossOrigin("*")
public class FuelController {



    private final FuelServiceImpl fuelService;

    public FuelController(FuelServiceImpl service){
        this.fuelService=service;
    }

    @PostMapping("/")
    public ResponseEntity<Fuel> saveFuel(@RequestBody Fuel fuel) {
        Fuel savedFuel = fuelService.save(fuel);
        return new ResponseEntity<>(savedFuel, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<Page<Fuel>> getAllFuel(Pageable pageable) {
        Page<Fuel> fuels = fuelService.getAll(pageable);
        return new ResponseEntity<>(fuels, HttpStatus.OK);
    }

    @GetMapping("/pie/{id}")
    public ResponseEntity<List<PieChartDTO>> getPieCharts(@PathVariable Long id) {
        List<PieChartDTO> pieChartData = fuelService.pieChartDTOS(id);
        return new ResponseEntity<>(pieChartData, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Fuel> getFuelById(@PathVariable Long id) {
        Fuel fuel = fuelService.getTransactionById(id);
        if (fuel != null) {
            return new ResponseEntity<>(fuel, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{id}")
    public Boolean deleteTransaction(@PathVariable Long id) {
        return fuelService.deleteTransaction(id);
    }




    @GetMapping("/amount/{id}")
    public List<Object[]> getTotalAmount(@PathVariable Long id) {
        return fuelService.getTotalAmount(id);
    }

    @GetMapping("/user/id/{id}")
    public List<Fuel> getFuelByUserId(@PathVariable Long id) {
        return fuelService.getFuelByUserId(id);
    }

}
