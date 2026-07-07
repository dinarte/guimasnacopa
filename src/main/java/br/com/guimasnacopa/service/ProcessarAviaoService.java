package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.Aviao;
import br.com.guimasnacopa.domain.AviaoParticipante;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.repository.AviaoParticipanteRepository;
import br.com.guimasnacopa.repository.AviaoRepository;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;

@Service
public class ProcessarAviaoService {

	public static final Integer CRITERIO_PRIMEIRO_LUGAR = 1;
	public static final Integer CRITERIO_TERCEIRO_LUGAR = 3;

	private static final Pattern GRUPO_X_PATTERN = Pattern.compile("(?i)GRUPO\\s*-?\\s*X\\s*(\\d+)");

	@Autowired
	private AviaoRepository aviaoRepository;

	@Autowired
	private AviaoParticipanteRepository aviaoParticipanteRepository;

	@Autowired
	private ParticipanteRepository participanteRepository;

	@Autowired
	private FaseRepository faseRepository;

	@Autowired
	private JogoRepository jogoRepository;

	@Autowired
	private PalpiteRepository palpiteRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private volatile boolean criterioLegadoNormalizado;

	@Transactional
	public ResultadoProcessamentoAviao processar(Bolao bolao, Integer criterioEscolhido) {
		List<Participante> participantesBolao = participanteRepository.findAllByBolaoAndPgOrderByPontuacaoDesc(bolao, true);
		participantesBolao.removeIf(Participante::getAviao);
		if (participantesBolao.isEmpty()) {
			throw new IllegalArgumentException("Nao existem participantes elegiveis para processamento.");
		}

		List<Fase> fases = faseRepository.findAllByBolaoOrderByCompeticao_nomeAscOrdinalAscNomeAsc(bolao);
		PreProcessamentoContexto contexto = preProcessar(bolao, fases, participantesBolao, criterioEscolhido);

		Aviao aviao = new Aviao();
		aviao.setDataHoraProcessamento(new Date());
		aviao.setCriterio(contexto.criterio);
		aviao.setBolao(bolao);
		aviao = aviaoRepository.save(aviao);

		List<AviaoParticipante> eliminados = new ArrayList<>();
		List<ResultadoParticipanteAviao> resultadoParticipantes = new ArrayList<>();

		List<Participante> participantesParaProcessamento = new ArrayList<>(participantesBolao);
		participantesParaProcessamento.sort(Comparator.comparing(p -> nvl(p.getPontuacao())));

		for (Participante participante : participantesParaProcessamento) {
			if (participante.getId().equals(contexto.participanteReferencia.getId())) {
				continue;
			}

			double pontuacaoAtual = nvl(participante.getPontuacao());
			double pontosPossiveisAcertarTimes = calcularPontosPossiveisAcertarTimes(participante, contexto);
			double pontosPossiveisAcertarCampeao = calcularPontosPossiveisAcertarCampeao(participante, contexto);
			double pontuacaoMaximaAtingivel = pontuacaoAtual + contexto.pontosPossiveisTipoResultado
					+ pontosPossiveisAcertarTimes + pontosPossiveisAcertarCampeao;

			double pontosPossiveisReferencia = calcularPontosPossiveisReferencia(participante, contexto);
			double pontuacaoReferenciaComparacao = contexto.pontuacaoReferenciaAtual + pontosPossiveisReferencia;
			boolean semChanceDeAtingirReferencia = pontuacaoMaximaAtingivel < pontuacaoReferenciaComparacao;

			if (semChanceDeAtingirReferencia) {
				participante.setAviao(true);

				AviaoParticipante aviaoParticipante = new AviaoParticipante();
				aviaoParticipante.setAviao(aviao);
				aviaoParticipante.setParticipante(participante);
				eliminados.add(aviaoParticipante);

				resultadoParticipantes
						.add(new ResultadoParticipanteAviao(participante, pontuacaoAtual, pontuacaoMaximaAtingivel));
			} else {
				break;
			}
		}

		participanteRepository.saveAll(participantesBolao);
		aviaoParticipanteRepository.saveAll(eliminados);

		ResultadoProcessamentoAviao resultado = new ResultadoProcessamentoAviao();
		resultado.setAviao(aviao);
		resultado.setCriterio(contexto.criterio);
		resultado.setDescricaoCriterio(getDescricaoCriterio(contexto.criterio));
		resultado.setPontosPossiveisRestantes(contexto.pontosPossiveisTipoResultado);
		resultado.setPontuacaoReferencia(contexto.pontuacaoReferenciaAtual);
		resultado.setResultadoParticipantes(resultadoParticipantes);
		return resultado;
	}

