package com.example.demowithswiggy.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demowithswiggy.dao.FoodItemRepo;
import com.example.demowithswiggy.dao.FoodOrderRepo;
import com.example.demowithswiggy.dao.RestaurantRepo;
import com.example.demowithswiggy.dao.UserRepo;
import com.example.demowithswiggy.model.FoodItem;
import com.example.demowithswiggy.model.FoodOrder;
import com.example.demowithswiggy.model.OrderStatus;
import com.example.demowithswiggy.model.Restaurant;
import com.example.demowithswiggy.model.User;
import com.example.demowithswiggy.service.PhoneOtpService;

@RestController
@RequestMapping("/api")
public class DemoApiController {
    private static final Set<String> HIDDEN_MENU_ITEMS = Set.of("ilaneer sarbath", "nongu sarbath");
    private final RestaurantRepo restaurantRepo;
    private final FoodItemRepo foodItemRepo;
    private final FoodOrderRepo foodOrderRepo;
    private final UserRepo userRepo;
    private final PhoneOtpService phoneOtpService;
    private final PasswordEncoder passwordEncoder;

    public DemoApiController(RestaurantRepo restaurantRepo, FoodItemRepo foodItemRepo,
            FoodOrderRepo foodOrderRepo, UserRepo userRepo, PhoneOtpService phoneOtpService,
            PasswordEncoder passwordEncoder) {
        this.restaurantRepo = restaurantRepo;
        this.foodItemRepo = foodItemRepo;
        this.foodOrderRepo = foodOrderRepo;
        this.userRepo = userRepo;
        this.phoneOtpService = phoneOtpService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/restaurants")
    public List<RestaurantCard> restaurants() {
        List<RestaurantCard> cards = new ArrayList<>();
        for (Restaurant restaurant : restaurantRepo.findAll()) {
            if (!"Madurai".equalsIgnoreCase(restaurant.getLocation())) continue;
            List<FoodItem> menu = foodItemRepo.findByRestaurantIdOrderByPriceAsc(restaurant.getId()).stream()
                    .filter(item -> !HIDDEN_MENU_ITEMS.contains(item.getName().toLowerCase(Locale.ROOT)))
                    .toList();
            if (menu.isEmpty()) continue;
            FoodItem popularPick = menu.get(0);
            cards.add(new RestaurantCard(restaurant.getId(), restaurant.getName(), restaurant.getCuisine(),
                    restaurant.getRating(), restaurant.getDeliveryTime(), (int) Math.round(popularPick.getPrice()),
                    restaurant.getOffer(), restaurant.getEmoji(), restaurant.getTone(), restaurant.getDescription(), restaurant.getArea(),
                    restaurant.isVeg(), restaurant.isFeatured(), popularPick.getId(), popularPick.getName(),
                    menu.stream().map(item -> new MenuCard(item.getId(), item.getName(),
                            (int) Math.round(item.getPrice()), item.getImageUrl())).toList()));
        }
        return cards;
    }

    @PostMapping("/orders/otp/send")
    public OtpMessage sendOrderOtp(@RequestBody OtpPhoneRequest request) {
        PhoneOtpService.OtpChallenge challenge = phoneOtpService.send(request.phone());
        return new OtpMessage("Verification code sent.",
                "******" + challenge.phone().substring(6), challenge.code());
    }

    @PostMapping("/orders/otp/verify")
    public OtpMessage verifyOrderOtp(@RequestBody OtpVerifyRequest request) {
        phoneOtpService.verify(request.phone(), request.code());
        return new OtpMessage("Phone number verified.", null, null);
    }

    @PostMapping("/orders")
    public OrderReceipt placeOrder(@RequestBody CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Add at least one item to your order.");
        }
        if (request.customerName() == null || request.customerName().isBlank()
                || request.customerName().trim().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter your name.");
        }
        String phone = phoneOtpService.requireVerified(request.phone());
        if (request.address() == null || request.address().trim().length() < 8
                || request.address().trim().length() > 600) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter your full delivery address.");
        }

        Map<Integer, FoodItem> selectedItems = new LinkedHashMap<>();
        Map<Integer, Integer> quantities = new LinkedHashMap<>();
        int subtotal = 0;
        for (OrderLine line : request.items()) {
            if (line.quantity() < 1 || line.quantity() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantity must be between 1 and 50.");
            }
            FoodItem item = foodItemRepo.findById(line.foodId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food item not found."));
            selectedItems.put(item.getId(), item);
            quantities.merge(item.getId(), line.quantity(), Integer::sum);
            subtotal += (int) Math.round(item.getPrice()) * line.quantity();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String customerEmail = authentication == null || authentication instanceof AnonymousAuthenticationToken
                ? "orders@crave.local" : authentication.getName();
        User customer = userRepo.findByEmail(customerEmail).orElseGet(() -> {
            User guest = new User();
            guest.setName("Crave Guest");
            guest.setEmail(customerEmail);
            guest.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
            guest.setRole(com.example.demowithswiggy.model.Role.CUSTOMER);
            return userRepo.save(guest);
        });
        int deliveryFee = subtotal >= 399 ? 0 : 25;
        FoodOrder order = new FoodOrder();
        order.setCustomer(customer);
        order.setCustomerName(request.customerName().trim());
        order.setCustomerPhone(phone);
        order.setDeliveryAddress(request.address().trim());
        order.setItems(new ArrayList<>(selectedItems.values()));
        order.setQuantities(quantities);
        order.setStatus(OrderStatus.PLACED);
        FoodOrder saved = foodOrderRepo.save(order);
        phoneOtpService.consume(phone);
        return new OrderReceipt(saved.getId(), saved.getStatus().name(), subtotal, deliveryFee, subtotal + deliveryFee);
    }

    public record RestaurantCard(int id, String name, String cuisine, double rating, int time,
            int price, String offer, String emoji, String tone, String description, String area,
            boolean veg, boolean featured, int foodId, String foodName, List<MenuCard> menu) { }
    public record MenuCard(int id, String name, int price, String imageUrl) { }
    public record OrderLine(int foodId, int quantity) { }
    public record OtpPhoneRequest(String phone) { }
    public record OtpVerifyRequest(String phone, String code) { }
    public record OtpMessage(String message, String maskedPhone, String verificationCode) { }
    public record CreateOrderRequest(List<OrderLine> items, String customerName, String phone, String address) { }
    public record OrderReceipt(int orderId, String status, int subtotal, int deliveryFee, int total) { }
}
