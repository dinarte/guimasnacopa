package br.com.guimasnacopa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Competicao implements Comparable<Competicao>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Long sessao;
	
	private String nome;
	
	private String nomeAmigavel;
	
	private String logoUrl;
	
	private Long idApi;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	

	public Long getSessao() {
		return sessao;
	}

	public void setSessao(Long sessao) {
		this.sessao = sessao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeAmigavel() {
		return nomeAmigavel;
	}

	public void setNomeAmigavel(String nomeAmigavel) {
		this.nomeAmigavel = nomeAmigavel;
	}
	
	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Long getIdApi() {
		return idApi;
	}

	public void setIdApi(Long idApi) {
		this.idApi = idApi;
	}

	@Override
	public int compareTo(Competicao o) {
		return this.getNome().compareTo(o.getNome());
	}
	
	

}
