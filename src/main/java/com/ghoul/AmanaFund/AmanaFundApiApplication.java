package com.ghoul.AmanaFund;

import com.ghoul.AmanaFund.entity.Role;
import com.ghoul.AmanaFund.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class AmanaFundApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmanaFundApiApplication.class, args);
	}
	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("ROLE_USER").isEmpty()) {
				roleRepository.save(Role.builder().name("ROLE_USER").build());
			}
			if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
				roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
			}
			if (roleRepository.findByName("ROLE_AUDITOR").isEmpty()) {
				roleRepository.save(Role.builder().name("ROLE_AUDITOR").build());
			}
			if (roleRepository.findByName("ROLE_AGENT").isEmpty()) {
				roleRepository.save(Role.builder().name("ROLE_AGENT").build());
			}
		};
	}
	}

