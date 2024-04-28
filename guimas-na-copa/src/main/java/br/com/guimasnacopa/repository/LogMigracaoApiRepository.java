package br.com.guimasnacopa.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.LogMigracaoApi;

@Repository
public interface LogMigracaoApiRepository extends CrudRepository<LogMigracaoApi, Integer>{

}
