package com.cresol.desafio_tecnico;

import org.springframework.boot.SpringApplication;

public class TestDesafioTecnicoApplication {

	public static void main(String[] args) {
		SpringApplication.from(DesafioTecnicoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
