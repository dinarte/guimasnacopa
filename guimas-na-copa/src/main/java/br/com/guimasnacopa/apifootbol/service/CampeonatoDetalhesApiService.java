package br.com.guimasnacopa.apifootbol.service;
import java.io.IOException;
import java.time.LocalDateTime;

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

import br.com.guimasnacopa.apifootbol.cache.CampeonatoDetalhesRepository;
import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesApiDTO;

@Service
public class CampeonatoDetalhesApiService {

	
	private static final String RESOURCE_KEY =  "campeonatos/{id}/partidas";
	
    //private final ApiCacheRepository cacheRepository;
    private final CampeonatoDetalhesRepository campeonatoDetalhesRepository;
    private final RestTemplate restTemplate;

    @Value("${external.api.base-url}")
    private String baseUrl;
    
    @Value("${external.api.token}")
    private String token;

    public CampeonatoDetalhesApiService(CampeonatoDetalhesRepository campeonatoDetalhesRepository) {
        this.campeonatoDetalhesRepository = campeonatoDetalhesRepository;
        this.restTemplate = new RestTemplate();
    }

    public CampeonatoDetalhesApiDTO getByCampeonatoId(Long campeonatoId) {
    	var campeonato = campeonatoDetalhesRepository
                .findByCampeonatoCampeonatoIdAndExpiresAtAfter(campeonatoId, LocalDateTime.now());
    	return campeonato.isEmpty() ? 
    			buscarNaApiESalvarCache(RESOURCE_KEY.replace("{id}", campeonatoId.toString())) : 
    				campeonato.get() ;
    }
        
    private CampeonatoDetalhesApiDTO buscarNaApiESalvarCache(String resourceKey) {
    	String resourceUrl = baseUrl + "/" + resourceKey;
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
	private CampeonatoDetalhesApiDTO saveInCache(String responseJson) {
		CampeonatoDetalhesApiDTO campeonato;
		campeonato = mapApiResponseToDto(responseJson);
		campeonatoDetalhesRepository.save(campeonato);
        return campeonato;
	}

    /**
     * mapeia a resposta json da api em uma lista de DTO. 
     * @param responseJson
     * @return
     */
	private CampeonatoDetalhesApiDTO mapApiResponseToDto(String responseJson) {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        return mapper.readValue(responseJson, CampeonatoDetalhesApiDTO.class);
	    } catch (IOException e) {
	        throw new RuntimeException("Erro ao converter JSON para CampeonatoDetalhesDTO", e);
	    }
	}
}