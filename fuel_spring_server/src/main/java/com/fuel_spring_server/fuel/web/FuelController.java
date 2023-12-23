package com.fuel_spring_server.fuel.web;


import com.fuel_spring_server.fuel.domain.Fuel;
import com.fuel_spring_server.fuel.services.FuelService;
import com.fuel_spring_server.fuel.services.impl.FuelServiceImpl;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fuel")
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

    @GetMapping("/{id}")
    public ResponseEntity<Fuel> getFuelById(@PathVariable Long id) {
        Fuel fuel = fuelService.getTransactionById(id);
        if (fuel != null) {
            return new ResponseEntity<>(fuel, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFuel(@PathParam(value = "id") Long id) {
        if (fuelService.deleteTransaction(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
