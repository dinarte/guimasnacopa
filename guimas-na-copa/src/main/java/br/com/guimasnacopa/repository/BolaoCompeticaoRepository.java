package br.com.guimasnacopa.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.BolaoCompeticao;
import br.com.guimasnacopa.domain.Competicao;

@Repository
public interface BolaoCompeticaoRepository extends CrudRepository<BolaoCompeticao, Integer> {
	
	public List<BolaoCompeticao> findAllByBolao(Bolao bolao);
	
	public List<BolaoCompeticao> findAllByBolaoAndCompeticao(Bolao bolao, Competicao competicao);

}
