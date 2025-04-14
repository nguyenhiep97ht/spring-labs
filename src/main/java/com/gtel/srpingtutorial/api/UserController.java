package com.gtel.srpingtutorial.api;

import com.gtel.srpingtutorial.model.data.Airport;
import com.gtel.srpingtutorial.model.request.AirportRequest;
import com.gtel.srpingtutorial.model.response.AirportResponse;
import com.gtel.srpingtutorial.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/airports")
public class UserController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    public Page<Airport> getAirports(@RequestParam int page, @RequestParam int size) {
        return airportService.getAirportsByPage(page, size);
    }


    @GetMapping("/{iata}")
    public Airport getAirport(@PathVariable Long id) {
        return (airportService.getAirportById(id).get());
    }

    @PostMapping
    public void createAirport(@RequestBody Airport airportRequest) {
        airportService.saveAirport(airportRequest);
    }

    @PutMapping("/{id}")
    public void updateAirport(@PathVariable Long id, @RequestBody Airport airportRequest) {
        airportService.updateAirport(id, airportRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
    }
}
