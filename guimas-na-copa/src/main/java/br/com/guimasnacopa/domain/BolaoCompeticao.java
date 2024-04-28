package br.com.guimasnacopa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class BolaoCompeticao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private Bolao bolao;
	
	@ManyToOne
	private Competicao competicao;
	

	public BolaoCompeticao() {
		super();
	}

	public BolaoCompeticao(Bolao bolao, Competicao competicao) {
		super();
		this.bolao = bolao;
		this.competicao = competicao;
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

	public Competicao getCompeticao() {
		return competicao;
	}

	public void setCompeticao(Competicao competicao) {
		this.competicao = competicao;
	}
	
	
	
}
