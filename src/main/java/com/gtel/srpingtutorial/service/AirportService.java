package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.model.data.Airport;
import com.gtel.srpingtutorial.model.request.AirportRequest;
import com.gtel.srpingtutorial.model.response.AirportResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

//    @Autowired
//    private EntityManager entityManager;

    //  JPA
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Page<Airport> getAirportsByPage(int page, int pageSize) {
        return airportRepository.findAll(PageRequest.of(page, pageSize));
    }


    public Optional<Airport> getAirportById(Long id) {
        return airportRepository.findById(id);
    }

    public Airport saveAirport(Airport airport) {
        return airportRepository.save(airport);
    }

    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }

    public void updateAirport(Long id, Airport airport) {
        if (airportRepository.existsById(id)) {
            airport.setId(id);
             airportRepository.save(airport);
        }
    }

    // Native Query
//    @Transactional
//    public List<Airport> getAllAirportsNative() {
//        Query query = entityManager.createNativeQuery("SELECT * FROM air_port", Airport.class);
//        return query.getResultList();
//    }

//    @Transactional
//    public List<Airport> getAirportsByPageNative(int page, int pageSize) {
//        Query query = entityManager.createNativeQuery("SELECT * FROM air_port LIMIT :limit OFFSET :offset", Airport.class);
//        query.setParameter("limit", pageSize);
//        query.setParameter("offset", page * pageSize);
//        return query.getResultList();
//    }

//    @Transactional
//    public Airport getAirportByIdNative(Long id) {
//        Query query = entityManager.createNativeQuery("SELECT * FROM air_port WHERE id = :id", Airport.class);
//        query.setParameter("id", id);
//        return (Airport) query.getSingleResult();
//    }

//    @Transactional
//    public void saveAirportNative(Airport airport) {
//        Query query = entityManager.createNativeQuery("INSERT INTO air_port (iata, name, airportGroupCode, language, priority) VALUES (:iata, :name, :airportGroupCode, :language, :priority)");
//        query.setParameter("iata", airport.getIata());
//        query.setParameter("name", airport.getName());
//        query.setParameter("airportGroupCode", airport.getAirportGroupCode());
//        query.setParameter("language", airport.getLanguage());
//        query.setParameter("priority", airport.getPriority());
//        query.executeUpdate();
//    }

//    @Transactional
//    public void deleteAirportNative(Long id) {
//        Query query = entityManager.createNativeQuery("DELETE FROM air_port WHERE id = :id");
//        query.setParameter("id", id);
//        query.executeUpdate();
//    }
//
//    // Hibernate
//    @Transactional
//    public List<Airport> getAllAirportsHibernate() {
//        return entityManager.unwrap(org.hibernate.Session.class).createQuery("FROM Airport", Airport.class).list();
//    }
//
//    @Transactional
//    public List<Airport> getAirportsByPageHibernate(int page, int pageSize) {
//        return entityManager.unwrap(org.hibernate.Session.class)
//                .createQuery("FROM Airport", Airport.class)
//                .setFirstResult(page * pageSize)
//                .setMaxResults(pageSize)
//                .list();
//    }
//
//    @Transactional
//    public Airport getAirportByIdHibernate(Long id) {
//        return entityManager.unwrap(org.hibernate.Session.class).get(Airport.class, id);
//    }
//
//    @Transactional
//    public void saveAirportHibernate(Airport airport) {
//        entityManager.unwrap(org.hibernate.Session.class).saveOrUpdate(airport);
//    }
//
//    @Transactional
//    public void deleteAirportHibernate(Long id) {
//        Airport airport = entityManager.unwrap(org.hibernate.Session.class).get(Airport.class, id);
//        if (airport != null) {
//            entityManager.unwrap(org.hibernate.Session.class).delete(airport);
//        }
//    }
}

