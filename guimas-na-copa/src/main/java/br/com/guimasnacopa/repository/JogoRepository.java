package br.com.guimasnacopa.repository;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Jogo;

@Repository
public interface JogoRepository  extends CrudRepository<Jogo, Integer>{

	@Query("from Jogo j where j.fase.bolao = :bolao order by j.fase, j.grupo, j.data")
	public List<Jogo> findAllByFase_BolaoOrderByFaseGrupoData(@Param("bolao") Bolao bolao);
	
	@Modifying(clearAutomatically = true)
	@Query("update Jogo set data = :data where id = :jogoId")
	public void updateData(@Param("data") LocalDateTime data, @Param("jogoId") Integer jogoId);
	
	
}
