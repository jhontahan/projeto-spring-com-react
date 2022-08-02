package com.jhonathan.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jhonathan.exception.ErroAutenticacao;
import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.model.repository.UsuarioRepository;
import com.jhonathan.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	
	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario não encontrado para o e-mail informado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsaurio(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
		
	}

	@Override
	public void validarEmail(String email) {
		
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado come este email.");
		}
		
		
	}

}
