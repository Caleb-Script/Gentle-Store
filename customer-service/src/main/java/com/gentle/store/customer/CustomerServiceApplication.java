package com.gentle.store.customer;

import com.gentle.store.customer.config.ApplicationConfig;
import com.gentle.store.customer.dev.DevConfig;
import com.gentle.store.customer.util.Banner;
import com.gentle.store.customer.util.MailProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, DevConfig.class})
@EnableConfigurationProperties(MailProps.class)
public class CustomerServiceApplication {

	public static void main(final String... args) {
		final var app = new SpringApplication(CustomerServiceApplication.class);
		//noinspection unused
		app.setBanner((environment, sourceClass, out) -> out.println(Banner.TEXT));
		app.run(args);
	}

}
