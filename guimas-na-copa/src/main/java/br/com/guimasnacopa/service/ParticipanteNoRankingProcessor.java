package br.com.guimasnacopa.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.guimasnacopa.api.domain.interfaces.IPalpiteBasico;
import br.com.guimasnacopa.api.domain.interfaces.IParticipanteNoRanking;

public class ParticipanteNoRankingProcessor implements IParticipanteNoRanking {
	
	private Long participanteId;
    
	private String name;
    
    private String email;
    
    private String urlFoto;
   
	private Integer classificacao;
	
	private Boolean exibirClassificacaoNoRanking;
	
	
	
	private List<IPalpiteBasico> palpitesProcessados;
	
	
	
	@Override
	public Double getPontuacao() {
		return palpitesProcessados.stream().filter(p->p.getPontuacaoAtingida()!=null).collect(Collectors.summingDouble(IPalpiteBasico::getPontuacaoAtingida));
	}
	
	@Override
	public Integer getAproveitamento() {
		Double aproveitamento = palpitesProcessados.stream().filter(p->p.getPontuacaoAtingida()!=null).collect(Collectors.summingDouble(IPalpiteBasico::getAproveitamentoPalpite)) / palpitesProcessados.size(); 
		return aproveitamento.intValue();
	}
	
	
	
	@Override
	public Integer getId() {
		return this.participanteId.intValue();
	}
	
	public void setId(Integer id) {
		this.participanteId = id.longValue();
	}
	
	@Override
	public Long getParticipanteId() {
		return participanteId;
	}
	public void setParticipanteId(Long participanteId) {
		this.participanteId = participanteId;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String getUrlFoto() {
		return urlFoto;
	}
	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}
	
	@Override
	public Integer getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}
	@Override
	public Boolean getExibirClassificacaoNoRanking() {
		return exibirClassificacaoNoRanking;
	}
	public void setExibirClassificacaoNoRanking(Boolean exibirClassificacaoNoRanking) {
		this.exibirClassificacaoNoRanking = exibirClassificacaoNoRanking;
	}
	
	@Override
	public List<IPalpiteBasico> getPalpitesProcessados() {
		return palpitesProcessados;
	}
	public void setPalpitesProcessados(List<IPalpiteBasico> palpitesProcessados) {
		this.palpitesProcessados = palpitesProcessados;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.participanteId.equals(((IParticipanteNoRanking) obj).getParticipanteId());
	}
	
	@Override
    public int hashCode() {
        return participanteId.intValue();
    }
}