package br.com.guimasnacopa.ia.api;

public interface IaBolaoJogosPalpitesFlat {

	Integer getIdBolao();
	String getNomeBolao();
	String getSlugBolao();

	Integer getIdCompeticao();
	String getNomeCompeticao();

	Integer getIdFase();
	String getFaseNome();
	Boolean getFaseFinal();
	Double getFasePontuacaoAcertarVencedor();
	Double getFaseAcertarEmpate();
	Double getFasePontuacaoAcertarPlacar();
	Double getFaseAcertarQtdGolsUmDosTimes();
	Double getFasePontuacaoAcertarPlacarAlto();
	Double getFaseMaximoPontuacaoPossivelPalpite();
	Double getFaseAcertarUmTime();
	Double getFaseAcertarTimes();

	Integer getIdJogo();
	String getGrupo();
	String getRodada();
	String getExecucao();

	Integer getIdTimeA();
	String getTimeA();
	String getFlagTimeA();
	String getEmojiTimeA();
	Integer getGolsTimeA();

	Integer getIdTimeB();
	String getTimeB();
	Integer getGolsTimeB();
	String getFlagTimeB();
	String getEmojiTimeB();

	Integer getIdPalpite();
	Integer getIdParticipante();
	String getNomeParticipante();
	Double getPontuacaoParticipante();
	Integer getClassificacaoParticipante();
	String getLimiteAposta();
	Integer getGolsPalpiteTimeA();
	Integer getGolsPalpiteTimeB();
	Double getPontuacaoAtingida();
	String getRegraPontuacao();
}