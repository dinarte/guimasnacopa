package br.com.guimasnacopa.ia.api;

public interface IaRanking {
	public String getParticipanteNome();
	public String getParticipanteId();
	public String getBolaoNome();
	public String getBolaoSlug();
	public String getBolaoId();
	public Integer getClassificacao();
	public Double getPontuacao();
	public Integer getAproveitamento();	
}
