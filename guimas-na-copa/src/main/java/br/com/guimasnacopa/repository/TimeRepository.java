package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Time;

@Repository
public interface TimeRepository  extends CrudRepository<Time, Integer>{

	public List<Time> findAllByFlag(String flag);
	
	@Query("select distinct tnj.time from TimeNoJogo tnj where tnj.jogo.fase.bolao = :bolao order by tnj.time.nome")
	public List<Time> findAllByBolao(@Param("bolao") Bolao bolao);
	
}
