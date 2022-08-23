package com.jhonathan.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jhonathan.exception.ErroAutenticacao;
import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.model.repository.UsuarioRepository;
import com.jhonathan.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	
	private UsuarioRepository repository;
	private PasswordEncoder encoder;
	
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario não encontrado para o e-mail informado.");
		}
		
		boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());
		
		if(!senhasBatem) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsaurio(Usuario usuario) {
		validarEmail(usuario.getEmail());
		
		criptografarSenha(usuario);
		
		return repository.save(usuario);
		
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaCripto = encoder.encode(senha);
		usuario.setSenha(senhaCripto);
	}

	@Override
	public void validarEmail(String email) {
		
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado come este email.");
		}
		
		
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

}
