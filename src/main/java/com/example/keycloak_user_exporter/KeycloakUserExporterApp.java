package com.example.keycloak_user_exporter;

import com.example.keycloak_user_exporter.service.KeycloakService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KeycloakUserExporterApp {

	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		ConfigurableApplicationContext context = SpringApplication.run(KeycloakUserExporterApp.class, args);
		//SpringApplication.run(KeycloakUserExporterApp.class, args);
		System.out.println("Go to http://localhost:8091/");

		KeycloakService keycloakService = context.getBean(KeycloakService.class);

		String token = keycloakService.authenticate("admin", "admin");

		String result = keycloakService.printUsersFormatted(token);
		System.out.println(result);

	}

}
