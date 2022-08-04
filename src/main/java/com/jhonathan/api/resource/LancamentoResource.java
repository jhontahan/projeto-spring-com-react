package com.jhonathan.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jhonathan.api.dto.AtualizarStatusDTO;
import com.jhonathan.api.dto.LancamentoDTO;
import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Lancamento;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.model.enums.StatusLancamento;
import com.jhonathan.model.enums.TipoLancamento;
import com.jhonathan.service.LancamentoService;
import com.jhonathan.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	//Não se utiliza mais o construtor pois como a variável do service é final,
	//obrigatoriamente o será criado um construtor com ela na anotação
	//@RequiredArgsConstructor
//	public LancamentoRecource(LancamentoService service, UsuarioService usuarioService) {
//		this.service = service;
//		this.usuarioService = usuarioService;
//	}
	
	
	//RequestParam é usado para receber parâmetros em uma busca. 
	//Usa-se o requirid = false para que ese parâmetro não seja obrigatório.
	
	
	//Exemplo de como usar via objeto JSON para realizar a consulta.
//	@GetMapping
//	public ResponseEntity buscar(@RequestBody LancamentoDTO dto) {
//		
//		Lancamento lancamentoFiltro = converter(dto);
//		
//		if (lancamentoFiltro.getUsuario() == null) {
//			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado.");
//		}
//		else {
//			lancamentoFiltro.setUsuario(usuario.get());
//		}
//		
//		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
//		
//		return ResponseEntity.ok(lancamentos);
//		
//	}
	
	@GetMapping
	public ResponseEntity buscar(@RequestParam(value="descricao", required = false) String descricao,
								 @RequestParam(value="mes", required = false) Integer mes,
								 @RequestParam(value="ano", required = false) Integer ano,
								 @RequestParam("usuario") Long idUsuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setMes(mes);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado.");
		}
		else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		Lancamento lancamento = converter(dto);
		
		lancamento = service.salvar(lancamento);
		
		try {
			return ResponseEntity.ok(lancamento);
		}catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizarStatusDTO dto) {
		return service.obterPorId(id).map(entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			}catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map(entity ->{
			
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			}catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
		
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entity ->{
			
			try {
				service.deletar(entity);
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
		
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado."));
		
		lancamento.setUsuario(usuario);
		
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}

		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		
		return lancamento;
	}
	
}