	@Transactional
	public void removerProcessamento(Integer aviaoId, Bolao bolao) {
		normalizarCriterioLegadoAviao();
		Aviao aviao = aviaoRepository.findOneByIdAndBolao(aviaoId, bolao);
		if (aviao == null) {
			throw new IllegalArgumentException("Processamento de aviao nao encontrado para o bolao selecionado.");
		}

		List<AviaoParticipante> passageiros = aviaoParticipanteRepository.findAllByAviao(aviao);
		aviaoParticipanteRepository.deleteAllByAviao(aviao);

		for (AviaoParticipante passageiro : passageiros) {
			Participante participante = passageiro.getParticipante();
			boolean segueMarcadoEmOutroProcessamento = aviaoParticipanteRepository
					.existsByParticipanteAndAviao_idNot(participante, aviao.getId());
			if (!segueMarcadoEmOutroProcessamento) {
				participante.setAviao(false);
				participanteRepository.save(participante);
			}
		}

		aviaoRepository.delete(aviao);
	}

	public Integer obterUltimoCriterioSelecionado(Bolao bolao) {
		normalizarCriterioLegadoAviao();
		Aviao ultimo = aviaoRepository.findTop1ByBolaoOrderByDataHoraProcessamentoDesc(bolao);
		if (ultimo == null) {
			return CRITERIO_PRIMEIRO_LUGAR;
		}
		return normalizarCriterio(ultimo.getCriterio());
	}

	public String getDescricaoCriterio(Integer criterio) {
		return CRITERIO_TERCEIRO_LUGAR.equals(criterio) ? "3 lugar" : "1 lugar";
	}

	public List<ResumoProcessamentoAviao> listarProcessamentos(Bolao bolao) {
		normalizarCriterioLegadoAviao();
		List<Aviao> processamentos = aviaoRepository.findAllByBolaoOrderByDataHoraProcessamentoDesc(bolao);
		List<ResumoProcessamentoAviao> resumo = new ArrayList<>();

		processamentos.forEach(aviao -> {
			ResumoProcessamentoAviao item = new ResumoProcessamentoAviao();
			item.setAviao(aviao);
			item.setQuantidadeEliminados(aviaoParticipanteRepository.countByAviao(aviao));
			resumo.add(item);
		});

		return resumo;
	}

	public DetalheProcessamentoAviao detalharProcessamento(Integer aviaoId, Bolao bolao) {
		normalizarCriterioLegadoAviao();
		Aviao aviao = aviaoRepository.findOneByIdAndBolao(aviaoId, bolao);
		if (aviao == null) {
			throw new IllegalArgumentException("Processamento de aviao nao encontrado para o bolao selecionado.");
		}
		List<AviaoParticipante> eliminados = aviaoParticipanteRepository
				.findAllByAviaoOrderByParticipante_classificacaoAscParticipante_pontuacaoDesc(aviao);

		DetalheProcessamentoAviao detalhe = new DetalheProcessamentoAviao();
		detalhe.setAviao(aviao);
		detalhe.setEliminados(eliminados);
		return detalhe;
	}

