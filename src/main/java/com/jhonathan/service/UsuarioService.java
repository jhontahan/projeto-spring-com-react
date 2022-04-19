package com.jhonathan.service;

import com.jhonathan.model.entity.Usuario;

public interface UsuarioService {

	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsaurio(Usuario usuario);
	
	void validarEmail(String email);
}
