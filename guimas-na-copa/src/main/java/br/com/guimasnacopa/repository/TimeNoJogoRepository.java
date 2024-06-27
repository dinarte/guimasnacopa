package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.TimeNoJogo;

@Repository
public interface TimeNoJogoRepository  extends CrudRepository<TimeNoJogo, Integer>{

	
	public List<TimeNoJogo> findAllByJogo(Jogo jogo);

	public List<TimeNoJogo> findAllByJogo_Fase_Bolao(Bolao bolao);

	@Modifying(clearAutomatically = true)
	@Query("update TimeNoJogo set gols = :gols where id = :id")
	public void updateGols(@Param("gols") int gols, @Param("id") Integer idTimeNoJogo);
	
}
