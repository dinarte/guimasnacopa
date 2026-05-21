package br.com.guimasnacopa.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesApiDTO;
import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesApiDTO.TimeApiDTO;
import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesToJogosConverter;
import br.com.guimasnacopa.apifootbol.dto.FaseApiDTO;
import br.com.guimasnacopa.apifootbol.dto.JogoApiDTO;
import br.com.guimasnacopa.apifootbol.service.CampeonatoDetalhesApiService;
import br.com.guimasnacopa.apifootbol.service.FaseApiService;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.BolaoCompeticao;
import br.com.guimasnacopa.domain.Competicao;
import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;
import br.com.guimasnacopa.repository.BolaoCompeticaoRepository;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.CompeticaoRepository;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.repository.TimeRepository;

@Component
public class BolaoService {
	
	@Autowired
	BolaoRepository bolaoRepo;
	
	@Autowired
	BolaoCompeticaoRepository bolaoCompeticaoRepository;
	
	@Autowired
	FaseRepository faseRepository;
	
	@Autowired
	TimeRepository timeRepository;
	
	@Autowired
	TimeNoJogoRepository timeNoJogoRepository;
	
	@Autowired
	JogoRepository jogoRepository;
	
	@Autowired
	CompeticaoRepository competicaoRepository;
	
	@Autowired
	CampeonatoDetalhesApiService campeonatoDetalhesApiService;
	
	@Autowired
	FaseApiService faseApiService;

	
	public Bolao getBolaoByPermaLink(String linkBolao) throws BolaoNaoSelecionadoException {
		Bolao bolao = bolaoRepo.findOneByPermalink(linkBolao);
		if (bolao == null)
			throw new BolaoNaoSelecionadoException("Praaaa foraaaaaaaaaaaaa!!!! Não  encontramos o bolão especificado.");
		return bolao;
	}
	
	@Transactional
	public Bolao criarBolao(Bolao bolao) {
		
		bolaoRepo.save(bolao);
		
		if (bolao.getId() == null) {
		
			Competicao competicao = competicaoRepository.findById(bolao.getCompeticao().getId()).get();
	
			associarCompeticaoAoBolao(bolao, competicao);
			
			CampeonatoDetalhesApiDTO campeonatoDetalheApi = campeonatoDetalhesApiService
																.getByCampeonatoId(competicao.getIdApi());
			
			List<FaseApiDTO> faseApiList = faseApiService.getAllByCampeonatoId(competicao.getIdApi());
			
			var converter = new CampeonatoDetalhesToJogosConverter();
			
			Set<Object> timesFaltantes = new HashSet<Object>();
					
			faseApiList.forEach(faseApi -> {
				
				Fase fase = saveFase(faseApi, bolao, competicao);
				
				List<JogoApiDTO> jogosApiList = converter.converter(campeonatoDetalheApi, faseApi.getSlug());
				
				//saveTimes(converter, jogosApiList);
				Map<Long, Integer> timesMap = getTimesMap();
				jogosApiList.forEach(jogoApi -> {
	
					Jogo jogo = new Jogo();
					
					jogo.setFase(fase);
					jogo.setExecucao(Jogo.EXECUSSAO_PREVISTO);
					jogo.setIdApi(jogoApi.getPartidaId());
					jogo.setGrupo(jogoApi.getGrupo().replace("grupo-", "GRUPO ").toUpperCase());
					jogo.setRodada(Integer.valueOf(jogoApi.getRodada().replace("a-rodada", "")));
					jogo.setData(convertStringToLocaldataTime(jogoApi.getDataRealizacaoIso()));
					jogo.setLiberarCriacaoPalpites(true);
					jogoRepository.save(jogo);
					
					
					System.out.println("Mandante: " + jogoApi.getTimeMandante().getTimeId() + " - " + jogoApi.getTimeMandante().getNomePopular() + " ("+timesMap.get(jogoApi.getTimeMandante().getTimeId())+")");
					System.out.println("Visitante: " + jogoApi.getTimeVisitante().getTimeId() + " - " + jogoApi.getTimeVisitante().getNomePopular() + " ("+timesMap.get(jogoApi.getTimeMandante().getTimeId())+")");
					
					if (timesMap.get(jogoApi.getTimeMandante().getTimeId()) == null) {
						Time time = new Time();
						time.setFlag(jogoApi.getTimeMandante().getEscudo());
						time.setIdApi(jogoApi.getTimeMandante().getTimeId());
						time.setNome(jogoApi.getTimeMandante().getNomePopular());
						time.setSigla(jogoApi.getTimeMandante().getSigla());
						timeRepository.save(time);
						timesMap.put(time.getIdApi(), time.getId());
						//timesFaltantes.add(jogoApi.getTimeMandante());
					}
					
					if (timesMap.get(jogoApi.getTimeVisitante().getTimeId()) == null) {
						Time time = new Time();
						time.setFlag(jogoApi.getTimeVisitante().getEscudo());
						time.setIdApi(jogoApi.getTimeVisitante().getTimeId());
						time.setNome(jogoApi.getTimeVisitante().getNomePopular());
						time.setSigla(jogoApi.getTimeVisitante().getSigla());
						timeRepository.save(time);
						timesMap.put(time.getIdApi(), time.getId());
						//timesFaltantes.add(jogoApi.getTimeVisitante());
					} 
					
					
					var timeA = new Time();
					timeA.setId(timesMap.get(jogoApi.getTimeMandante().getTimeId()));
					jogo.setTimeA(timeA);
					TimeNoJogo timeNoJogoA = new TimeNoJogo();
					timeNoJogoA.setJogo(jogo);
					timeNoJogoA.setTime(timeA);
					timeNoJogoA.setMandoDeCampo(TimeNoJogo.CASA);
					timeNoJogoRepository.save(timeNoJogoA);
					
					var timeB = new Time();
					timeB.setId(timesMap.get(jogoApi.getTimeVisitante().getTimeId()));
					jogo.setTimeA(timeB);
					TimeNoJogo timeNoJogoB = new TimeNoJogo();
					timeNoJogoB.setJogo(jogo);
					timeNoJogoB.setTime(timeB);
					timeNoJogoB.setMandoDeCampo(TimeNoJogo.VISITANTE);
					timeNoJogoRepository.save(timeNoJogoB);
	
				});
				
			});
			
			
			System.out.println(timesFaltantes);
			
			timesFaltantes.forEach(timeFaltante -> {
				TimeApiDTO dto = (TimeApiDTO) timeFaltante;
				System.out.println(dto.getTimeId() + " - " + dto.getNomePopular());
				
			});
		}
		return bolao;
		
	}

