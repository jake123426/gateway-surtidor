package com.microservcio.gatewayreactivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayreactivoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayreactivoApplication.class, args);
	}

//	@Bean
//	CommandLineRunner commandLineRunner() {
//		return args -> {
//			HttpClient httpClient = new HttpClient();
//			httpClient.getUserByEmail("Cesia").subscribe(System.out::println);
//		};
//	}

}
