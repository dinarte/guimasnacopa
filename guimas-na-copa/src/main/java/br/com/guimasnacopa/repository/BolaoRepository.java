package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;

@Repository
public interface BolaoRepository  extends CrudRepository<Bolao, Integer>{

	public Bolao findOneByPermalink(String permalink);

	
	public List<Bolao> findAllByOrderByDataInicioDesc();


}
