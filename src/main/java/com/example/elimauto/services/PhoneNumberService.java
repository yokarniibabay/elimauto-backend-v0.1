package com.example.elimauto.services;

import org.springframework.stereotype.Service;

@Service
public class PhoneNumberService {
    public String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }

        if (phoneNumber.startsWith("8")) {
            return "+7" + phoneNumber.substring(1);
        }

        return "+" + phoneNumber;
    }
}
