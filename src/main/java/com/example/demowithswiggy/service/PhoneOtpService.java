package com.example.demowithswiggy.service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PhoneOtpService {
    private static final long OTP_LIFETIME_MILLIS = 5 * 60 * 1000L;
    private static final long RESEND_WAIT_MILLIS = 60 * 1000L;
    private static final int MAX_ATTEMPTS = 5;

    private final SecureRandom random = new SecureRandom();
    private final ConcurrentMap<String, Challenge> challenges = new ConcurrentHashMap<>();

    public String normalizePhone(String rawPhone) {
        if (rawPhone == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid 10-digit Indian phone number.");
        }
        String phone = rawPhone.replaceAll("\\D", "");
        if (phone.length() == 12 && phone.startsWith("91")) phone = phone.substring(2);
        if (!phone.matches("[6-9][0-9]{9}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid 10-digit Indian phone number.");
        }
        return phone;
    }

    public OtpChallenge send(String rawPhone) {
        String phone = normalizePhone(rawPhone);
        long now = System.currentTimeMillis();
        Challenge previous = challenges.get(phone);
        if (previous != null && now - previous.createdAt < RESEND_WAIT_MILLIS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Wait a minute before requesting another OTP.");
        }
        String code = String.format("%06d", random.nextInt(1_000_000));
        challenges.put(phone, new Challenge(code, now + OTP_LIFETIME_MILLIS, now));
        return new OtpChallenge(phone, code);
    }

    public void verify(String rawPhone, String code) {
        String phone = normalizePhone(rawPhone);
        Challenge challenge = challenges.get(phone);
        long now = System.currentTimeMillis();
        if (challenge == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired or not requested. Send a new OTP.");
        }
        synchronized (challenge) {
        if (now > challenge.expiresAt) {
            challenges.remove(phone);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired or not requested. Send a new OTP.");
        }
        if (challenge.attempts >= MAX_ATTEMPTS) {
            challenges.remove(phone);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many incorrect attempts. Request a new OTP later.");
        }
        challenge.attempts++;
        if (code == null || !challenge.code.equals(code.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That OTP is incorrect. Check the demo code shown on the page and try again.");
        }
        challenge.verified = true;
        }
    }

    public String requireVerified(String rawPhone) {
        String phone = normalizePhone(rawPhone);
        Challenge challenge = challenges.get(phone);
        if (challenge == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verify your phone number with OTP before placing the order.");
        }
        synchronized (challenge) {
            if (!challenge.verified || System.currentTimeMillis() > challenge.expiresAt) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verify your phone number with OTP before placing the order.");
            }
        }
        return phone;
    }

    public void consume(String phone) {
        challenges.remove(phone);
    }

    public record OtpChallenge(String phone, String code) { }

    private static final class Challenge {
        private final String code;
        private final long expiresAt;
        private final long createdAt;
        private int attempts;
        private boolean verified;

        private Challenge(String code, long expiresAt, long createdAt) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.createdAt = createdAt;
        }
    }
}
