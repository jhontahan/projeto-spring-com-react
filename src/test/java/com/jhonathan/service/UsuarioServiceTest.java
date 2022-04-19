package com.jhonathan.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jhonathan.exception.ErroAutenticacao;
import com.jhonathan.exception.RegraNegocioException;
import com.jhonathan.model.entity.Usuario;
import com.jhonathan.model.repository.UsuarioRepository;
import com.jhonathan.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	
	@Test(expected = None.class)
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		
		String email = "teste@gmail.com";
		String senha = "senha";
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).nome("teste").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		Usuario u = service.salvarUsaurio(new Usuario());
		
		Assertions.assertThat(u).isNotNull();
		Assertions.assertThat(u.getId()).isEqualTo(1l);
		Assertions.assertThat(u.getNome()).isEqualTo("teste");
		Assertions.assertThat(u.getEmail()).isEqualTo("teste@gmail.com");
		Assertions.assertThat(u.getSenha()).isEqualTo("senha");
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComOMesmoEmail() {
		String email = "teste@gmail.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		service.salvarUsaurio(usuario);
		
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}
	
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		String email = "teste@gmail.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		Usuario result = service.autenticar(email, senha);
		
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable( ()-> service.autenticar("teste@gmail.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado para o e-mail informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		String email = "teste@gmail.com";
		String senha = "senha";
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable exception = Assertions.catchThrowable( ()-> service.autenticar("teste@gmail.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		service.validarEmail("email.com");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroQuandoExistirEmailCadastrado() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		
		service.validarEmail("j.cruz");
	}
	
	
}
