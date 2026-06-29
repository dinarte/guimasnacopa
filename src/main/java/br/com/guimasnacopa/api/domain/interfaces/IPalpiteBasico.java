package br.com.guimasnacopa.api.domain.interfaces;

import java.util.Map;

public interface IPalpiteBasico {
	
	Long getId();

	Long getGolsTimeA();

	Long getGolsTimeB();

	Long getGolsJogoTimeA();

	Long getGolsJogoTimeB();

	Double getPontuacaoAtingida();
	
	Double getAproveitamentoPalpite();

	Map<String, Double> getDetalhePontuacao();

}