	private PreProcessamentoContexto preProcessar(Bolao bolao, List<Fase> fases, List<Participante> participantesBolao,
			Integer criterioEscolhido) {
		PreProcessamentoContexto contexto = new PreProcessamentoContexto();
		contexto.criterio = normalizarCriterio(criterioEscolhido);

		contexto.participanteReferencia = participantesBolao.stream()
				.filter(p -> contexto.criterio.equals(p.getClassificacao()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"Participante de referencia nao encontrado para o criterio selecionado."));

		contexto.palpitesAcertarTimes = mapearPalpitePorParticipante(
				palpiteRepository.findAllByBolaoCompeticao_bolaoAndTipoOrderByBolaoCompeticao(bolao, Palpite.ACERTAR_TIMES));
		contexto.palpitesAcertarCampeao = mapearPalpitePorParticipante(palpiteRepository
				.findAllByBolaoCompeticao_bolaoAndTipoOrderByBolaoCompeticao(bolao, Palpite.ACERTAR_CAMPEAO));

		contexto.palpiteReferenciaAcertarTimes = contexto.palpitesAcertarTimes.get(contexto.participanteReferencia.getId());
		contexto.palpiteReferenciaAcertarCampeao = contexto.palpitesAcertarCampeao
				.get(contexto.participanteReferencia.getId());

		if (contexto.palpiteReferenciaAcertarTimes == null || contexto.palpiteReferenciaAcertarCampeao == null) {
			throw new IllegalArgumentException(
					"Nao foi possivel recuperar os palpites de predicao final para o participante de referencia.");
		}

		contexto.faseFinal = fases.stream().filter(Fase::getFaseFinal).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Fase final nao encontrada para o bolao selecionado."));

		List<Jogo> jogos = jogoRepository.findAllByBolaoOrderByCompeticaoGrupoData(bolao);
		contexto.timesChaveamentoA = new HashSet<>();
		contexto.timesChaveamentoB = new HashSet<>();
		contexto.timesEliminadosMataMata = new HashSet<>();

		for (Jogo jogo : jogos) {
			Integer numeroGrupoX = extrairNumeroGrupoX(jogo.getGrupo());
			if (numeroGrupoX == null || jogo.getTimesNoJogo() == null) {
				continue;
			}

			for (TimeNoJogo timeNoJogo : jogo.getTimesNoJogo()) {
				if (timeNoJogo.getTime() == null || timeNoJogo.getTime().getId() == null) {
					continue;
				}

				if (numeroGrupoX >= 1 && numeroGrupoX <= 8) {
					contexto.timesChaveamentoA.add(timeNoJogo.getTime().getId());
				} else if (numeroGrupoX >= 9 && numeroGrupoX <= 16) {
					contexto.timesChaveamentoB.add(timeNoJogo.getTime().getId());
				}
			}

			if (isJogoComResultadoDefinido(jogo)) {
				Integer vencedorId = obterTimeVencedor(jogo);
				if (vencedorId != null) {
					for (TimeNoJogo timeNoJogo : jogo.getTimesNoJogo()) {
						if (timeNoJogo.getTime() != null && timeNoJogo.getTime().getId() != null
								&& !vencedorId.equals(timeNoJogo.getTime().getId())) {
							contexto.timesEliminadosMataMata.add(timeNoJogo.getTime().getId());
						}
					}
				}
			}
		}

		contexto.pontosPossiveisTipoResultado = calcularPontosPossiveisTipoResultado(fases, jogos);
		contexto.pontuacaoReferenciaAtual = nvl(contexto.participanteReferencia.getPontuacao());
		return contexto;
	}

	private double calcularPontosPossiveisTipoResultado(List<Fase> fases, List<Jogo> jogos) {
		double total = 0d;

		Map<Integer, Integer> encerradosPorFase = new HashMap<>();
		for (Jogo jogo : jogos) {
			if (jogo.getFase() == null || jogo.getFase().getId() == null) {
				continue;
			}
			if (isJogoComResultadoDefinido(jogo)) {
				Integer faseId = jogo.getFase().getId();
				encerradosPorFase.put(faseId, encerradosPorFase.getOrDefault(faseId, 0) + 1);
			}
		}

		for (Fase fase : fases) {
			int jogosEncerrados = encerradosPorFase.getOrDefault(fase.getId(), 0);
			int jogosRestantes = Math.max(0, nvlInt(fase.getQtdJogos()) - jogosEncerrados);
			total += jogosRestantes * nvl(fase.getMaximoPontuacaoPossivelPalpite());
		}

		return total;
	}

