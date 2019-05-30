package br.com.guimasnacopa.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TimeNoJogo {

	
	public final static String VISITANTE = "Visitante";
	public final static  String CASA = "Casa";
	public final static  String INDIFERENTE = "-";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	private Jogo jogo;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private  Time time;
	
	private Integer gols;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Integer getGols() {
		return gols;
	}

	public void setGols(Integer gols) {
		this.gols = gols;
	}

	public Jogo getJogo() {
		return jogo;
	}

	public void setJogo(Jogo jogo) {
		this.jogo = jogo;
	}
	
}
