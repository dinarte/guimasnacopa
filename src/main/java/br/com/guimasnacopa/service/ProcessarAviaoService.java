package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.Aviao;
import br.com.guimasnacopa.domain.AviaoParticipante;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.repository.AviaoParticipanteRepository;
import br.com.guimasnacopa.repository.AviaoRepository;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;

@Service
public class ProcessarAviaoService {

	public static final String CRITERIO_PRIMEIRO_LUGAR = "PRIMEIRO_LUGAR";
	public static final String CRITERIO_TERCEIRO_LUGAR = "TERCEIRO_LUGAR";

	@Autowired
	private AviaoRepository aviaoRepository;

	@Autowired
	private AviaoParticipanteRepository aviaoParticipanteRepository;

	@Autowired
	private ParticipanteRepository participanteRepository;

	@Autowired
	private FaseRepository faseRepository;

	@Autowired
	private PalpiteRepository palpiteRepository;

	public void processarAviao(Bolao bolao, Integer criterioEscolhido) {
		List<Participante> participantesBolao = participanteRepository.findAllByBolaoAndPgOrderByPontuacaoDesc(bolao, true);
		participantesBolao.removeIf(Participante::getAviao);
		Participante participanteReferencia = participantesBolao.stream().filter(p -> p.getClassificacao() == criterioEscolhido)
				.findAny().orElse(null);
		if (participanteReferencia == null) {
			throw new IllegalArgumentException("Participante de referencia nao encontrado para o bolao selecionado.");
		}

		Set<Palpite> palpitesParticipanteReferencia = palpiteRepository
				.findAllByParticipanteAndTipoInOrderByBolaoCompeticao(participanteReferencia,
						Arrays.asList(Palpite.ACERTAR_TIMES, Palpite.ACERTAR_CAMPEAO));

		Palpite palpiteAcertarTimes = palpitesParticipanteReferencia.stream()
				.filter(p -> Palpite.ACERTAR_TIMES.equals(p.getTipo())).findFirst().orElse(null);
		Palpite palpiteAcertarCampeao = palpitesParticipanteReferencia.stream()
				.filter(p -> Palpite.ACERTAR_CAMPEAO.equals(p.getTipo())).findFirst().orElse(null);

		if (palpiteAcertarTimes == null || palpiteAcertarCampeao == null) {
			throw new IllegalArgumentException(
					"Nao foi possivel recuperar os palpites de predicao final para o participante de referencia.");
		}

		List<Fase> fases = faseRepository.findAllByBolaoOrderByCompeticao_nomeAscOrdinalAscNomeAsc(bolao);
		Fase faseFinal = fases.stream().filter(Fase::getFaseFinal).findAny().orElse(null);
		if (faseFinal == null) {
			throw new IllegalArgumentException("Fase final nao encontrada para o bolao selecionado.");
		}

		participantesBolao.forEach(participante -> {
		});

		Double pontuacaoPossivelPredicaoFinalistas = faseFinal.getAcertarUmTime() + (faseFinal.getAcertarTimes() * 3d);
	}

