package br.com.guimasnacopa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Participante {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	private Bolao bolao;
	
	@ManyToOne
	private Usuario usuario;
	
	private Boolean pg = false;
	
	private Double pontuacao;
	
	private Integer classificacao;
	
	private Integer aproveitamento;
	
	@Transient
	public int palpitesParaInformar;
	
	@Transient
	public int palpitesInformados;
	
	@Transient
	private Double porcentagemPalpites;

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

	

}
