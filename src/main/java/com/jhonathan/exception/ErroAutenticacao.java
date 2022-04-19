package com.jhonathan.exception;

public class ErroAutenticacao extends RuntimeException{

	private static final long serialVersionUID = 5331898038964539413L;

	public ErroAutenticacao(String mensagem) {
		super(mensagem);
	}
	
}
