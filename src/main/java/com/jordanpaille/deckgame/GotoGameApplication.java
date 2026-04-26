package com.jordanpaille.deckgame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GotoGameApplication {
	static void main(String[] args) {
		log.info("Starting GotoGameApplication");
		SpringApplication.run(com.jordanpaille.deckgame.GotoGameApplication.class, args);
	}

}
