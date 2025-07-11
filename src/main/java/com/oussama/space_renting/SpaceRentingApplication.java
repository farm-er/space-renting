package com.oussama.space_renting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SpaceRentingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpaceRentingApplication.class, args);
	}

}
