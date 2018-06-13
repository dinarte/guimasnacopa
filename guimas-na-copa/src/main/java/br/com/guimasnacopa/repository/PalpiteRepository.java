package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;

@Repository
public interface PalpiteRepository  extends CrudRepository<Palpite, Integer>{

	@Query("select p from Palpite p "
			+ "left join p.jogo "
			+ "left join p.jogo.fase "
			+ "left join p.timeA "
			+ "left join p.timeB "
			+ "where p.participante = :participante "
			+ "order by p.jogo.fase.nome, p.jogo.grupo, p.jogo.data, p.jogo.id")
	public List<Palpite> findAllByParticipante(@Param("participante") Participante participante);
	

	public List<Palpite> findTop6ByParticipanteOrderByJogo_Data(Participante participante);

	public List<Palpite> findAllByJogo(Jogo jogo);
	
	
}
