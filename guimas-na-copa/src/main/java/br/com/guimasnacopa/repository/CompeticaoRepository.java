package br.com.guimasnacopa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Competicao;

@Repository
public interface CompeticaoRepository extends CrudRepository<Competicao, Integer> {

	Optional<Competicao> findOneByIdApi(Long campeonatoId);

	List<Competicao> findByStatusNot(String string);
	
}
