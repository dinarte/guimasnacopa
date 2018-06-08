package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.TimeNoJogo;

@Repository
public interface TimeNoJogoRepository  extends CrudRepository<TimeNoJogo, Integer>{

	
	public List<TimeNoJogo> findAllByJogo(Jogo jogo);
	
}
