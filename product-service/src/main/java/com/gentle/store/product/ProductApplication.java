package com.gentle.store.product;

import com.gentle.store.product.config.ApplicationConfig;
import com.gentle.store.product.dev.DevConfig;
import com.gentle.store.product.util.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApplicationConfig.class, DevConfig.class})
public class ProductApplication {
    public static void main(final String... args) {
        final var app = new SpringApplication(ProductApplication.class);
        //noinspection unused
        app.setBanner((environment, sourceClass, out) -> out.println(Banner.TEXT));
        app.run(args);
    }

}
