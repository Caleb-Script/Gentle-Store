package com.gentle.store.shopping.cart.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
@Setter
@Getter
@AllArgsConstructor
public class MailProps {
    private String from;
    private String to;
}
