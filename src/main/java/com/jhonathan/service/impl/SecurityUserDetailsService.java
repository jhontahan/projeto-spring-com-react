package com.jhonathan.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jhonathan.model.entity.Usuario;
import com.jhonathan.model.repository.UsuarioRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService{

	private UsuarioRepository usuarioRepository;

	public SecurityUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
		
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Código para pegar os usuários de qualquer forma (txt, excel, bd)
		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado."));
		
		User user = (User) User.builder()
						.username(usuario.getEmail())
						.password(usuario.getSenha())
						.roles("USER")
						.build();
		
		return user;
						
	}

	
	
}
