package com.gtel.srpingtutorial.model.response;

import lombok.Data;

@Data
public class AirportResponse {
    private String iata;
    private String name;
    private String airportGroupCode;
    private String language;
    private Integer priority;
}
