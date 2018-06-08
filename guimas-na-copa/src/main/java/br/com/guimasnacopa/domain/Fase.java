package br.com.guimasnacopa.domain;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Fase {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	private Bolao bolao;
	
	private String nome;
	
	private Date inicioPalpite;
	
	private Date fimPaltpite;

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

	public Date getInicioPalpite() {
		return inicioPalpite;
	}

	public void setInicioPalpite(Date inicioPalpite) {
		this.inicioPalpite = inicioPalpite;
	}

	public Date getFimPaltpite() {
		return fimPaltpite;
	}

	public void setFimPaltpite(Date fimPaltpite) {
		this.fimPaltpite = fimPaltpite;
	}

	public Bolao getBolao() {
		return bolao;
	}

	public void setBolao(Bolao bolao) {
		this.bolao = bolao;
	}
	
	
}
