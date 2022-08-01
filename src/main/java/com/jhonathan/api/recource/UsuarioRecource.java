package com.jhonathan.api.recource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioRecource {

	@GetMapping("/")
	public String hello(){
		return "hello";
	}
	
}
