package com.jhonathan.api.recource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhonathan.api.dto.UsuarioDTO;
import com.jhonathan.exception.ErroAutenticacao;
import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRecource {

	private UsuarioService usuarioService;

	public UsuarioRecource(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
		
		try {
			Usuario usuario = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuario);
		}catch(ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		
		Usuario usuario = Usuario.builder()
						  .nome(dto.getNome())
						  .email(dto.getEmail())
						  .senha(dto.getSenha())
						  .build();
		
		try {
			Usuario usuarioSalvo = usuarioService.salvarUsaurio(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		}catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	
}
