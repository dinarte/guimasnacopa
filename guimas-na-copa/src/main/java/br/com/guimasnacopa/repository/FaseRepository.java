package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Fase;

@Repository
public interface FaseRepository  extends CrudRepository<Fase, Integer>{

	public List<Fase> findAllByBolao(Bolao bolao);

	
}
