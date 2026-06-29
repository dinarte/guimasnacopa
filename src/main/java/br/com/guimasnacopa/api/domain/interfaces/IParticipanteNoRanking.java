package br.com.guimasnacopa.api.domain.interfaces;

import java.util.List;

public interface IParticipanteNoRanking {

	Double getPontuacao();

	Integer getAproveitamento();

	Integer getId();

	Long getParticipanteId();

	String getName();

	String getEmail();

	String getUrlFoto();

	Integer getClassificacao();

	Boolean getExibirClassificacaoNoRanking();

	List<IPalpiteBasico> getPalpitesProcessados();

}