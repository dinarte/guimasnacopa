package br.com.guimasnacopa.ia.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IaBolaoJogosPalpitesDto {

	private Integer idBolao;
	private String nomeBolao;
	private String slugBolao;
	private List<IaBolaoCompeticaoDto> bolaoCompeticoes = new ArrayList<>();

	public Integer getIdBolao() {
		return idBolao;
	}

	public void setIdBolao(Integer idBolao) {
		this.idBolao = idBolao;
	}

	public String getNomeBolao() {
		return nomeBolao;
	}

	public void setNomeBolao(String nomeBolao) {
		this.nomeBolao = nomeBolao;
	}

	public String getSlugBolao() {
		return slugBolao;
	}

	public void setSlugBolao(String slugBolao) {
		this.slugBolao = slugBolao;
	}

	public List<IaBolaoCompeticaoDto> getBolaoCompeticoes() {
		return bolaoCompeticoes;
	}

	public void setBolaoCompeticoes(List<IaBolaoCompeticaoDto> bolaoCompeticoes) {
		this.bolaoCompeticoes = bolaoCompeticoes;
	}

	public static class IaBolaoCompeticaoDto {

		private IaCompeticaoDto competicao;

		public IaCompeticaoDto getCompeticao() {
			return competicao;
		}

		public void setCompeticao(IaCompeticaoDto competicao) {
			this.competicao = competicao;
		}
	}

	public static class IaCompeticaoDto {

		private Integer idCompeticao;
		private String nomeCompeticao;
		private List<IaFaseDto> fases = new ArrayList<>();

		public Integer getIdCompeticao() {
			return idCompeticao;
		}

		public void setIdCompeticao(Integer idCompeticao) {
			this.idCompeticao = idCompeticao;
		}

		public String getNomeCompeticao() {
			return nomeCompeticao;
		}

		public void setNomeCompeticao(String nomeCompeticao) {
			this.nomeCompeticao = nomeCompeticao;
		}

		public List<IaFaseDto> getFases() {
			return fases;
		}

		public void setFases(List<IaFaseDto> fases) {
			this.fases = fases;
		}
	}

	public static class IaFaseDto {

		private Integer idFase;
		private String nome;
		@JsonProperty("final")
		private Boolean faseFinal;
		private IaValoresRegrasPontuacaoDto valoresRegrasPontuacao;
		private List<IaJogoDto> jogos = new ArrayList<>();

		public Integer getIdFase() {
			return idFase;
		}

		public void setIdFase(Integer idFase) {
			this.idFase = idFase;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		@JsonProperty("final")
		public Boolean getFaseFinal() {
			return faseFinal;
		}

		public void setFaseFinal(Boolean faseFinal) {
			this.faseFinal = faseFinal;
		}

		public IaValoresRegrasPontuacaoDto getValoresRegrasPontuacao() {
			return valoresRegrasPontuacao;
		}

		public void setValoresRegrasPontuacao(IaValoresRegrasPontuacaoDto valoresRegrasPontuacao) {
			this.valoresRegrasPontuacao = valoresRegrasPontuacao;
		}

		public List<IaJogoDto> getJogos() {
			return jogos;
		}

		public void setJogos(List<IaJogoDto> jogos) {
			this.jogos = jogos;
		}
	}

	public static class IaValoresRegrasPontuacaoDto {

		private IaMensagensDto mensagens;
		@JsonProperty("final")
		private IaRegraFinalDto finalRegras;
		private IaRegraBonusDto bonus;
		private IaRegraResultadoDto resultado;

		public IaMensagensDto getMensagens() {
			return mensagens;
		}

		public void setMensagens(IaMensagensDto mensagens) {
			this.mensagens = mensagens;
		}

		@JsonProperty("final")
		public IaRegraFinalDto getFinalRegras() {
			return finalRegras;
		}

		public void setFinalRegras(IaRegraFinalDto finalRegras) {
			this.finalRegras = finalRegras;
		}

		public IaRegraBonusDto getBonus() {
			return bonus;
		}

		public void setBonus(IaRegraBonusDto bonus) {
			this.bonus = bonus;
		}

		public IaRegraResultadoDto getResultado() {
			return resultado;
		}

		public void setResultado(IaRegraResultadoDto resultado) {
			this.resultado = resultado;
		}
	}

	public static class IaMensagensDto {
		private final String acertouCampeao = "Acertou o campeão";
		private final String bonusPorAcertarOsDoisFinalistas = "Bonus por acertar os dois finalistas";
		private final String acertouFinalistaB = "Acertou o finalista B";
		private final String acertouFinalistaA = "Acertou o finalista A";
		private final String zPontuacaoMaximaUltrapassadaAjuste = "zPontuação máxima ultrapassada (Ajuste)";
		private final String paiLoloNaoDeixou = "Pai Lolô não deixou";
		private final String bonusPlacarAltoDoTimeA = "Bonus placar alto do time A";
		private final String bonusPlacarAltoDoTimeB = "Bonus placar alto do time B";
		private final String acertouVencedor = "Acertou o vencedor";
		private final String acertouEmpate = "Acertou empate";
		private final String premioDePlacarExato = "Prêmio de placar exato";
		private final String acertouGolsDoTimeB = "Acertou gols do time B";
		private final String acertouGolsDoTimeA = "Acertou gols do time A";

		public String getAcertouCampeao() { return acertouCampeao; }
		public String getBonusPorAcertarOsDoisFinalistas() { return bonusPorAcertarOsDoisFinalistas; }
		public String getAcertouFinalistaB() { return acertouFinalistaB; }
		public String getAcertouFinalistaA() { return acertouFinalistaA; }
		public String getzPontuacaoMaximaUltrapassadaAjuste() { return zPontuacaoMaximaUltrapassadaAjuste; }
		public String getPaiLoloNaoDeixou() { return paiLoloNaoDeixou; }
		public String getBonusPlacarAltoDoTimeA() { return bonusPlacarAltoDoTimeA; }
		public String getBonusPlacarAltoDoTimeB() { return bonusPlacarAltoDoTimeB; }
		public String getAcertouVencedor() { return acertouVencedor; }
		public String getAcertouEmpate() { return acertouEmpate; }
		public String getPremioDePlacarExato() { return premioDePlacarExato; }
		public String getAcertouGolsDoTimeB() { return acertouGolsDoTimeB; }
		public String getAcertouGolsDoTimeA() { return acertouGolsDoTimeA; }
	}

	public static class IaRegraFinalDto {
		private Double acertouCampeao;
		private Double bonusPorAcertarOsDoisFinalistas;
		private Double acertouFinalistaB;
		private Double acertouFinalistaA;

		public Double getAcertouCampeao() { return acertouCampeao; }
		public void setAcertouCampeao(Double acertouCampeao) { this.acertouCampeao = acertouCampeao; }
		public Double getBonusPorAcertarOsDoisFinalistas() { return bonusPorAcertarOsDoisFinalistas; }
		public void setBonusPorAcertarOsDoisFinalistas(Double v) { this.bonusPorAcertarOsDoisFinalistas = v; }
		public Double getAcertouFinalistaB() { return acertouFinalistaB; }
		public void setAcertouFinalistaB(Double acertouFinalistaB) { this.acertouFinalistaB = acertouFinalistaB; }
		public Double getAcertouFinalistaA() { return acertouFinalistaA; }
		public void setAcertouFinalistaA(Double acertouFinalistaA) { this.acertouFinalistaA = acertouFinalistaA; }
	}

	public static class IaRegraBonusDto {
		private Double bonusPlacarAltoDoTimeA;
		private Double bonusPlacarAltoDoTimeB;

		public Double getBonusPlacarAltoDoTimeA() { return bonusPlacarAltoDoTimeA; }
		public void setBonusPlacarAltoDoTimeA(Double v) { this.bonusPlacarAltoDoTimeA = v; }
		public Double getBonusPlacarAltoDoTimeB() { return bonusPlacarAltoDoTimeB; }
		public void setBonusPlacarAltoDoTimeB(Double v) { this.bonusPlacarAltoDoTimeB = v; }
	}

	public static class IaRegraResultadoDto {
		private Double acertouVencedor;
		private Double acertouEmpate;
		private Double premioDePlacarExato;
		private Double acertouGolsDoTimeB;
		private Double acertouGolsDoTimeA;
		private Double pontuacaoMaximaPermitida;

		public Double getAcertouVencedor() { return acertouVencedor; }
		public void setAcertouVencedor(Double v) { this.acertouVencedor = v; }
		public Double getAcertouEmpate() { return acertouEmpate; }
		public void setAcertouEmpate(Double v) { this.acertouEmpate = v; }
		public Double getPremioDePlacarExato() { return premioDePlacarExato; }
		public void setPremioDePlacarExato(Double v) { this.premioDePlacarExato = v; }
		public Double getAcertouGolsDoTimeB() { return acertouGolsDoTimeB; }
		public void setAcertouGolsDoTimeB(Double v) { this.acertouGolsDoTimeB = v; }
		public Double getAcertouGolsDoTimeA() { return acertouGolsDoTimeA; }
		public void setAcertouGolsDoTimeA(Double v) { this.acertouGolsDoTimeA = v; }
		public Double getPontuacaoMaximaPermitida() { return pontuacaoMaximaPermitida; }
		public void setPontuacaoMaximaPermitida(Double v) { this.pontuacaoMaximaPermitida = v; }
	}

	public static class IaJogoDto {

		private Integer idJogo;
		private String grupo;
		private String rodada;
		private String execucao;
		private Integer idTimeA;
		private String timeA;
		private String flagTimeA;
		private String emojiTimeA;
		private Integer golsTimeA;
		private Integer idTimeB;
		private String timeB;
		private Integer golsTimeB;
		private String flagTimeB;
		private String emojiTimeB;
		private List<IaPalpiteDto> palpites = new ArrayList<>();

		public Integer getIdJogo() {
			return idJogo;
		}

		public void setIdJogo(Integer idJogo) {
			this.idJogo = idJogo;
		}

		public String getGrupo() {
			return grupo;
		}

		public void setGrupo(String grupo) {
			this.grupo = grupo;
		}

		public String getRodada() {
			return rodada;
		}

		public void setRodada(String rodada) {
			this.rodada = rodada;
		}

		public String getExecucao() {
			return execucao;
		}

		public void setExecucao(String execucao) {
			this.execucao = execucao;
		}

		public Integer getIdTimeA() {
			return idTimeA;
		}

		public void setIdTimeA(Integer idTimeA) {
			this.idTimeA = idTimeA;
		}

		public String getTimeA() {
			return timeA;
		}

		public void setTimeA(String timeA) {
			this.timeA = timeA;
		}

		public String getFlagTimeA() {
			return flagTimeA;
		}

		public void setFlagTimeA(String flagTimeA) {
			this.flagTimeA = flagTimeA;
		}

		public String getEmojiTimeA() {
			return emojiTimeA;
		}

		public void setEmojiTimeA(String emojiTimeA) {
			this.emojiTimeA = emojiTimeA;
		}

		public Integer getGolsTimeA() {
			return golsTimeA;
		}

		public void setGolsTimeA(Integer golsTimeA) {
			this.golsTimeA = golsTimeA;
		}

		public Integer getIdTimeB() {
			return idTimeB;
		}

		public void setIdTimeB(Integer idTimeB) {
			this.idTimeB = idTimeB;
		}

		public String getTimeB() {
			return timeB;
		}

		public void setTimeB(String timeB) {
			this.timeB = timeB;
		}

		public Integer getGolsTimeB() {
			return golsTimeB;
		}

		public void setGolsTimeB(Integer golsTimeB) {
			this.golsTimeB = golsTimeB;
		}

		public String getFlagTimeB() {
			return flagTimeB;
		}

		public void setFlagTimeB(String flagTimeB) {
			this.flagTimeB = flagTimeB;
		}

		public String getEmojiTimeB() {
			return emojiTimeB;
		}

		public void setEmojiTimeB(String emojiTimeB) {
			this.emojiTimeB = emojiTimeB;
		}

		public List<IaPalpiteDto> getPalpites() {
			return palpites;
		}

		public void setPalpites(List<IaPalpiteDto> palpites) {
			this.palpites = palpites;
		}
	}

	public static class IaPalpiteDto {

		private Integer idPalpite;
		private Integer idParticipante;
		private String nomeParticipante;
		private Double pontuacaoParticipante;
		private Integer classificacaoParticipante;
		private String limiteAposta;
		private Integer golsTimeA;
		private Integer golsTimeB;
		private Double pontuacaoAtingida;
		private Map<String, Object> regraPontuacao = new LinkedHashMap<>();

		public Integer getIdPalpite() {
			return idPalpite;
		}

		public void setIdPalpite(Integer idPalpite) {
			this.idPalpite = idPalpite;
		}

		public Integer getIdParticipante() {
			return idParticipante;
		}

		public void setIdParticipante(Integer idParticipante) {
			this.idParticipante = idParticipante;
		}

		public String getNomeParticipante() {
			return nomeParticipante;
		}

		public void setNomeParticipante(String nomeParticipante) {
			this.nomeParticipante = nomeParticipante;
		}

		public Double getPontuacaoParticipante() {
			return pontuacaoParticipante;
		}

		public void setPontuacaoParticipante(Double pontuacaoParticipante) {
			this.pontuacaoParticipante = pontuacaoParticipante;
		}

		public Integer getClassificacaoParticipante() {
			return classificacaoParticipante;
		}

		public void setClassificacaoParticipante(Integer classificacaoParticipante) {
			this.classificacaoParticipante = classificacaoParticipante;
		}

		public String getLimiteAposta() {
			return limiteAposta;
		}

		public void setLimiteAposta(String limiteAposta) {
			this.limiteAposta = limiteAposta;
		}

		public Integer getGolsTimeA() {
			return golsTimeA;
		}

		public void setGolsTimeA(Integer golsTimeA) {
			this.golsTimeA = golsTimeA;
		}

		public Integer getGolsTimeB() {
			return golsTimeB;
		}

		public void setGolsTimeB(Integer golsTimeB) {
			this.golsTimeB = golsTimeB;
		}

		public Double getPontuacaoAtingida() {
			return pontuacaoAtingida;
		}

		public void setPontuacaoAtingida(Double pontuacaoAtingida) {
			this.pontuacaoAtingida = pontuacaoAtingida;
		}

		public Map<String, Object> getRegraPontuacao() {
			return regraPontuacao;
		}

		public void setRegraPontuacao(Map<String, Object> regraPontuacao) {
			this.regraPontuacao = regraPontuacao;
		}
	}
}