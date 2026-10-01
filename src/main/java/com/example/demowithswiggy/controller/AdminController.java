package com.example.demowithswiggy.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.demowithswiggy.model.*;
import com.example.demowithswiggy.dao.*;
@RestController
@RequestMapping("/admin")
public class AdminController {
   @Autowired   
   RestaurantRepo rr;
   @Autowired
   UserRepo ur;
   @Autowired
   FoodItemRepo fir;
   @Autowired
   FoodOrderRepo foor;
    @PostMapping("/restaurant")
    public Restaurant addRestaurant(@RequestBody Restaurant r) {
        return rr.save(r);
    }

    @PostMapping("/food")
    public FoodItem addFood(@RequestBody NewFoodItem request) {
        Restaurant restaurant = rr.findById(request.restaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found."));
        FoodItem food = new FoodItem(request.name(), request.price(), restaurant);
        return fir.save(food);
    }

    @PutMapping("/assign/{orderId}/{partnerId}")
    public FoodOrder assignDelivery(
            @PathVariable int orderId,
            @PathVariable int partnerId) {

        FoodOrder order = foor.findById(orderId).get();
        User partner = ur.findById(partnerId).get();

        order.setDeliveryPartner(partner);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        return foor.save(order);
    }

    public record NewFoodItem(String name, double price, int restaurantId) { }
}
