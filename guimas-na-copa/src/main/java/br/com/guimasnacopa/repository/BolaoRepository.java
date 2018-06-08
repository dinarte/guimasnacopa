package br.com.guimasnacopa.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;

@Repository
public interface BolaoRepository  extends CrudRepository<Bolao, Integer>{

	public Bolao findOneByPermalink(String permalink);
}