	private double calcularPontosPossiveisAcertarTimes(Participante participante, PreProcessamentoContexto contexto) {
		Palpite palpiteAcertarTimes = contexto.palpitesAcertarTimes.get(participante.getId());
		if (palpiteAcertarTimes == null) {
			return 0d;
		}

		Set<Integer> timesEscolhidos = extrairTimesPalpite(palpiteAcertarTimes);
		if (timesEscolhidos.isEmpty()) {
			return 0d;
		}

		int qtdTimesComChance = 0;
		for (Integer timeId : timesEscolhidos) {
			if (!contexto.timesEliminadosMataMata.contains(timeId)) {
				qtdTimesComChance++;
			}
		}

		int maximoAcertosPorChaveamento = 2;
		if (timesEscolhidos.size() >= 2 && estaoNoMesmoChaveamento(timesEscolhidos, contexto)) {
			maximoAcertosPorChaveamento = 1;
		}

		int maximoAcertosPossiveis = Math.min(qtdTimesComChance, maximoAcertosPorChaveamento);
		if (maximoAcertosPossiveis >= 2) {
			return nvl(contexto.faseFinal.getAcertarTimes()) * 3d;
		}
		if (maximoAcertosPossiveis == 1) {
			return nvl(contexto.faseFinal.getAcertarTimes());
		}
		return 0d;
	}

	private double calcularPontosPossiveisAcertarCampeao(Participante participante,
			PreProcessamentoContexto contexto) {
		Palpite palpiteAcertarCampeao = contexto.palpitesAcertarCampeao.get(participante.getId());
		if (palpiteAcertarCampeao == null || palpiteAcertarCampeao.getTimeA() == null
				|| palpiteAcertarCampeao.getTimeA().getId() == null) {
			return 0d;
		}

		if (contexto.timesEliminadosMataMata.contains(palpiteAcertarCampeao.getTimeA().getId())) {
			return 0d;
		}
		return nvl(contexto.faseFinal.getAcertarUmTime());
	}

	private double calcularPontosPossiveisReferencia(Participante participante, PreProcessamentoContexto contexto) {
		double total = 0d;

		Palpite palpiteAcertarTimesParticipante = contexto.palpitesAcertarTimes.get(participante.getId());
		int quantidadeTimesIguais = contarTimesIguais(contexto.palpiteReferenciaAcertarTimes,
				palpiteAcertarTimesParticipante);
		if (quantidadeTimesIguais >= 2) {
			total += nvl(contexto.faseFinal.getAcertarTimes()) * 3d;
		} else if (quantidadeTimesIguais == 1) {
			total += nvl(contexto.faseFinal.getAcertarTimes());
		}

		Palpite palpiteAcertarCampeaoParticipante = contexto.palpitesAcertarCampeao.get(participante.getId());
		if (contexto.palpiteReferenciaAcertarCampeao != null && palpiteAcertarCampeaoParticipante != null
				&& contexto.palpiteReferenciaAcertarCampeao.getTimeA() != null
				&& palpiteAcertarCampeaoParticipante.getTimeA() != null
				&& contexto.palpiteReferenciaAcertarCampeao.getTimeA().getId() != null
				&& contexto.palpiteReferenciaAcertarCampeao.getTimeA().getId()
						.equals(palpiteAcertarCampeaoParticipante.getTimeA().getId())) {
			total += nvl(contexto.faseFinal.getAcertarUmTime());
		}

		return total;
	}

	private Map<Integer, Palpite> mapearPalpitePorParticipante(Set<Palpite> palpites) {
		Map<Integer, Palpite> mapa = new HashMap<>();
		for (Palpite palpite : palpites) {
			if (palpite.getParticipante() != null && palpite.getParticipante().getId() != null
					&& !mapa.containsKey(palpite.getParticipante().getId())) {
				mapa.put(palpite.getParticipante().getId(), palpite);
			}
		}
		return mapa;
	}