	private Map<Long, Integer> getTimesMap() {
		List<Time> timeList = timeRepository.findAll();
		Map<Long, Integer> mapa = timeList.stream()
			    .filter(t -> t.getIdApi() != null && t.getId() != null)
			    .collect(Collectors.toMap(
			        Time::getIdApi,
			        Time::getId,
			        (v1, v2) -> v1 // resolve duplicidade de chave
			    ));
		return mapa;
	}

	private void associarCompeticaoAoBolao(Bolao bolao, Competicao competicao) {
		var bolaoCompeticao = new BolaoCompeticao();
		bolaoCompeticao.setBolao(bolao);
		bolaoCompeticao.setCompeticao(competicao);
		bolaoCompeticaoRepository.save(bolaoCompeticao);
	}

	private void saveTimes(CampeonatoDetalhesToJogosConverter converter, List<JogoApiDTO> jogosApiList) {
		List<TimeApiDTO> timesApiList = converter.extrairTimes(jogosApiList);
		List<Long> timesIdAPisList = timeRepository.findAllIdsApi();
		timesApiList.forEach(timeApi -> {
			if (timesIdAPisList.contains(timeApi.getTimeId())) {
				Time time = new Time();
				time.setFlag(timeApi.getEscudo());
				time.setIdApi(timeApi.getTimeId());
				time.setNome(timeApi.getNomePopular());
				time.setSigla(timeApi.getSigla());
				timeRepository.save(time);
			}
		});
	}

	private Fase saveFase(FaseApiDTO faseApi, Bolao bolao, Competicao competicao) {
		Fase fase = new Fase();
		fase.setNome(faseApi.getNome());
		fase.setIdApi(faseApi.getFaseId());
		fase.setInicioPalpite(new Date());
		fase.setBolao(bolao);
		fase.setCompeticao(competicao);
		fase.setTipo(faseApi.getTipo());
		fase.setFaseFinal(faseApi.getSlug().equals("final") ? true : false);
		faseRepository.save(fase);
		return fase;
	}
	
	
	public LocalDateTime convertStringToLocaldataTime(String data) {

		DateTimeFormatter formatter =
		        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

		LocalDateTime localDateTime =
		        OffsetDateTime.parse(data, formatter)
		                      .toLocalDateTime();
		
		return localDateTime;
	}
}