	@Transactional
	public ResultadoProcessamentoAviao processar(Bolao bolao, String criterioEscolhido) {
		List<Participante> participantesBolao = participanteRepository.findAllByBolaoAndPgOrderByPontuacaoDesc(bolao, true);
		participantesBolao.removeIf(p -> p.getAviao());
		List<Fase> fases = faseRepository.findAllByBolaoOrderByCompeticao_nomeAscOrdinalAscNomeAsc(bolao);
		String criterio = normalizarCriterio(criterioEscolhido);

		double pontosPossiveisRestantes = calcularPontosPossiveis(fases);
		double pontuacaoReferencia = calcularPontuacaoReferencia(participantesBolao, criterio);

		Aviao aviao = new Aviao();
		aviao.setDataHoraProcessamento(new Date());
		aviao.setCriterio(criterio);
		aviao.setBolao(bolao);
		aviao = aviaoRepository.save(aviao);

		List<AviaoParticipante> eliminados = new ArrayList<>();
		List<ResultadoParticipanteAviao> resultadoParticipantes = new ArrayList<>();

		for (Participante participante : participantesBolao) {
			double pontuacaoAtual = nvl(participante.getPontuacao());
			double pontuacaoMaximaAtingivel = pontuacaoAtual + pontosPossiveisRestantes;
			boolean semChanceDeAtingirReferencia = pontuacaoMaximaAtingivel < pontuacaoReferencia;

			if (semChanceDeAtingirReferencia) {
				participante.setAviao(true);

				AviaoParticipante aviaoParticipante = new AviaoParticipante();
				aviaoParticipante.setAviao(aviao);
				aviaoParticipante.setParticipante(participante);
				eliminados.add(aviaoParticipante);

				resultadoParticipantes
						.add(new ResultadoParticipanteAviao(participante, pontuacaoAtual, pontuacaoMaximaAtingivel));
			}
		}

		participanteRepository.saveAll(participantesBolao);
		aviaoParticipanteRepository.saveAll(eliminados);

		ResultadoProcessamentoAviao resultado = new ResultadoProcessamentoAviao();
		resultado.setAviao(aviao);
		resultado.setCriterio(criterio);
		resultado.setDescricaoCriterio(getDescricaoCriterio(criterio));
		resultado.setPontosPossiveisRestantes(pontosPossiveisRestantes);
		resultado.setPontuacaoReferencia(pontuacaoReferencia);
		resultado.setResultadoParticipantes(resultadoParticipantes);
		return resultado;
	}

	@Transactional
	public void removerProcessamento(Integer aviaoId, Bolao bolao) {
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

	public String obterUltimoCriterioSelecionado(Bolao bolao) {
		Aviao ultimo = aviaoRepository.findTop1ByBolaoOrderByDataHoraProcessamentoDesc(bolao);
		if (ultimo == null) {
			return CRITERIO_PRIMEIRO_LUGAR;
		}
		return normalizarCriterio(ultimo.getCriterio());
	}

	public String getDescricaoCriterio(String criterio) {
		return CRITERIO_TERCEIRO_LUGAR.equals(criterio) ? "3 lugar" : "1 lugar";
	}

	public List<ResumoProcessamentoAviao> listarProcessamentos(Bolao bolao) {
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

	private double calcularPontosPossiveis(List<Fase> fases) {
		double total = 0d;
		for (Fase fase : fases) {
			double pontosBase = nvlInt(fase.getQtdJogos()) * nvl(fase.getMaximoPontuacaoPossivelPalpite());
			if (fase.getFaseFinal()) {
				double bonusFinal = nvl(fase.getAcertarUmTime()) + (nvl(fase.getAcertarTimes()) * 3d);
				total += pontosBase + bonusFinal;
			} else {
				total += pontosBase;
			}
		}
		return total;
	}

	private double calcularPontuacaoReferencia(List<Participante> participantesBolao, String criterio) {
		if (participantesBolao == null || participantesBolao.isEmpty()) {
			return 0d;
		}

		int indice = 0;
		if (CRITERIO_TERCEIRO_LUGAR.equals(criterio)) {
			indice = participantesBolao.size() >= 3 ? 2 : participantesBolao.size() - 1;
		}

		return nvl(participantesBolao.get(indice).getPontuacao());
	}

	private String normalizarCriterio(String criterio) {
		if (CRITERIO_TERCEIRO_LUGAR.equals(criterio)) {
			return CRITERIO_TERCEIRO_LUGAR;
		}
		return CRITERIO_PRIMEIRO_LUGAR;
	}

	private double nvl(Double value) {
		return value == null ? 0d : value;
	}

	private int nvlInt(Integer value) {
		return value == null ? 0 : value;
	}

	public static class ResultadoProcessamentoAviao {
		private Aviao aviao;
		private String criterio;
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

		public String getCriterio() {
			return criterio;
		}

		public void setCriterio(String criterio) {
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