	private Set<Integer> extrairTimesPalpite(Palpite palpite) {
		Set<Integer> times = new HashSet<>();
		if (palpite == null) {
			return times;
		}
		if (palpite.getTimeA() != null && palpite.getTimeA().getId() != null) {
			times.add(palpite.getTimeA().getId());
		}
		if (palpite.getTimeB() != null && palpite.getTimeB().getId() != null) {
			times.add(palpite.getTimeB().getId());
		}
		return times;
	}

	private int contarTimesIguais(Palpite palpiteA, Palpite palpiteB) {
		if (palpiteA == null || palpiteB == null) {
			return 0;
		}
		Set<Integer> timesA = extrairTimesPalpite(palpiteA);
		Set<Integer> timesB = extrairTimesPalpite(palpiteB);
		timesA.retainAll(timesB);
		return timesA.size();
	}

	private boolean estaoNoMesmoChaveamento(Set<Integer> timesEscolhidos, PreProcessamentoContexto contexto) {
		if (timesEscolhidos.size() < 2) {
			return false;
		}
		int qtdA = 0;
		int qtdB = 0;
		for (Integer timeId : timesEscolhidos) {
			if (contexto.timesChaveamentoA.contains(timeId)) {
				qtdA++;
			}
			if (contexto.timesChaveamentoB.contains(timeId)) {
				qtdB++;
			}
		}
		return qtdA >= 2 || qtdB >= 2;
	}

