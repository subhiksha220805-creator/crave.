package com.example.demowithswiggy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import com.example.demowithswiggy.dao.UserRepo;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity 
//important


public class SecurityConfig {
//	bean is like a container
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	  http
        .csrf(csrf -> csrf.disable())
        .securityContext(context -> context.securityContextRepository(securityContextRepository()))
//        csrf-removes duplicate or dosnt allows duplicates
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/customer/**").hasRole("CUSTOMER")
            .requestMatchers("/delivery/**").hasRole("DELIVERY_PARTNER")
            .anyRequest().permitAll()
        )
        .httpBasic(withDefaults());

    return http.build();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
	 @Bean
	    PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

    @Bean
    UserDetailsService userDetailsService(UserRepo users) {
        return email -> users.findByEmail(email)
            .map(user -> User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

	 
	 
	
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 }
