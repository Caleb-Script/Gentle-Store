package com.gentle.store.order;

import com.gentle.store.order.config.ApplicationConfig;
import com.gentle.store.order.dev.DevConfig;
import com.gentle.store.order.util.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, DevConfig.class})
public class OrderApplication {

    public static void main(String[] args) {
        final var app = new SpringApplication(OrderApplication.class);
        //noinspection unused
        app.setBanner((environment, sourceClass, out) -> out.println(Banner.TEXT));
        app.run(args);
    }

}
