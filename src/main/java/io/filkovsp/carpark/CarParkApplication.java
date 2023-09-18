package io.filkovsp.carpark;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarParkApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CarParkApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Hello World!");

		/*
		TODO:
		    this runner can be extended later with some specific logic for a certain CarPark implementation
		    or, alternatively, application can be extended with REST controller.
		 */

	}

}
