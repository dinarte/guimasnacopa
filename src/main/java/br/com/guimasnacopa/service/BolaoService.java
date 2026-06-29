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
		
			popularFasesEJogosvIApI(bolao);
		}
		return bolao;
		
	}

	@Transactional
	public void atualizarFasesEJogosDaApi(Integer bolaoId) {
		Bolao bolao = bolaoRepo.findById(bolaoId).orElseThrow();
		popularFasesEJogosvIApI(bolao);
	}

	private void popularFasesEJogosvIApI(Bolao bolao) {
		Set<Object> timesFaltantes = new HashSet<Object>();

		List<Competicao> competicoes = getCompeticoesDoBolao(bolao);
		competicoes.forEach(competicao -> {
			associarCompeticaoAoBolao(bolao, competicao); 

			CampeonatoDetalhesApiDTO campeonatoDetalheApi = campeonatoDetalhesApiService
												.getByCampeonatoId(competicao.getIdApi());

			List<FaseApiDTO> faseApiList = faseApiService.getAllByCampeonatoId(competicao.getIdApi());

			var converter = new CampeonatoDetalhesToJogosConverter();

			faseApiList.forEach(faseApi -> {

				Fase fase = findOrCreateFase(faseApi, bolao, competicao);

				List<JogoApiDTO> jogosApiList = converter.converter(campeonatoDetalheApi, faseApi.getSlug());

				//saveTimes(converter, jogosApiList);
				Map<Long, Integer> timesMap = getTimesMap();
				jogosApiList.forEach(jogoApi -> {
					Jogo jogoExistente = jogoRepository.findOneByFaseAndIdApi(fase, jogoApi.getPartidaId());
					if (jogoExistente != null) {
						return;
					}

					Long timeMandanteId = jogoApi.getTimeMandante() != null ? jogoApi.getTimeMandante().getTimeId() : null;
					Long timeVisitanteId = jogoApi.getTimeVisitante() != null ? jogoApi.getTimeVisitante().getTimeId() : null;

					if (timeMandanteId == null || timeVisitanteId == null) {
						return;
					}

					Jogo jogo = new Jogo();
					LocalDateTime dataJogo = convertStringToLocaldataTime(jogoApi.getDataRealizacaoIso());

					jogo.setFase(fase);
					jogo.setExecucao(Jogo.EXECUSSAO_PREVISTO);
					jogo.setIdApi(jogoApi.getPartidaId());
					jogo.setGrupo(jogoApi.getGrupo().replace("grupo-", "GRUPO ").toUpperCase());
					jogo.setRodada(Integer.valueOf(jogoApi.getRodada().replace("a-rodada", "")));
					jogo.setData(dataJogo);
					jogo.setLimiteAposta(dataJogo.minusHours(1));
					jogo.setLiberarCriacaoPalpites(true);
					jogoRepository.save(jogo);


					System.out.println("Mandante: " + timeMandanteId + " - " + jogoApi.getTimeMandante().getNomePopular() + " ("+timesMap.get(timeMandanteId)+")");
					System.out.println("Visitante: " + timeVisitanteId + " - " + jogoApi.getTimeVisitante().getNomePopular() + " ("+timesMap.get(timeVisitanteId)+")");

					if (timesMap.get(timeMandanteId) == null) {
						Time time = new Time();
						time.setFlag(jogoApi.getTimeMandante().getEscudo());
						time.setIdApi(timeMandanteId);
						time.setNome(jogoApi.getTimeMandante().getNomePopular());
						time.setSigla(jogoApi.getTimeMandante().getSigla());
						timeRepository.save(time);
						timesMap.put(time.getIdApi(), time.getId());
						//timesFaltantes.add(jogoApi.getTimeMandante());
					}

					if (timesMap.get(timeVisitanteId) == null) {
						Time time = new Time();
						time.setFlag(jogoApi.getTimeVisitante().getEscudo());
						time.setIdApi(timeVisitanteId);
						time.setNome(jogoApi.getTimeVisitante().getNomePopular());
						time.setSigla(jogoApi.getTimeVisitante().getSigla());
						timeRepository.save(time);
						timesMap.put(time.getIdApi(), time.getId());
						//timesFaltantes.add(jogoApi.getTimeVisitante());
					}


					var timeA = new Time();
					timeA.setId(timesMap.get(timeMandanteId));
					jogo.setTimeA(timeA);
					TimeNoJogo timeNoJogoA = new TimeNoJogo();
					timeNoJogoA.setJogo(jogo);
					timeNoJogoA.setTime(timeA);
					timeNoJogoA.setMandoDeCampo(TimeNoJogo.CASA);
					timeNoJogoRepository.save(timeNoJogoA);

					var timeB = new Time();
					timeB.setId(timesMap.get(timeVisitanteId));
					jogo.setTimeB(timeB);
					TimeNoJogo timeNoJogoB = new TimeNoJogo();
					timeNoJogoB.setJogo(jogo);
					timeNoJogoB.setTime(timeB);
					timeNoJogoB.setMandoDeCampo(TimeNoJogo.VISITANTE);
					timeNoJogoRepository.save(timeNoJogoB);

				});

			});
		});
		
		
		System.out.println(timesFaltantes);
		
		timesFaltantes.forEach(timeFaltante -> {
			TimeApiDTO dto = (TimeApiDTO) timeFaltante;
			System.out.println(dto.getTimeId() + " - " + dto.getNomePopular());
			
		});
	}

	private List<Competicao> getCompeticoesDoBolao(Bolao bolao) {
		if (bolao.getCompeticao() != null && bolao.getCompeticao().getId() != null) {
			Competicao competicao = competicaoRepository.findById(bolao.getCompeticao().getId()).orElse(null);
			if (competicao == null) {
				return List.of();
			}
			return List.of(competicao);
		}

		return bolaoCompeticaoRepository.findAllByBolao(bolao).stream()
				.map(BolaoCompeticao::getCompeticao)
				.filter(competicao -> competicao != null)
				.collect(Collectors.toList());
	}

	private Fase findOrCreateFase(FaseApiDTO faseApi, Bolao bolao, Competicao competicao) {
		Fase faseExistente = faseRepository.findOneByBolaoAndCompeticaoAndIdApi(bolao, competicao, faseApi.getFaseId());
		if (faseExistente != null) {
			return faseExistente;
		}

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
		List<BolaoCompeticao> associacoes = bolaoCompeticaoRepository.findAllByBolaoAndCompeticao(bolao, competicao);
		if (!associacoes.isEmpty()) {
			return;
		}

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

	
	
	public LocalDateTime convertStringToLocaldataTime(String data) {

		DateTimeFormatter formatter =
		        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

		LocalDateTime localDateTime =
		        OffsetDateTime.parse(data, formatter)
		                      .toLocalDateTime();
		
		return localDateTime;
	}
}
