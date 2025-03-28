package com.isidrosantiago.booknetwork;

import com.isidrosantiago.booknetwork.role.Role;
import com.isidrosantiago.booknetwork.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository repository) {
		return args -> {
			if (repository.findByName("USER").isEmpty()) {
				repository.save(
					Role.builder().name("USER").build()
				);
			}
		};
	}

}
