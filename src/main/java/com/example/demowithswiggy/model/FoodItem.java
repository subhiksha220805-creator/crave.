package com.example.demowithswiggy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class FoodItem {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	private String name;
    private double price;
    private String imageUrl;

    @ManyToOne
    private Restaurant restaurant;

	public FoodItem(String name, double price, Restaurant restaurant) {
		super();
		this.name = name;
		this.price = price;
		this.restaurant = restaurant;
	}

	public FoodItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
    


}
