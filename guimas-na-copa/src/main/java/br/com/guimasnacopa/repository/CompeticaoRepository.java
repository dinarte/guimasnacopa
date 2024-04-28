package br.com.guimasnacopa.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Competicao;

@Repository
public interface CompeticaoRepository extends CrudRepository<Competicao, Integer> {
	
}
