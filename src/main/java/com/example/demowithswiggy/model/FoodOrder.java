package com.example.demowithswiggy.model;

import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Column;

@Entity
public class FoodOrder {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	    @ManyToOne
	    private User customer;

	    @ManyToOne
	    private User deliveryPartner;
	    private String customerName;
	    private String customerPhone;
	    @Column(length = 600)
	    private String deliveryAddress;

	    @ManyToMany
	    private List<FoodItem> items;

	    @ElementCollection
	    @CollectionTable(name = "food_order_quantities", joinColumns = @JoinColumn(name = "food_order_id"))
	    @MapKeyColumn(name = "food_item_id")
	    @Column(name = "quantity")
	    private Map<Integer, Integer> quantities;

	    @Enumerated(EnumType.STRING)
	    private OrderStatus status;

		public FoodOrder(User customer, User deliveryPartner, List<FoodItem> items, OrderStatus status) {
			super();
			this.customer = customer;
			this.deliveryPartner = deliveryPartner;
			this.items = items;
			this.status = status;
		}

		public FoodOrder() {
			super();
			// TODO Auto-generated constructor stub
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public User getCustomer() {
			return customer;
		}

		public void setCustomer(User customer) {
			this.customer = customer;
		}

		public User getDeliveryPartner() {
			return deliveryPartner;
		}

		public void setDeliveryPartner(User deliveryPartner) {
			this.deliveryPartner = deliveryPartner;
		}

		public String getCustomerName() { return customerName; }
		public void setCustomerName(String customerName) { this.customerName = customerName; }
		public String getCustomerPhone() { return customerPhone; }
		public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
		public String getDeliveryAddress() { return deliveryAddress; }
		public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

		public List<FoodItem> getItems() {
			return items;
		}

		public void setItems(List<FoodItem> items) {
			this.items = items;
		}

		public Map<Integer, Integer> getQuantities() { return quantities; }

		public void setQuantities(Map<Integer, Integer> quantities) { this.quantities = quantities; }

		public OrderStatus getStatus() {
			return status;
		}

		public void setStatus(OrderStatus status) {
			this.status = status;
		}
	    
	    
	    
	}


