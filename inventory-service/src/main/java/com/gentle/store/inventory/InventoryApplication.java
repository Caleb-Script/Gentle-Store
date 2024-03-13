package com.gentle.store.inventory;

import com.gentle.store.inventory.config.ApplicationConfig;
import com.gentle.store.inventory.dev.DevConfig;
import com.gentle.store.inventory.util.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApplicationConfig.class, DevConfig.class})
public class InventoryApplication {
    public static void main(final String... args) {
        final var app = new SpringApplication(InventoryApplication.class);
        //noinspection unused
        app.setBanner((environment, sourceClass, out) -> out.println(Banner.TEXT));
        app.run(args);
    }

}
