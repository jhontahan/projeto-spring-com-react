package com.jhonathan.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhonathan.api.dto.TokenDTO;
import com.jhonathan.api.dto.UsuarioDTO;
import com.jhonathan.exception.ErroAutenticacao;
import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.service.JwtService;
import com.jhonathan.service.LancamentoService;
import com.jhonathan.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

	private final UsuarioService usuarioService;
	private final LancamentoService lancamentoService;
	private final JwtService jwtService;
	
	
	//Não se utiliza mais o construtor pois como a variável do service é final,
	//obrigatoriamente o será criado um construtor com ela na anotação
	//@RequiredArgsConstructor
//	public UsuarioRecource(UsuarioService usuarioService) {
//		this.usuarioService = usuarioService;
//	}
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto) {
		
		try {
			Usuario usuario = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
			
			String tokenGerado = jwtService.gerarToken(usuario);
			TokenDTO tokenDTO = new TokenDTO(usuario.getNome(), tokenGerado);
			
			
			return ResponseEntity.ok(tokenDTO);
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
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = usuarioService.obterPorId(id);
		
		if (!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		
		return ResponseEntity.ok(saldo);
	}
	
	
}
