package br.com.guimasnacopa.service;

import java.util.List;
import java.util.stream.Collectors;

public class ParticipanteNoRankingProcessor {
	
	private Long participanteId;
    
	private String name;
    
    private String email;
    
    private String urlFoto;
   
	private Integer classificacao;
	
	private Boolean exibirClassificacaoNoRanking;
	
	
	
	private List<IPalpiteBasico> palpitesProcessados;
	
	
	
	public Double getPontuacao() {
		return palpitesProcessados.stream().collect(Collectors.summingDouble(IPalpiteBasico::getPontuacaoAtingida));
	}
	
	public Integer getAproveitamento() {
		Double aproveitamento = palpitesProcessados.stream().collect(Collectors.summingDouble(IPalpiteBasico::getAproveitamentoPalpite)) / palpitesProcessados.size(); 
		return aproveitamento.intValue();
	}
	
	
	
	public Integer getId() {
		return this.participanteId.intValue();
	}
	
	public void setId(Integer id) {
		this.participanteId = id.longValue();
	}
	
	public Long getParticipanteId() {
		return participanteId;
	}
	public void setParticipanteId(Long participanteId) {
		this.participanteId = participanteId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUrlFoto() {
		return urlFoto;
	}
	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}
	
	public Integer getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}
	public Boolean getExibirClassificacaoNoRanking() {
		return exibirClassificacaoNoRanking;
	}
	public void setExibirClassificacaoNoRanking(Boolean exibirClassificacaoNoRanking) {
		this.exibirClassificacaoNoRanking = exibirClassificacaoNoRanking;
	}
	
	public List<IPalpiteBasico> getPalpitesProcessados() {
		return palpitesProcessados;
	}
	public void setPalpitesProcessados(List<IPalpiteBasico> palpitesProcessados) {
		this.palpitesProcessados = palpitesProcessados;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.participanteId.equals(((ParticipanteNoRankingProcessor) obj).getParticipanteId());
	}
	
	@Override
    public int hashCode() {
        return participanteId.intValue();
    }
}