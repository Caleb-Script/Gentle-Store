package com.gentle.store.customer.util;

import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.entity.PhoneNumber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLog {

    public static void logActivity(Customer customer, String kp) {
        customer.getActivities().forEach(activity -> {
            log.trace("{} Activity: id={}, zeit={}, typ={}, content={}",
                    kp,
                    activity.getId() != null ? activity.getId():"null",
                    activity.getTimestamp(),
                    activity.getActivityType(),
                    activity.getContent());
        });
        log.info("ENDE");
    }

    public static void logPhoneNumberList(Customer customer, String kp) {
        customer.getPhoneNumberList().forEach(phoneNumber -> {
            log.trace("{} phone number: id={}, Nummer: {}/{}",
                    kp,
                    phoneNumber.getId() != null ? phoneNumber.getId():"null",
                    phoneNumber.getDialingCode(),
                    phoneNumber.getNumber());
        });
        log.info("ENDE");
    }
    public static void logNumber2(PhoneNumber phoneNumber) {

            log.trace("phone number2: id={}, Nummer: {}/{}",
                    phoneNumber.getId(),
                    phoneNumber.getDialingCode(),
                    phoneNumber.getNumber());
        log.info("ENDE");
    }
}
