package com.sejongmento.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SejongmentoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SejongmentoBackendApplication.class, args);
	}

}
