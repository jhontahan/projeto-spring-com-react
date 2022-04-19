package com.jhonathan.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhonathan.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
