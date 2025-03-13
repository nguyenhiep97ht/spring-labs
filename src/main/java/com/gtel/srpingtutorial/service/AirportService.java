package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.model.request.AirportRequest;
import com.gtel.srpingtutorial.model.response.AirportResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    public List<AirportResponse> getAirports(int page, int size) {
        return null;
    }

    public int countAirports() {
        return 0;
    }

    public AirportResponse getAirport(String iata) {
        return null;
    }

    public void createAirport(AirportRequest airportRequest) {
    }

    public void deleteAirport(String iata) {
    }

    public void updateAirports(String iata, AirportRequest airportRequest) {
    }

    public void updatePathAirports(String iata, AirportRequest airportRequest) {
    }
}