	private Integer extrairNumeroGrupoX(String grupo) {
		if (grupo == null) {
			return null;
		}

		Matcher matcher = GRUPO_X_PATTERN.matcher(grupo.trim());
		if (!matcher.find()) {
			return null;
		}

		try {
			return Integer.parseInt(matcher.group(1));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private boolean isJogoComResultadoDefinido(Jogo jogo) {
		if (jogo == null || jogo.getTimesNoJogo() == null || jogo.getTimesNoJogo().size() < 2) {
			return false;
		}

		if (jogo.isEncerrado()) {
			return true;
		}

		for (TimeNoJogo timeNoJogo : jogo.getTimesNoJogo()) {
			if (timeNoJogo.getGols() == null) {
				return false;
			}
		}
		return true;
	}

	private Integer obterTimeVencedor(Jogo jogo) {
		if (jogo == null || jogo.getTimesNoJogo() == null || jogo.getTimesNoJogo().size() < 2) {
			return null;
		}

		TimeNoJogo a = jogo.getTimesNoJogo().get(0);
		TimeNoJogo b = jogo.getTimesNoJogo().get(1);
		if (a.getTime() == null || b.getTime() == null || a.getTime().getId() == null || b.getTime().getId() == null
				|| a.getGols() == null || b.getGols() == null) {
			return null;
		}

		if (a.getGols() > b.getGols()) {
			return a.getTime().getId();
		}
		if (b.getGols() > a.getGols()) {
			return b.getTime().getId();
		}

		for (TimeNoJogo timeNoJogo : jogo.getTimesNoJogo()) {
			if (timeNoJogo.getVencedor() && timeNoJogo.getTime() != null) {
				return timeNoJogo.getTime().getId();
			}
		}
		return null;
	}

	private Integer normalizarCriterio(Integer criterio) {
		if (CRITERIO_TERCEIRO_LUGAR.equals(criterio)) {
			return CRITERIO_TERCEIRO_LUGAR;
		}
		return CRITERIO_PRIMEIRO_LUGAR;
	}

	private synchronized void normalizarCriterioLegadoAviao() {
		if (criterioLegadoNormalizado) {
			return;
		}

		jdbcTemplate.update("update aviao set criterio = '3' where criterio::text = 'TERCEIRO_LUGAR'");
		jdbcTemplate.update(
				"update aviao set criterio = '1' where criterio::text = 'PRIMEIRO_LUGAR' or criterio is null or criterio::text !~ '^[0-9]+$'");
		criterioLegadoNormalizado = true;
	}

	private double nvl(Double value) {
		return value == null ? 0d : value;
	}

	private int nvlInt(Integer value) {
		return value == null ? 0 : value;
	}

	private static class PreProcessamentoContexto {
		private Integer criterio;
		private Participante participanteReferencia;
		private Palpite palpiteReferenciaAcertarTimes;
		private Palpite palpiteReferenciaAcertarCampeao;
		private Fase faseFinal;
		private double pontosPossiveisTipoResultado;
		private double pontuacaoReferenciaAtual;
		private Set<Integer> timesChaveamentoA;
		private Set<Integer> timesChaveamentoB;
		private Set<Integer> timesEliminadosMataMata;
		private Map<Integer, Palpite> palpitesAcertarTimes;
		private Map<Integer, Palpite> palpitesAcertarCampeao;
	}

	public static class ResultadoProcessamentoAviao {
		private Aviao aviao;
		private Integer criterio;
		private String descricaoCriterio;
		private Double pontosPossiveisRestantes;
		private Double pontuacaoReferencia;
		private List<ResultadoParticipanteAviao> resultadoParticipantes;

		public Aviao getAviao() {
			return aviao;
		}

		public void setAviao(Aviao aviao) {
			this.aviao = aviao;
		}

		public Double getPontosPossiveisRestantes() {
			return pontosPossiveisRestantes;
		}

		public void setPontosPossiveisRestantes(Double pontosPossiveisRestantes) {
			this.pontosPossiveisRestantes = pontosPossiveisRestantes;
		}

		public Double getPontuacaoPrimeiroColocado() {
			return pontuacaoReferencia;
		}

		public void setPontuacaoPrimeiroColocado(Double pontuacaoPrimeiroColocado) {
			this.pontuacaoReferencia = pontuacaoPrimeiroColocado;
		}

		public Double getPontuacaoReferencia() {
			return pontuacaoReferencia;
		}

		public void setPontuacaoReferencia(Double pontuacaoReferencia) {
			this.pontuacaoReferencia = pontuacaoReferencia;
		}

		public List<ResultadoParticipanteAviao> getResultadoParticipantes() {
			return resultadoParticipantes;
		}

		public void setResultadoParticipantes(List<ResultadoParticipanteAviao> resultadoParticipantes) {
			this.resultadoParticipantes = resultadoParticipantes;
		}

		public Integer getCriterio() {
			return criterio;
		}

		public void setCriterio(Integer criterio) {
			this.criterio = criterio;
		}

		public String getDescricaoCriterio() {
			return descricaoCriterio;
		}

		public void setDescricaoCriterio(String descricaoCriterio) {
			this.descricaoCriterio = descricaoCriterio;
		}
	}

	public static class ResultadoParticipanteAviao {
		private Participante participante;
		private Double pontuacaoAtual;
		private Double pontuacaoMaximaAtingivel;

		public ResultadoParticipanteAviao(Participante participante, Double pontuacaoAtual,
				Double pontuacaoMaximaAtingivel) {
			this.participante = participante;
			this.pontuacaoAtual = pontuacaoAtual;
			this.pontuacaoMaximaAtingivel = pontuacaoMaximaAtingivel;
		}

		public Participante getParticipante() {
			return participante;
		}

		public Double getPontuacaoAtual() {
			return pontuacaoAtual;
		}

		public Double getPontuacaoMaximaAtingivel() {
			return pontuacaoMaximaAtingivel;
		}
	}

	public static class ResumoProcessamentoAviao {
		private Aviao aviao;
		private Long quantidadeEliminados;

		public Aviao getAviao() {
			return aviao;
		}

		public void setAviao(Aviao aviao) {
			this.aviao = aviao;
		}

		public Long getQuantidadeEliminados() {
			return quantidadeEliminados;
		}

		public void setQuantidadeEliminados(Long quantidadeEliminados) {
			this.quantidadeEliminados = quantidadeEliminados;
		}
	}

	public static class DetalheProcessamentoAviao {
		private Aviao aviao;
		private List<AviaoParticipante> eliminados;

		public Aviao getAviao() {
			return aviao;
		}

		public void setAviao(Aviao aviao) {
			this.aviao = aviao;
		}

		public List<AviaoParticipante> getEliminados() {
			return eliminados;
		}

		public void setEliminados(List<AviaoParticipante> eliminados) {
			this.eliminados = eliminados;
		}
	}
}
