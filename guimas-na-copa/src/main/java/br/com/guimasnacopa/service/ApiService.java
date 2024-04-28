package br.com.guimasnacopa.service;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import br.com.guimasnacopa.api.domain.FixturesLeague;
import br.com.guimasnacopa.api.domain.League;
import br.com.guimasnacopa.api.domain.Round;
import br.com.guimasnacopa.api.domain.Team;
import br.com.guimasnacopa.api.mapper.Mapper;
import br.com.guimasnacopa.api.mapper.Mapper.MappingData;
import br.com.guimasnacopa.api.repository.FixturesLeagueRepository;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.BolaoCompeticao;
import br.com.guimasnacopa.domain.Competicao;
import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.LogMigracaoApi;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.repository.CompeticaoRepository;
import br.com.guimasnacopa.repository.LogMigracaoApiRepository;


@Transactional
@Service
public class ApiService {
	
	//https://api-football-v1.p.rapidapi.com/v3/fixtures?league=9&season=2024
	
	private static final String API_BASE_HOST = "api-football-v1.p.rapidapi.com/v3";
	private static final String API_KEY = "55011da490msh8d921becc26ce28p13dd2cjsnac64cd16c124";
	private static final String API_RECURSO_KEY_TEMPLATE = "{recurso}"; 
	private static final String API_BASE_URI_TEMPLATE =  "https://"+API_BASE_HOST+API_RECURSO_KEY_TEMPLATE+"?";
	private static final String RES_JOGOS = "/fixtures";
	
	@Autowired
	FixturesLeagueRepository fixturesLeagueRepository;
	
	@Autowired
	LogMigracaoApiRepository logMigracaoApiRepository;
	
	@Autowired
	CompeticaoRepository competicaoRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
		
	public String getJogosByCompeticaoAndSeasson(String idCompeticaoApi, String season) {
		String uri =  getUriResource(RES_JOGOS) 
				+ "&league="+idCompeticaoApi+"&season="+season;
		String retorno = get(uri);
		return retorno;
	}
	
	private String getUriResource(String recurso) {
		return  API_BASE_URI_TEMPLATE.replace(API_RECURSO_KEY_TEMPLATE, recurso);
	}
	
	private String get(String uri) {
		
		try {
			JSONArray response = Unirest.get(uri)
					.header("X-RapidAPI-Key", API_KEY)
					.header("X-RapidAPI-Host", API_BASE_HOST)
					.asJson()
					.getBody()
					.getObject()
					.getJSONArray("response");
			 
			return response.toString() ;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	  
	public List<FixturesLeague> carregarInicial(String idCompeticaoApi, String season) {
		
		String json = getJogosByCompeticaoAndSeasson(idCompeticaoApi,season);
		
		ObjectMapper mapper = new ObjectMapper();
		List<FixturesLeague> jogosDto;
		
		try {
			jogosDto = mapper.readValue(json, new TypeReference<List<FixturesLeague>>() {});
			jogosDto.forEach( jogoDto -> {
				System.out.println("Salvando o jogo " + jogoDto.getId());
				jogoDto.setId(jogoDto.getFixture().getId());
				jogoDto.setRound( new Round(jogoDto.getLeague().getRound(), jogoDto.getLeague()) );
				entityManager.merge(jogoDto);
				entityManager.flush();
				entityManager.clear();
			});
			return jogosDto;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static void main(String[] args) {
		
		ApiService api = new ApiService();
		
		System.out.println("INICIO:");
		System.out.println(API_BASE_URI_TEMPLATE);
		String json = api.getJogosByCompeticaoAndSeasson("9","2024");
		
		ObjectMapper mapper = new ObjectMapper();
		List<FixturesLeague> jogosDto;
		try {
			jogosDto = mapper.readValue(json, new TypeReference<List<FixturesLeague>>() {});
			
			
			//extraindo as competicoes
			Collection<League> leagues = jogosDto
											.stream()
											.map( dto -> dto.getLeague() )
											.collect(Collectors.toMap( League::getId, 
													Function.identity(), 
													(a, b) -> a
													)).values();
			
			leagues.forEach( l -> {
				System.out.println(l.getId());
				System.out.println(l.getName());
				System.out.println(l.getFlag());
				System.out.println(l.getLogo());
				System.out.println(l.getRound());
			} );
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void gerarDadosDoBolao(Bolao bolao) {
		map(bolao, League.class, Competicao.class, Mapper::leagueToCompeticao);
		map(bolao, Round.class, Fase.class, Mapper::roundToFase);
		map(bolao, Team.class, Time.class, Mapper::teamToTime);
		map(bolao, FixturesLeague.class, Jogo.class, Mapper::fixturesLeagueToJogo);
		associarCompeticoesAoBolao(bolao);
	}

	private void associarCompeticoesAoBolao(Bolao bolao) {
		//TODO: Associar apenas competicoes escolhidas pelo usuario
		List<Competicao> competicoes = (List<Competicao>) competicaoRepository.findAll();
		competicoes.forEach( competicao -> {
			BolaoCompeticao bolaoCompeticao = new BolaoCompeticao();
			bolaoCompeticao.setBolao(bolao);
			bolaoCompeticao.setCompeticao(competicao);
			entityManager.persist(bolaoCompeticao);
		});
	}

	@SuppressWarnings("unchecked")
	private void map(Bolao bolao, Class<?> origem, Class<?> destino, Consumer<MappingData> mapper) {
		Query query = entityManager.createQuery("from " + origem.getSimpleName());
		
		List<Object> objList = query.getResultList();
		
		Query queryCount = entityManager.createQuery("select id, idApi from " + destino.getSimpleName());
		List<Map<String, Long>> idsExistentes = queryCount.getResultList();
		
		List<LogMigracaoApi> logMigracaoApi = (List<LogMigracaoApi>) logMigracaoApiRepository.findAll();
		
		
		objList.forEach(obj -> {
			
			Long idApi = getIdApi(obj);
			List<Long>idApiList = idsExistentes.stream().map(item -> item.get("idApi")).collect(Collectors.toList());
			
			if (!idApiList.contains(idApi)) {
				MappingData mappingData = MappingData.getInstance(bolao, obj); 
				mappingData.setLogMigracaoApi(logMigracaoApi);
				mapper.accept(mappingData);
				Object destination = mappingData.getDestination();
				entityManager.persist(destination);
				LogMigracaoApi logApi = createLogApi(origem, destino, idApi, destination);
				entityManager.persist(logApi);
				entityManager.flush();
				entityManager.clear();
			}
			
		});
	}

	private LogMigracaoApi createLogApi(Class<?> origem, Class<?> destino, Long idApi, Object destination) {
		LogMigracaoApi logApi = new LogMigracaoApi();
		logApi.setApi(API_BASE_HOST);
		logApi.setSource(origem.getName());
		logApi.setIdSource(idApi);
		logApi.setDestination(destino.getName());
		logApi.setIdDestination(getId(destination));
		return logApi;
	}
	
	private Long getIdApi(Object obj) {
		try {
			Method metodo = obj.getClass().getMethod("getId");
			Long resultado = (Long) metodo.invoke(obj);
	        return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
	}
	
	private Integer getId(Object obj) {
		try {
			Method metodo = obj.getClass().getMethod("getId");
			Integer resultado = (Integer) metodo.invoke(obj);
	        return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
	}

}
