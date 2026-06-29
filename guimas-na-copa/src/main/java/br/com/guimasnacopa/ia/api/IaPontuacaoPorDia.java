package br.com.guimasnacopa.ia.api;

public interface IaPontuacaoPorDia {
	public String getData();
	public Integer getParticipanteId();
	public String getParticipanteNome();
	public Double getPontuacao();
	public Double getPontuacaoAcumulada();

}