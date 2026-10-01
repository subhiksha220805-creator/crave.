package com.example.demowithswiggy.service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demowithswiggy.dao.FoodItemRepo;
import com.example.demowithswiggy.dao.RestaurantRepo;
import com.example.demowithswiggy.dao.UserRepo;
import com.example.demowithswiggy.model.FoodItem;
import com.example.demowithswiggy.model.Restaurant;
import com.example.demowithswiggy.model.Role;
import com.example.demowithswiggy.model.User;

@Configuration
public class DemoDataInitializer {
    @Bean
    CommandLineRunner loadDemoData(RestaurantRepo restaurants, FoodItemRepo foods,
            UserRepo users, PasswordEncoder passwordEncoder) {
        return args -> {
            if (restaurants.countByLocation("Madurai") == 0) {
                Restaurant konar = addRestaurant(restaurants, new Restaurant("Simmakkal Konar Kadai", "Madurai", "Madurai specials · Non-veg", 4.8, 30,
                        "KARI DOSA SPECIAL", "🥘", "#f4dfc8", "Known for Madurai-style kari dosa", false, true));
                addFood(foods, konar, "Madurai Kari Dosa", 220);
                addFood(foods, konar, "Mutton Kola Urundai", 180);

                Restaurant murugan = addRestaurant(restaurants, new Restaurant("Murugan Idli Shop", "Madurai", "South Indian · Tiffin", 4.7, 25,
                        "IDLI & CHUTNEY", "🥞", "#f3e9ce", "Soft idli with chutneys and sambar", true, true));
                addFood(foods, murugan, "Idli with Chutneys", 90);
                addFood(foods, murugan, "Podi Idli", 110);
                addFood(foods, murugan, "Ghee Pongal", 120);

                Restaurant jigarthanda = addRestaurant(restaurants, new Restaurant("Famous Jigarthanda", "Madurai", "Drinks · Dessert", 4.8, 20,
                        "MADURAI FAVOURITE", "🥤", "#e9e8d6", "A cool Madurai-style milk dessert", true, true));
                addFood(foods, jigarthanda, "Classic Jigarthanda", 90);
                addFood(foods, jigarthanda, "Special Jigarthanda", 130);

                Restaurant chandran = addRestaurant(restaurants, new Restaurant("Chandran Mess", "Madurai", "Madurai specials · Non-veg", 4.6, 35,
                        "MESS SPECIAL", "🍛", "#f5dfdc", "Traditional Madurai mess-style dishes", false, true));
                addFood(foods, chandran, "Mutton Kola Urundai", 190);
                addFood(foods, chandran, "Chicken Chukka", 220);
                addFood(foods, chandran, "Madurai Meals", 180);

                Restaurant bunParotta = addRestaurant(restaurants, new Restaurant("Madurai Bun Parotta Kadai", "Madurai", "Parotta · Local favourite", 4.5, 28,
                        "BUN PAROTTA COMBO", "🫓", "#eee0d5", "Fluffy bun parotta with curry", false, true));
                addFood(foods, bunParotta, "Bun Parotta", 70);
                addFood(foods, bunParotta, "Bun Parotta with Salna", 130);
                addFood(foods, bunParotta, "Kothu Parotta", 160);

                Restaurant sriram = addRestaurant(restaurants, new Restaurant("Sriram Mess", "Madurai", "South Indian · Vegetarian", 4.6, 30,
                        "VEG MEALS", "🥗", "#e6eddf", "Banana-leaf vegetarian meals", true, false));
                addFood(foods, sriram, "Vegetarian Thali", 160);
                addFood(foods, sriram, "Masala Vadai", 50);

                Restaurant burma = addRestaurant(restaurants, new Restaurant("Burma Idiyappam Kadai", "Madurai", "South Indian · Tiffin", 4.5, 24,
                        "IDIYAPPAM & COCONUT MILK", "🍜", "#f5e8d9", "Idiyappam with tomato chutney and coconut milk", true, false));
                addFood(foods, burma, "Idiyappam Set", 100);
                addFood(foods, burma, "Sweet Coconut Milk", 45);

                Restaurant sabarees = addRestaurant(restaurants, new Restaurant("Sree Sabarees", "Madurai", "South Indian · Vegetarian", 4.7, 25,
                        "PURE VEG", "🍲", "#f3ead0", "Vegetarian tiffin and South Indian meals", true, false));
                addFood(foods, sabarees, "Mini Tiffin", 150);
                addFood(foods, sabarees, "Rava Dosa", 130);
                addFood(foods, sabarees, "Filter Coffee", 45);
            }

            seedMaduraiMenus(restaurants, foods);

            if (users.findByEmail("orders@crave.local").isEmpty()) {
                User customer = new User();
                customer.setName("Crave Guest");
                customer.setEmail("orders@crave.local");
                customer.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                customer.setRole(Role.CUSTOMER);
                users.save(customer);
            }
        };
    }

