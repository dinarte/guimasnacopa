package br.com.guimasnacopa.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.guimasnacopa.api.domain.interfaces.IPalpiteBasico;
import br.com.guimasnacopa.api.domain.interfaces.IParticipanteNoRanking;

@Entity
public class Participante implements IParticipanteNoRanking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private Bolao bolao;
	
	@ManyToOne
	private Usuario usuario;
	
	private Boolean pg = false;
	
	private Boolean admin = false;
	
	private Double pontuacao;
	
	private Integer classificacao;
	
	private Boolean exibirClassificacaoNoRanking;
	
	private Integer aproveitamento;
	
	@Transient
	public int palpitesParaInformar;
	
	@Transient
	public int palpitesInformados;
	
	@Transient
	private Double porcentagemPalpites;
	
	@Transient
	private Boolean userOnLine;
	

	public Boolean getAdmin() {
		return admin == null ? false : admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public int getPalpitesParaInformar() {
		return palpitesParaInformar;
	}

	public void setPalpitesParaInformar(int palpitesParaInformar) {
		this.palpitesParaInformar = palpitesParaInformar;
	}

	public int getPalpitesInformados() {
		return palpitesInformados;
	}

	public void setPalpitesInformados(int palpitesInformados) {
		this.palpitesInformados = palpitesInformados;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Bolao getBolao() {
		return bolao;
	}

	public void setBolao(Bolao bolao) {
		this.bolao = bolao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Boolean getPg() {
		return pg;
	}

	public void setPg(Boolean pg) {
		this.pg = pg;
	}

	public Double getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Double pontuacao) {
		this.pontuacao = pontuacao;
	}

	public Integer getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}

	public Integer getAproveitamento() {
		return aproveitamento;
	}

	public void setAproveitamento(Integer aproveitamento) {
		this.aproveitamento = aproveitamento;
	}

	public Double getPorcentagemPalpites() {
		return porcentagemPalpites;
	}

	public void setPorcentagemPalpites(Double porcentagemPalpites) {
		this.porcentagemPalpites = porcentagemPalpites;
	}

	public Boolean getExibirClassificacaoNoRanking() {
		return exibirClassificacaoNoRanking;
	}

	public void setExibirClassificacaoNoRanking(Boolean exibirClassificacaoNoRanking) {
		this.exibirClassificacaoNoRanking = exibirClassificacaoNoRanking;
	}

	public Boolean getUserOnLine() {
		return userOnLine == null ? false : userOnLine;
	}

	public void setUserOnLine(Boolean userOnLine) {
		this.userOnLine = userOnLine;
	}

	@Transient
	@Override
	public Long getParticipanteId() {
		return id.longValue();
	}

	@Transient
	@Override
	public String getName() {
		return getUsuario().getName();
	}

	@Transient
	@Override
	public String getEmail() {
		return getUsuario().getEmail();
	}

	@Transient
	@Override
	public String getUrlFoto() {
		return getUsuario().getUrlFoto();
	}

	@Transient
	@Override
	public List<IPalpiteBasico> getPalpitesProcessados() {
		return null;
	}

	

}
