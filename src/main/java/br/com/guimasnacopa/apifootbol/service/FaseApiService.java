package br.com.guimasnacopa.apifootbol.service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.guimasnacopa.apifootbol.cache.FaseRepository;
import br.com.guimasnacopa.apifootbol.dto.FaseApiDTO;

@Service
public class FaseApiService {

	private static final String RESOURCE_KEY =  "campeonatos/{id}/fases";
	
    //private final ApiCacheRepository cacheRepository;
    private final FaseRepository faseRepository;
    private final RestTemplate restTemplate;

    @Value("${external.api.base-url}")
    private String baseUrl;
    
    @Value("${external.api.token}")
    private String token;

    public FaseApiService(FaseRepository faseRepository) {
        this.faseRepository = faseRepository;
        this.restTemplate = new RestTemplate();
    }

    public List<FaseApiDTO> getAllByCampeonatoId(Long campeonatoId) {
    	var list = faseRepository
                .findByExpiresAtAfter(LocalDateTime.now());
    	return list.isEmpty() ? 
    			buscarNaApiESalvarCache(RESOURCE_KEY.replace("{id}", campeonatoId.toString())) : 
    				list ;
    }
    
    public FaseApiDTO getById(Long id) {
    	return faseRepository.findByFaseId(id).get();
    }
    
    public FaseApiDTO getBySlug(String slug) {
    	return faseRepository.findBySlug(slug).get();
    }

    private List<FaseApiDTO> buscarNaApiESalvarCache(String resouceKey) {
    	String resourceUrl = baseUrl + "/" + resouceKey;
        ResponseEntity<String> response = getUrl(resourceUrl);
        String responseJson = response.getBody();
        return saveInCache(responseJson);
    }

	private ResponseEntity<String> getUrl(String resourceUrl) {
		HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
        		resourceUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
		return response;
	}

    
    /**
     * Salva os valores no cache
     * @param responseJson
     */
	private List<FaseApiDTO> saveInCache(String responseJson) {
		List<FaseApiDTO> fases;
		fases = mapApiResponseToDtoList(responseJson);
        faseRepository.saveAll(fases);
        return fases;
	}

    /**
     * mapeia a resposta json da api em uma lista de DTO. 
     * @param responseJson
     * @return
     */
	private List<FaseApiDTO> mapApiResponseToDtoList(String responseJson) {
		List<FaseApiDTO> fases = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			fases = mapper.readValue(
			        responseJson,
			        new TypeReference<List<FaseApiDTO>>() {}
			);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return fases;
	}
}