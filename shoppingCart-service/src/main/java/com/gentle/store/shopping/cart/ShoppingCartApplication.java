package com.gentle.store.shopping.cart;

import com.gentle.store.shopping.cart.config.ApplicationConfig;
import com.gentle.store.shopping.cart.dev.DevConfig;
import com.gentle.store.shopping.cart.util.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApplicationConfig.class, DevConfig.class})
public class ShoppingCartApplication {
    public static void main(final String... args) {
        final var app = new SpringApplication(ShoppingCartApplication.class);
        //noinspection unused
        app.setBanner((environment, sourceClass, out) -> out.println(Banner.TEXT));
        app.run(args);
    }

}
