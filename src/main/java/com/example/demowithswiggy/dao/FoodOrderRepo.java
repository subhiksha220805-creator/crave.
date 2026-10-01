package com.example.demowithswiggy.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demowithswiggy.model.FoodOrder;

public interface FoodOrderRepo extends JpaRepository<FoodOrder,Integer> {
	 List<FoodOrder> findByDeliveryPartnerId(int id);
	}



































