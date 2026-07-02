package br.com.guimasnacopa.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
		AtualizacaoApiPreview preview = gerarPreviewAtualizacaoApi(bolaoId);
		persistirAtualizacaoApi(preview);
	}

	public AtualizacaoApiPreview gerarPreviewAtualizacaoApi(Integer bolaoId) {
		Bolao bolao = bolaoRepo.findById(bolaoId).orElseThrow();
		AtualizacaoApiPreview preview = new AtualizacaoApiPreview();
		preview.setBolaoId(bolao.getId());
		preview.setBolaoNome(bolao.getNome());

		List<Competicao> competicoes = getCompeticoesDoBolao(bolao);
		var converter = new CampeonatoDetalhesToJogosConverter();

		competicoes.forEach(competicao -> {
			CompeticaoAtualizacao competicaoAtualizacao = new CompeticaoAtualizacao();
			competicaoAtualizacao.setCompeticaoId(competicao.getId());
			competicaoAtualizacao.setCompeticaoNome(competicao.getNome());

			CampeonatoDetalhesApiDTO campeonatoDetalheApi = campeonatoDetalhesApiService
					.getByCampeonatoIdSemCache(competicao.getIdApi());

			List<FaseApiDTO> faseApiList = faseApiService
					.getAllByCampeonatoIdSemCache(competicao.getIdApi());

			faseApiList.forEach(faseApi -> {
				Fase faseExistente = faseRepository
						.findOneByBolaoAndCompeticaoAndIdApi(bolao, competicao, faseApi.getFaseId());

				FaseConferenciaItem faseItem = FaseConferenciaItem.from(
						competicao.getNome(),
						faseApi.getFaseId(),
						faseApi.getNome(),
						faseApi.getTipo());

				if (faseExistente == null) {
					preview.getFasesNovas().add(faseItem);
					competicaoAtualizacao.getFasesNovas().add(FaseImportacao.from(faseApi));
				} else {
					preview.getFasesCadastradas().add(faseItem);
				}

				List<JogoApiDTO> jogosApiList = converter.converter(campeonatoDetalheApi, faseApi.getSlug());
				jogosApiList.forEach(jogoApi -> {
					if (jogoApi.getPartidaId() == null || jogoApi.getTimeMandante() == null || jogoApi.getTimeVisitante() == null
							|| jogoApi.getTimeMandante().getTimeId() == null || jogoApi.getTimeVisitante().getTimeId() == null) {
						return;
					}

					boolean jogoExistente = false;
					if (faseExistente != null) {
						jogoExistente = jogoRepository.findOneByFaseAndIdApi(faseExistente, jogoApi.getPartidaId()) != null;
					}

					JogoConferenciaItem jogoItem = JogoConferenciaItem.from(
							competicao.getNome(),
							faseApi.getNome(),
							jogoApi.getPartidaId(),
							jogoApi.getTimeMandante().getNomePopular(),
							jogoApi.getTimeVisitante().getNomePopular(),
							formatarGrupo(jogoApi.getGrupo()),
							parseRodada(jogoApi.getRodada()),
							jogoApi.getDataRealizacaoIso());

					if (jogoExistente) {
						preview.getJogosCadastrados().add(jogoItem);
					} else {
						preview.getJogosNovos().add(jogoItem);
						competicaoAtualizacao.getJogosNovos().add(JogoImportacao.from(faseApi, jogoApi));
					}
				});
			});

			preview.getCompeticoes().add(competicaoAtualizacao);
		});

		return preview;
	}

	@Transactional
	public void persistirAtualizacaoApi(AtualizacaoApiPreview preview) {
		if (preview == null || preview.getBolaoId() == null) {
			throw new IllegalArgumentException("Prévia de atualização inválida.");
		}

		Bolao bolao = bolaoRepo.findById(preview.getBolaoId()).orElseThrow();
		Map<Long, Integer> timesMap = getTimesMap();

		preview.getCompeticoes().forEach(competicaoAtualizacao -> {
			Competicao competicao = competicaoRepository.findById(competicaoAtualizacao.getCompeticaoId()).orElse(null);
			if (competicao == null) {
				return;
			}

			associarCompeticaoAoBolao(bolao, competicao);

			competicaoAtualizacao.getFasesNovas().forEach(faseImportacao ->
					findOrCreateFaseFromImport(faseImportacao, bolao, competicao));

			competicaoAtualizacao.getJogosNovos().forEach(jogoImportacao -> {
				Fase fase = faseRepository.findOneByBolaoAndCompeticaoAndIdApi(bolao, competicao, jogoImportacao.getFaseIdApi());
				if (fase == null) {
					return;
				}

				Jogo jogoExistente = jogoRepository.findOneByFaseAndIdApi(fase, jogoImportacao.getPartidaId());
				if (jogoExistente != null) {
					return;
				}

				Integer timeMandanteId = findOrCreateTime(jogoImportacao.getTimeMandante(), timesMap);
				Integer timeVisitanteId = findOrCreateTime(jogoImportacao.getTimeVisitante(), timesMap);
				if (timeMandanteId == null || timeVisitanteId == null) {
					return;
				}

				Jogo jogo = new Jogo();
				LocalDateTime dataJogo = convertStringToLocaldataTime(jogoImportacao.getDataRealizacaoIso());

				jogo.setFase(fase);
				jogo.setExecucao(Jogo.EXECUSSAO_PREVISTO);
				jogo.setIdApi(jogoImportacao.getPartidaId());
				jogo.setGrupo(formatarGrupo(jogoImportacao.getGrupo()));
				jogo.setRodada(parseRodada(jogoImportacao.getRodada()));
				jogo.setData(dataJogo);
				jogo.setLimiteAposta(dataJogo.minusHours(1));
				jogo.setLiberarCriacaoPalpites(true);
				jogoRepository.save(jogo);

				var timeA = new Time();
				timeA.setId(timeMandanteId);
				jogo.setTimeA(timeA);
				TimeNoJogo timeNoJogoA = new TimeNoJogo();
				timeNoJogoA.setJogo(jogo);
				timeNoJogoA.setTime(timeA);
				timeNoJogoA.setMandoDeCampo(TimeNoJogo.CASA);
				timeNoJogoRepository.save(timeNoJogoA);

				var timeB = new Time();
				timeB.setId(timeVisitanteId);
				jogo.setTimeB(timeB);
				TimeNoJogo timeNoJogoB = new TimeNoJogo();
				timeNoJogoB.setJogo(jogo);
				timeNoJogoB.setTime(timeB);
				timeNoJogoB.setMandoDeCampo(TimeNoJogo.VISITANTE);
				timeNoJogoRepository.save(timeNoJogoB);
			});
		});
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

	private Fase findOrCreateFaseFromImport(FaseImportacao faseImportacao, Bolao bolao, Competicao competicao) {
		Fase faseExistente = faseRepository.findOneByBolaoAndCompeticaoAndIdApi(bolao, competicao, faseImportacao.getFaseId());
		if (faseExistente != null) {
			return faseExistente;
		}

		Fase fase = new Fase();
		fase.setNome(faseImportacao.getNome());
		fase.setIdApi(faseImportacao.getFaseId());
		fase.setInicioPalpite(new Date());
		fase.setBolao(bolao);
		fase.setCompeticao(competicao);
		fase.setTipo(faseImportacao.getTipo());
		fase.setFaseFinal("final".equals(faseImportacao.getSlug()));
		faseRepository.save(fase);
		return fase;
	}

	private Integer findOrCreateTime(TimeImportacao timeImportacao, Map<Long, Integer> timesMap) {
		if (timeImportacao == null || timeImportacao.getTimeId() == null) {
			return null;
		}

		Integer timeId = timesMap.get(timeImportacao.getTimeId());
		if (timeId != null) {
			return timeId;
		}

		Time time = new Time();
		time.setFlag(timeImportacao.getEscudo());
		time.setIdApi(timeImportacao.getTimeId());
		time.setNome(timeImportacao.getNomePopular());
		time.setSigla(timeImportacao.getSigla());
		timeRepository.save(time);

		timesMap.put(time.getIdApi(), time.getId());
		return time.getId();
	}

	private String formatarGrupo(String grupo) {
		if (grupo == null || grupo.trim().isEmpty()) {
			return "GRUPO UNICO";
		}

		return grupo.replace("grupo-", "GRUPO ")
					.toUpperCase();
	}

	private Integer parseRodada(String rodada) {
		if (rodada == null || rodada.trim().isEmpty()) {
			return 1;
		}

		String apenasDigitos = rodada.replaceAll("\\D+", "");
		if (apenasDigitos.isEmpty()) {
			return 1;
		}

		return Integer.valueOf(apenasDigitos);
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

	public static class AtualizacaoApiPreview {
		private Integer bolaoId;
		private String bolaoNome;
		private List<FaseConferenciaItem> fasesCadastradas = new ArrayList<>();
		private List<FaseConferenciaItem> fasesNovas = new ArrayList<>();
		private List<JogoConferenciaItem> jogosCadastrados = new ArrayList<>();
		private List<JogoConferenciaItem> jogosNovos = new ArrayList<>();
		private List<CompeticaoAtualizacao> competicoes = new ArrayList<>();

		public Integer getBolaoId() {
			return bolaoId;
		}

		public void setBolaoId(Integer bolaoId) {
			this.bolaoId = bolaoId;
		}

		public String getBolaoNome() {
			return bolaoNome;
		}

		public void setBolaoNome(String bolaoNome) {
			this.bolaoNome = bolaoNome;
		}

		public List<FaseConferenciaItem> getFasesCadastradas() {
			return fasesCadastradas;
		}

		public List<FaseConferenciaItem> getFasesNovas() {
			return fasesNovas;
		}

		public List<JogoConferenciaItem> getJogosCadastrados() {
			return jogosCadastrados;
		}

		public List<JogoConferenciaItem> getJogosNovos() {
			return jogosNovos;
		}

		public List<CompeticaoAtualizacao> getCompeticoes() {
			return competicoes;
		}
	}

	public static class FaseConferenciaItem {
		private String competicaoNome;
		private Long faseIdApi;
		private String faseNome;
		private String tipo;

		public static FaseConferenciaItem from(String competicaoNome, Long faseIdApi, String faseNome, String tipo) {
			FaseConferenciaItem item = new FaseConferenciaItem();
			item.competicaoNome = competicaoNome;
			item.faseIdApi = faseIdApi;
			item.faseNome = faseNome;
			item.tipo = tipo;
			return item;
		}

		public String getCompeticaoNome() {
			return competicaoNome;
		}

		public Long getFaseIdApi() {
			return faseIdApi;
		}

		public String getFaseNome() {
			return faseNome;
		}

		public String getTipo() {
			return tipo;
		}
	}

	public static class JogoConferenciaItem {
		private String competicaoNome;
		private String faseNome;
		private Long partidaId;
		private String confronto;
		private String grupo;
		private Integer rodada;
		private String dataIso;

		public static JogoConferenciaItem from(String competicaoNome, String faseNome, Long partidaId, String mandante,
				String visitante, String grupo, Integer rodada, String dataIso) {
			JogoConferenciaItem item = new JogoConferenciaItem();
			item.competicaoNome = competicaoNome;
			item.faseNome = faseNome;
			item.partidaId = partidaId;
			item.confronto = mandante + " x " + visitante;
			item.grupo = grupo;
			item.rodada = rodada;
			item.dataIso = dataIso;
			return item;
		}

		public String getCompeticaoNome() {
			return competicaoNome;
		}

		public String getFaseNome() {
			return faseNome;
		}

		public Long getPartidaId() {
			return partidaId;
		}

		public String getConfronto() {
			return confronto;
		}

		public String getGrupo() {
			return grupo;
		}

		public Integer getRodada() {
			return rodada;
		}

		public String getDataIso() {
			return dataIso;
		}
	}

	public static class CompeticaoAtualizacao {
		private Integer competicaoId;
		private String competicaoNome;
		private List<FaseImportacao> fasesNovas = new ArrayList<>();
		private List<JogoImportacao> jogosNovos = new ArrayList<>();

		public Integer getCompeticaoId() {
			return competicaoId;
		}

		public void setCompeticaoId(Integer competicaoId) {
			this.competicaoId = competicaoId;
		}

		public String getCompeticaoNome() {
			return competicaoNome;
		}

		public void setCompeticaoNome(String competicaoNome) {
			this.competicaoNome = competicaoNome;
		}

		public List<FaseImportacao> getFasesNovas() {
			return fasesNovas;
		}

		public List<JogoImportacao> getJogosNovos() {
			return jogosNovos;
		}
	}

	public static class FaseImportacao {
		private Long faseId;
		private String nome;
		private String tipo;
		private String slug;

		public static FaseImportacao from(FaseApiDTO faseApiDTO) {
			FaseImportacao fase = new FaseImportacao();
			fase.faseId = faseApiDTO.getFaseId();
			fase.nome = faseApiDTO.getNome();
			fase.tipo = faseApiDTO.getTipo();
			fase.slug = faseApiDTO.getSlug();
			return fase;
		}

		public Long getFaseId() {
			return faseId;
		}

		public String getNome() {
			return nome;
		}

		public String getTipo() {
			return tipo;
		}

		public String getSlug() {
			return slug;
		}
	}

	public static class JogoImportacao {
		private Long faseIdApi;
		private Long partidaId;
		private String grupo;
		private String rodada;
		private String dataRealizacaoIso;
		private TimeImportacao timeMandante;
		private TimeImportacao timeVisitante;

		public static JogoImportacao from(FaseApiDTO faseApiDTO, JogoApiDTO jogoApiDTO) {
			JogoImportacao jogo = new JogoImportacao();
			jogo.faseIdApi = faseApiDTO.getFaseId();
			jogo.partidaId = jogoApiDTO.getPartidaId();
			jogo.grupo = jogoApiDTO.getGrupo();
			jogo.rodada = jogoApiDTO.getRodada();
			jogo.dataRealizacaoIso = jogoApiDTO.getDataRealizacaoIso();
			jogo.timeMandante = TimeImportacao.from(jogoApiDTO.getTimeMandante());
			jogo.timeVisitante = TimeImportacao.from(jogoApiDTO.getTimeVisitante());
			return jogo;
		}

		public Long getFaseIdApi() {
			return faseIdApi;
		}

		public Long getPartidaId() {
			return partidaId;
		}

		public String getGrupo() {
			return grupo;
		}

		public String getRodada() {
			return rodada;
		}

		public String getDataRealizacaoIso() {
			return dataRealizacaoIso;
		}

		public TimeImportacao getTimeMandante() {
			return timeMandante;
		}

		public TimeImportacao getTimeVisitante() {
			return timeVisitante;
		}
	}

	public static class TimeImportacao {
		private Long timeId;
		private String nomePopular;
		private String sigla;
		private String escudo;

		public static TimeImportacao from(TimeApiDTO timeApiDTO) {
			if (timeApiDTO == null) {
				return null;
			}

			TimeImportacao time = new TimeImportacao();
			time.timeId = timeApiDTO.getTimeId();
			time.nomePopular = timeApiDTO.getNomePopular();
			time.sigla = timeApiDTO.getSigla();
			time.escudo = timeApiDTO.getEscudo();
			return time;
		}

		public Long getTimeId() {
			return timeId;
		}

		public String getNomePopular() {
			return nomePopular;
		}

		public String getSigla() {
			return sigla;
		}

		public String getEscudo() {
			return escudo;
		}
	}
}
