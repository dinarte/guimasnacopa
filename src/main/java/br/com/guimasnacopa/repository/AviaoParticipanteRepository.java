package br.com.guimasnacopa.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Aviao;
import br.com.guimasnacopa.domain.AviaoParticipante;
import br.com.guimasnacopa.domain.Participante;

@Repository
public interface AviaoParticipanteRepository extends CrudRepository<AviaoParticipante, Integer> {

	Long countByAviao(Aviao aviao);

	List<AviaoParticipante> findAllByAviaoOrderByParticipante_classificacaoAscParticipante_pontuacaoDesc(Aviao aviao);

	List<AviaoParticipante> findAllByAviao(Aviao aviao);

	void deleteAllByAviao(Aviao aviao);

	boolean existsByParticipanteAndAviao_idNot(Participante participante, Integer aviaoId);
}