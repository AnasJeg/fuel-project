package com.fuel_spring_server;

import com.fuel_spring_server.fuel.domain.Fuel;
import com.fuel_spring_server.fuel.services.FuelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@Rollback
public class FuelControllerTest {


    private int port=8010;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FuelService fuelService;

    @Test
    public void testSaveFuel() {
        Fuel newFuel = new Fuel();
        newFuel.setType("Gasoil");
        newFuel.setPrice(22.6);
        newFuel.setTotale(30.0);
        newFuel.setLitre(12.0);
        newFuel.setDate(new Date());


        ResponseEntity<Fuel> response = restTemplate.postForEntity("http://localhost:" + port + "/api/fuel/", newFuel, Fuel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Fuel savedFuel = response.getBody();
        assertNotNull(savedFuel);

    }

    @Test
    public void testGetAllFuel() {
        ResponseEntity<Fuel[]> response = restTemplate.getForEntity("http://localhost:" + port + "/api/fuel/", Fuel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Fuel[] fuels = response.getBody();
        assertNotNull(fuels);

    }

    @Test
    public void testGetFuelById() {
        ResponseEntity<Fuel[]> allFuelsResponse = restTemplate.getForEntity("http://localhost:" + port + "/api/fuel/", Fuel[].class);
        Fuel[] allFuels = allFuelsResponse.getBody();

        long fuelIdToFind = allFuels[0].getId();
        ResponseEntity<Fuel> response = restTemplate.getForEntity("http://localhost:" + port + "/api/fuel/" + fuelIdToFind, Fuel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Fuel fuel = response.getBody();
        assertNotNull(fuel);

    }

    @Test
    public void testDeleteFuel() {

        Fuel fuelToDelete = new Fuel();
        fuelToDelete.setType("Diesel");
        fuelToDelete.setPrice(2.0);
        fuelToDelete.setTotale(20.0);
        fuelToDelete.setLitre(10.0);
        fuelToDelete.setDate(new Date());
        Fuel savedFuel = fuelService.save(fuelToDelete);

        long fuelIdToDelete = savedFuel.getId();
        restTemplate.delete("http://localhost:" + port + "/api/fuel/delete?id=" + fuelIdToDelete);


        Fuel deletedFuel = fuelService.getTransactionById(fuelIdToDelete);
        assertNull(deletedFuel); // Assuming your service method returns null for non-existing entries
    }
}