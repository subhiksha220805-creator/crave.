package com.example.demowithswiggy.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.demowithswiggy.model.*;
public interface FoodItemRepo extends JpaRepository<FoodItem,Integer>{
    List<FoodItem> findByRestaurantIdOrderByPriceAsc(int restaurantId);
}