    private Restaurant addRestaurant(RestaurantRepo restaurants, Restaurant restaurant) {
        return restaurants.save(restaurant);
    }

    private void addFood(FoodItemRepo foods, Restaurant restaurant, String name, double price) {
        foods.save(new FoodItem(name, price, restaurant));
    }

    private void seedMaduraiMenus(RestaurantRepo restaurants, FoodItemRepo foods) {
        String kariDosa = "/images/mutton-kari-dosa-user.png";
        String eggDosa = "/images/egg-dosa-user.png";
        String idli = "/images/idli.jpg";
        String podiIdli = "/images/podi-idli-user.png";
        String jigarthanda = "/images/jigarthanda.jpg";
        String kola = "/images/mutton-kola-urundai-user.png";
        String bunParotta = "/images/bun-parotta-user.png";
        String idiyappam = "/images/idiyappam.jpg";
        String idiyappamCurry = "/images/idiyappam-curry-user.png";
        String idiyappamSet = "/images/idiyappam-set-user.png";
        String idiyappamCoconutMilk = "/images/idiyappam-coconut-milk-user.png";
        String sweetCoconutMilk = "/images/sweet-coconut-milk-user.png";
        String ravaDosa = "/images/rava-dosa-user.png";
        String thali = "/images/thali.jpg";
        String southIndianMeals = "/images/south-indian-meals-user.png";
        String pongal = "/images/pongal.jpg";
        String coffee = "/images/filter-coffee.jpg";
        String biryani = "/images/biryani.jpg";
        String curry = "/images/chicken-chukka.jpg";
        String muttonChukka = "/images/mutton-chukka.jpg";
        String brainRoast = "/images/brain-roast.jpg";
        String muttonLiver = "/images/mutton-liver.jpg";
        String nannariSarbath = "/images/nannari-sarbath.jpg";
        String roseMilk = "/images/rosemilk-user.png";
        String badamMilk = "/images/badam-milk-user.png";
        String kothu = "/images/user-kothu-parotta.png";
        String eggKothu = "/images/egg-kothu-parotta-user.png";
        String curdRice = "/images/curd-rice-user.png";
        String parottaPhoto = "/images/user-parotta.png";
        String nonVegMeals = "/images/user-nonveg-meals.png";
        String vada = "/images/vadai.jpg";
        String sabareesVada = "/images/masala-vadai-alt.jpg";

        restaurants.findByNameIgnoreCase("Simmakal Konar Kadai").ifPresent(legacy -> {
            legacy.setName("Simmakkal Konar Kadai");
            restaurants.save(legacy);
        });

        Restaurant muniyandi = ensureRestaurant(restaurants, "Muniyandi Vilas – KK Nagar", "Madurai",
                "Non-veg · South Indian", 4.5, 30, "MADURAI SPECIALS", "🍗", "#f4dfc8",
                "South Indian, biryani and street-food favourites", false, true, "Opp. MGR Bus Stand, Melur Main Road");
        ensureFood(foods, muniyandi, "Mutton Brain Roast", 220, brainRoast);
        ensureFood(foods, muniyandi, "Chicken Chukka Gravy", 210, curry);
        ensureFood(foods, muniyandi, "Mutton Chukka Gravy", 260, muttonChukka);
        ensureFood(foods, muniyandi, "Chicken Biryani", 240, biryani);
        ensureFood(foods, muniyandi, "Egg Kothu Parotta", 160, eggKothu);
        ensureFood(foods, muniyandi, "Plain Kothu Parotta", 150, kothu);
        ensureFood(foods, muniyandi, "Chicken Kothu Parotta", 220, kothu);
        ensureFood(foods, muniyandi, "Mutton Kothu Parotta", 280, kothu);
        ensureFood(foods, muniyandi, "Mixed Kothu Parotta", 320, kothu);

        Restaurant konar = ensureRestaurant(restaurants, "Simmakkal Konar Kadai", "Madurai",
                "Madurai specials · Non-veg", 4.8, 30, "KARI DOSA SPECIAL", "🥘", "#f4dfc8",
                "Known for Madurai-style kari dosa", false, true, "Simmakkal");
        ensureFood(foods, konar, "Madurai Kari Dosa", 220, kariDosa);
        ensureFood(foods, konar, "Egg Dosa", 130, eggDosa);
        ensureFood(foods, konar, "Mutton Chukka", 250, muttonChukka);
        ensureFood(foods, konar, "Mutton Kola Urundai", 180, kola);
        ensureFood(foods, konar, "Mutton Liver Fry", 230, muttonLiver);

        Restaurant murugan = ensureRestaurant(restaurants, "Murugan Idli Shop", "Madurai",
                "South Indian · Tiffin", 4.7, 25, "IDLI & CHUTNEY", "🥞", "#f3e9ce",
                "Soft idli with chutneys and sambar", true, true, "West Masi Street");
        ensureFood(foods, murugan, "Idli with Chutneys", 90, idli);
        ensureFood(foods, murugan, "Podi Idli", 110, podiIdli);
        ensureFood(foods, murugan, "Ghee Pongal", 120, pongal);
        ensureFood(foods, murugan, "Ghee Dosa", 130, kariDosa);
        ensureFood(foods, murugan, "Filter Coffee", 45, coffee);

        Restaurant jigarthandaHotel = ensureRestaurant(restaurants, "Famous Jigarthanda", "Madurai",
                "Drinks · Dessert", 4.8, 20, "MADURAI FAVOURITE", "🥤", "#e9e8d6",
                "A cool Madurai-style milk dessert", true, true, "Vilakkuthoon");
        ensureFood(foods, jigarthandaHotel, "Classic Jigarthanda", 90, jigarthanda);
        ensureFood(foods, jigarthandaHotel, "Special Jigarthanda", 130, jigarthanda);
        ensureFood(foods, jigarthandaHotel, "Jigarthanda with Ice Cream", 150, jigarthanda);
        ensureFood(foods, jigarthandaHotel, "Nannari Sarbath", 60, nannariSarbath);
        ensureFood(foods, jigarthandaHotel, "Rosemilk", 70, roseMilk);
        ensureFood(foods, jigarthandaHotel, "Badam Milk", 80, badamMilk);

        Restaurant chandran = ensureRestaurant(restaurants, "Chandran Mess", "Madurai",
                "Madurai specials · Non-veg", 4.6, 35, "MESS SPECIAL", "🍛", "#f5dfdc",
                "Traditional Madurai mess-style dishes", false, true, "Alagar Kovil Main Road");
        ensureFood(foods, chandran, "Mutton Kola Urundai", 190, kola);
        ensureFood(foods, chandran, "Chicken Chukka", 220, curry);
        ensureFood(foods, chandran, "Madurai Meals", 180, thali);
        ensureFood(foods, chandran, "Mutton Chukka", 260, muttonChukka);
        ensureFood(foods, chandran, "Chicken Biryani", 240, biryani);
        ensureFood(foods, chandran, "Non-Veg Meals", 280, nonVegMeals);

        Restaurant bun = ensureRestaurant(restaurants, "Madurai Bun Parotta Kadai", "Madurai",
                "Parotta · Local favourite", 4.5, 28, "BUN PAROTTA COMBO", "🫓", "#eee0d5",
                "Fluffy bun parotta with curry", false, true, "Madurai");
        ensureFood(foods, bun, "Bun Parotta", 70, parottaPhoto);
        ensureFood(foods, bun, "Bun Parotta with Salna", 130, parottaPhoto);
        ensureFood(foods, bun, "Egg Kothu Parotta", 160, eggKothu);
        ensureFood(foods, bun, "Chicken Chukka", 220, curry);
        ensureFood(foods, bun, "Mutton Chukka", 260, muttonChukka);

        Restaurant sriram = ensureRestaurant(restaurants, "Sriram Mess", "Madurai",
                "South Indian · Vegetarian", 4.6, 30, "VEG MEALS", "🥗", "#e6eddf",
                "Banana-leaf vegetarian meals", true, false, "Madurai");
        ensureFood(foods, sriram, "Vegetarian Thali", 160, thali);
        ensureFood(foods, sriram, "Mini Tiffin", 140, idli);
        ensureFood(foods, sriram, "Masala Vadai", 50, vada);
        ensureFood(foods, sriram, "Curd Rice", 90, curdRice);
        ensureFood(foods, sriram, "Filter Coffee", 45, coffee);

        Restaurant burma = ensureRestaurant(restaurants, "Burma Idiyappam Kadai", "Madurai",
                "South Indian · Tiffin", 4.5, 24, "IDIYAPPAM & COCONUT MILK", "🍜", "#f5e8d9",
                "Idiyappam with chutney and coconut milk", true, false, "Mahaboopalayam");
        ensureFood(foods, burma, "Idiyappam Set", 100, idiyappamSet);
        ensureFood(foods, burma, "Idiyappam with Coconut Milk", 120, idiyappamCoconutMilk);
        ensureFood(foods, burma, "Idiyappam with Curry", 150, idiyappamCurry);
        ensureFood(foods, burma, "Sweet Coconut Milk", 45, sweetCoconutMilk);

        Restaurant sabarees = ensureRestaurant(restaurants, "Sree Sabarees", "Madurai",
                "South Indian · Vegetarian", 4.7, 25, "PURE VEG", "🍲", "#f3ead0",
                "Vegetarian tiffin and South Indian meals", true, false, "Town Hall Road");
        ensureFood(foods, sabarees, "Mini Tiffin", 150, idli);
        ensureFood(foods, sabarees, "Rava Dosa", 130, ravaDosa);
        ensureFood(foods, sabarees, "Filter Coffee", 45, coffee);
        ensureFood(foods, sabarees, "South Indian Meals", 180, southIndianMeals);
        ensureFood(foods, sabarees, "Masala Vadai", 50, sabareesVada);
    }

