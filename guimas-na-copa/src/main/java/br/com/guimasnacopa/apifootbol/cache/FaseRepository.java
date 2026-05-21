package br.com.guimasnacopa.apifootbol.cache;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.apifootbol.dto.FaseApiDTO;

@Repository(value = "faseApiRepository")
public interface FaseRepository
        extends MongoRepository<FaseApiDTO, Long> {
	
	List<FaseApiDTO> findByExpiresAtAfter(
			LocalDateTime now);
	
	Optional<FaseApiDTO> findByCampeonatoIdAndExpiresAtAfter(
			Long campeonadoId, LocalDateTime now);
	
	Optional<FaseApiDTO> findBySlugAndExpiresAtAfter(
			String slug, LocalDateTime now);

	Optional<FaseApiDTO> findBySlug(String slug);

	Optional<FaseApiDTO> findByFaseId(Long id);
}