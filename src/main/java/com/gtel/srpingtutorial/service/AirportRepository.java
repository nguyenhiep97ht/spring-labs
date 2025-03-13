package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.model.data.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
}