    private Restaurant ensureRestaurant(RestaurantRepo repo, String name, String city, String cuisine,
            double rating, int deliveryTime, String offer, String emoji, String tone, String description,
            boolean veg, boolean featured, String area) {
        Restaurant restaurant = repo.findByNameIgnoreCase(name).orElseGet(() ->
                addRestaurant(repo, new Restaurant(name, city, cuisine, rating, deliveryTime,
                        offer, emoji, tone, description, veg, featured)));
        restaurant.setLocation(city);
        restaurant.setArea(area);
        restaurant.setCuisine(cuisine);
        restaurant.setRating(rating);
        restaurant.setDeliveryTime(deliveryTime);
        restaurant.setOffer(offer);
        restaurant.setEmoji(emoji);
        restaurant.setTone(tone);
        restaurant.setDescription(description);
        restaurant.setVeg(veg);
        restaurant.setFeatured(featured);
        return repo.save(restaurant);
    }

    private void ensureFood(FoodItemRepo repo, Restaurant restaurant, String name, double price, String imageUrl) {
        FoodItem food = repo.findByRestaurantIdOrderByPriceAsc(restaurant.getId()).stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> new FoodItem(name, price, restaurant));
        food.setPrice(price);
        food.setImageUrl(imageUrl);
        repo.save(food);
    }

}
