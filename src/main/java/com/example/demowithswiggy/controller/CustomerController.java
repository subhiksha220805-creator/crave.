package com.example.demowithswiggy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demowithswiggy.dao.FoodItemRepo;
import com.example.demowithswiggy.dao.FoodOrderRepo;
import com.example.demowithswiggy.dao.UserRepo;
import com.example.demowithswiggy.model.FoodItem;
import com.example.demowithswiggy.model.FoodOrder;
import com.example.demowithswiggy.model.OrderStatus;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	   @Autowired
	   UserRepo ur;
	   @Autowired
	   FoodItemRepo fir;
	   @Autowired
	   FoodOrderRepo foor;
	   @GetMapping("/foods")
	    public List<FoodItem> viewFoods() {
	        return fir.findAll();
	        }
	   @PostMapping("/order/{customerId}")
	    public FoodOrder placeOrder(
	            @PathVariable int customerId,
	            @RequestBody List<Integer> foodIds) {

	        FoodOrder order = new FoodOrder();
	        order.setCustomer(ur.findById(customerId).get());
	        order.setItems(fir.findAllById(foodIds));
	        order.setStatus(OrderStatus.PLACED);

	        return foor.save(order);
	    }

}
