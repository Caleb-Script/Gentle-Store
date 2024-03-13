package com.gentle.store.payment.dev;

import org.springframework.context.annotation.Profile;

@Profile(DevConfig.DEV)
public class DevConfig implements Flyway {
    /**
     * Konstante für das Spring-Profile "dev".
     */
    public static final String DEV = "dev";

    DevConfig() {
    }
}
