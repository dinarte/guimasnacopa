package br.com.guimasnacopa.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Fase;

@Repository
public interface FaseRepository  extends CrudRepository<Fase, Integer>{

	
}
