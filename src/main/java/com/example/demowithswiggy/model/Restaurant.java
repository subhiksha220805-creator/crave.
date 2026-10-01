package com.example.demowithswiggy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    // Legacy DB compatibility only; actual menu prices belong to FoodItem.
    @Column(nullable = true)
    private double price;
    @ManyToOne
    @JsonIgnore
    private Restaurant restaurant;
    private String location;
    private String area;
    private String cuisine;
    private double rating;
    private int deliveryTime;
    private String offer;
    private String emoji;
    private String tone;
    private String description;
    private boolean veg;
    private boolean featured;

    public Restaurant() { }

    public Restaurant(String name, String location, String cuisine, double rating, int deliveryTime,
            String offer, String emoji, String tone, String description, boolean veg, boolean featured) {
        this.name = name;
        this.location = location;
        this.cuisine = cuisine;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.offer = offer;
        this.emoji = emoji;
        this.tone = tone;
        this.description = description;
        this.veg = veg;
        this.featured = featured;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    @JsonIgnore
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }
    public String getOffer() { return offer; }
    public void setOffer(String offer) { this.offer = offer; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isVeg() { return veg; }
    public void setVeg(boolean veg) { this.veg = veg; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
}
