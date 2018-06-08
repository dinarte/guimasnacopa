package br.com.guimasnacopa.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Jogo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private Date data;
	
	private String grupo;
	
	@OneToMany(mappedBy="jogo") 
	Set<TimeNoJogo> timesNoJogo;
	
	@ManyToOne
	private Fase fase;
	
	@Transient
	public String getTimesDescricao() {
		String desc = "";
		for (TimeNoJogo timeNoJogo : timesNoJogo) {
			desc = desc.concat(timeNoJogo .getTime().getNome()).concat("  ");
		}
		return desc.replaceFirst(" ", " x ");
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Fase getFase() {
		return fase;
	}

	public void setFase(Fase fase) {
		this.fase = fase;
	}

	public Set<TimeNoJogo> getTimesNoJogo() {
		return timesNoJogo;
	}

	public void setTimesNoJogo(Set<TimeNoJogo> timesNoJogo) {
		this.timesNoJogo = timesNoJogo;
	}

		
	
	
}
