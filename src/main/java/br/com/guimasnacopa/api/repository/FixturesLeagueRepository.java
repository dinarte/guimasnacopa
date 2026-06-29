package br.com.guimasnacopa.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.api.domain.FixturesLeague;

@Repository
public interface FixturesLeagueRepository extends CrudRepository<FixturesLeague, Long> {

}
