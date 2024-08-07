package br.com.guimasnacopa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Bolao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nome;
	
	@Column(columnDefinition = "TEXT")
	private String regras;
	
	@Column(columnDefinition = "TEXT")
	private String instrucoesPagamento;
	
	private String permalink;
	
	private Double valor;
	
	private Double taxaAdministrativa;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getRegras() {
		return regras;
	}

	public void setRegras(String regras) {
		this.regras = regras;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getTaxaAdministrativa() {
		return taxaAdministrativa;
	}

	public void setTaxaAdministrativa(Double taxaAdministrativa) {
		this.taxaAdministrativa = taxaAdministrativa;
	}

	public String getInstrucoesPagamento() {
		return instrucoesPagamento;
	}

	public void setInstrucoesPagamento(String instrucoesPagamento) {
		this.instrucoesPagamento = instrucoesPagamento;
	}
	
	
}
