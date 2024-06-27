package br.com.guimasnacopa.service;

import java.util.Map;

public interface IPalpiteBasico {

	Long getGolsTimeA();

	Long getGolsTimeB();

	Long getGolsJogoTimeA();

	Long getGolsJogoTimeB();

	Double getPontuacaoAtingida();
	
	Double getAproveitamentoPalpite();

	Map<String, Double> getDetalhePontuacao();

}