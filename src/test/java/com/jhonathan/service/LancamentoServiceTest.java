package com.jhonathan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Lancamento;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.model.enums.StatusLancamento;
import com.jhonathan.model.repository.LancamentoRepository;
import com.jhonathan.model.repository.LancamentoRepositoryTest;
import com.jhonathan.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmlancamento() {
		Lancamento lancamentoASalvar  = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo  = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}

	@Test
	public void naoDeveSalvarUmlancamentoQuandoHouverErroDeValidacao() {
		Lancamento lancamentoASalvar  = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
	
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	
	}
	
	@Test
	public void deveAtualizarUmlancamento() {
		Lancamento lancamentoSalvo  = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		Lancamento lancamento = service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentatAtualizarUmlancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar  = LancamentoRepositoryTest.criarLancamento();
	
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamentoASalvar  = LancamentoRepositoryTest.criarLancamento();
		lancamentoASalvar.setId(1l);
		
		service.deletar(lancamentoASalvar);
		
		Mockito.verify(repository).delete(lancamentoASalvar);
		
	}

	@Test
	public void deveLancarErroAoTentatDeletarUmlancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar  = LancamentoRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType(() -> service.deletar(lancamentoASalvar), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).delete(lancamentoASalvar);
		
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento  = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = java.util.Arrays.asList(lancamento);
		
		Mockito.when(repository.findAll(Mockito.any(org.springframework.data.domain.Example.class))).thenReturn(lista);
	
		List<Lancamento> result = service.buscar(lancamento);
		
		Assertions.assertThat(result).isNotEmpty().hasSize(1).contains(lancamento);
		
	}
	
	@Test
	public void deveAtualizarUmStatusDeUmLancamento() {
		Lancamento lancamento  = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		Long id = 1l;
		
		Lancamento lancamento  = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> result = service.obterPorId(id);
		
		Assertions.assertThat(result.isPresent()).isTrue();
		
	}

	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExiste() {
		Long id = 1l;
		
		Lancamento lancamento  = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> result = service.obterPorId(id);
		
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao("");
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		
		lancamento.setDescricao("salario");
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setMes(-1);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setMes(13);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(1);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		lancamento.setAno(202);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

		lancamento.setAno(2020);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");

		lancamento.setUsuario(new Usuario());
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");

		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1l);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

		lancamento.setValor(BigDecimal.valueOf(1));
		erro =  Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
		
	}
}
