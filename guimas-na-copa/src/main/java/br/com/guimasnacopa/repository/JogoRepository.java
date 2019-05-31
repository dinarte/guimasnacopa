package br.com.guimasnacopa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Participante;

@Repository
public interface JogoRepository extends CrudRepository<Jogo, Integer> {

	@EntityGraph(attributePaths = { "timesNoJogo" })
	@Query("select j from Jogo as j where j.fase.bolao = :bolao order by j.fase, j.grupo, j.data")
	public List<Jogo> findAllByFase_BolaoOrderByFaseGrupoData(@Param("bolao") Bolao bolao);
	
	@EntityGraph(attributePaths = { "timesNoJogo" })
	@Query("select j from Jogo as j "
			+ "where j.fase.bolao = :bolao "
			+ "and not exists (select p.id "
			+ "					from Palpite p "
			+ "					where p.jogo = j "
			+ "					and p.tipo = 'Resultado'"
			+ "					and p.participante = :participante)"
			+ "order by j.fase, j.grupo, j.data")
	public List<Jogo> findAllByBolaoAndPrticipanteAndNotExistsPalpiteOrderByFaseGrupoData(@Param("bolao") Bolao bolao, 
			@Param("participante") Participante participante);

	@Modifying(clearAutomatically = true)
	@Query("update Jogo set data = :data where id = :jogoId")
	public void updateData(@Param("data") LocalDateTime data, @Param("jogoId") Integer jogoId);

	@Query("select count(id) from Jogo j " + "where j.liberarCriacaoPalpites is true " + "and j.fase.bolao =:bolao "
			+ "and not exists (select id from Palpite p where p.jogo = j and p.participante = :participante)")
	public Long countJogosComPalpitesPendentesByBolaoAndParticipante(@Param("bolao") Bolao bolao,
			@Param("participante") Participante participante);

}
