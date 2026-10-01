package com.example.demowithswiggy.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.example.demowithswiggy.model.*;
public interface RestaurantRepo  extends JpaRepository<Restaurant,Integer>{
    long countByLocation(String location);
    Optional<Restaurant> findByNameIgnoreCase(String name);
}
