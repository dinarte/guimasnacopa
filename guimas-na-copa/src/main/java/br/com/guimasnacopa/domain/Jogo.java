package br.com.guimasnacopa.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Jogo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime data;
	
	private String grupo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime limiteAposta;
	
	@OneToMany(mappedBy="jogo") 
	List<TimeNoJogo> timesNoJogo;
	
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
	
	@Transient
	public String getColorClass() {
		return grupo != null ? grupo.trim().toLowerCase().replace(" ", "") : "default"; 
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
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

	public List<TimeNoJogo> getTimesNoJogo() {
		return timesNoJogo;
	}

	public void setTimesNoJogo(List<TimeNoJogo> timesNoJogo) {
		this.timesNoJogo = timesNoJogo;
	}

	public LocalDateTime getLimiteAposta() {
		return limiteAposta;
	}

	public void setLimiteAposta(LocalDateTime limiteAposta) {
		this.limiteAposta = limiteAposta;
	}

		
	
	
}
