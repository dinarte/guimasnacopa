package br.com.guimasnacopa.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Aviao;
import br.com.guimasnacopa.domain.Bolao;

@Repository
public interface AviaoRepository extends CrudRepository<Aviao, Integer> {

	List<Aviao> findAllByBolaoOrderByDataHoraProcessamentoDesc(Bolao bolao);

	Aviao findTop1ByBolaoOrderByDataHoraProcessamentoDesc(Bolao bolao);

	Aviao findOneByIdAndBolao(Integer id, Bolao bolao);
}