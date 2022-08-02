package com.jhonathan.api.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LancamentoDTO {
	private Long id;
	
	private String descricao;
	
	private Integer mes;
	
	private Integer ano;
	
	private Long usuario;
	
	private BigDecimal valor;
	
	private String tipo;
	
	private String status;
	
}
