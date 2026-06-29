package br.com.guimasnacopa.apifootbol.cache;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesApiDTO;

public interface CampeonatoDetalhesRepository
        extends MongoRepository<CampeonatoDetalhesApiDTO, Long> {
	
	
	Optional<CampeonatoDetalhesApiDTO> findByCampeonatoCampeonatoIdAndExpiresAtAfter(
			Long campeonadoId, LocalDateTime now);

	void deleteByCampeonatoCampeonatoId(Long campeonadoId);
	
}