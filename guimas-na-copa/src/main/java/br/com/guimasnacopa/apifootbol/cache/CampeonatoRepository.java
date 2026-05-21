package br.com.guimasnacopa.apifootbol.cache;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDTO;

public interface CampeonatoRepository
        extends MongoRepository<CampeonatoDTO, Long> {
	
	List<CampeonatoDTO> findByExpiresAtAfter(
	        LocalDateTime now
	    );
	
	Optional<CampeonatoDTO> findByCampeonatoId(Long campeonadoId);
	
	Optional<CampeonatoDTO> findBySlug(String slug);
	
}