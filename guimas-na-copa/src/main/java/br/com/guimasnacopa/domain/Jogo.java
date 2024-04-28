package br.com.guimasnacopa.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime data;
	
	private String grupo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime limiteAposta;
	
	@OneToMany(mappedBy="jogo", cascade = CascadeType.PERSIST) 
	List<TimeNoJogo> timesNoJogo;
	
	private Boolean liberarCriacaoPalpites;
	
	@ManyToOne
	private Fase fase;
	
	private Long idApi;
	
	@Transient
	private Time timeA;

	@Transient
	private Time timeB;
	
	@Transient
	public String getTimesDescricao() {
		String timeA = timesNoJogo.get(0).getTime().getNome();
		String timeB = timesNoJogo.get(1).getTime().getNome();
		
		return timeA
				.concat(timesNoJogo.get(0).getTime().getFlag())
				.concat(" vs ")
				.concat(timesNoJogo.get(1).getTime().getFlag())
				.concat(timeB);
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

	public Time getTimeA() {
		return timeA;
	}

	public void setTimeA(Time timeA) {
		this.timeA = timeA;
	}

	public Time getTimeB() {
		return timeB;
	}

	public void setTimeB(Time timeB) {
		this.timeB = timeB;
	}

	public Boolean getLiberarCriacaoPalpites() {
		return liberarCriacaoPalpites;
	}

	public void setLiberarCriacaoPalpites(Boolean liberarCriacaoPalpites) {
		this.liberarCriacaoPalpites = liberarCriacaoPalpites;
	}

	public Long getIdApi() {
		return idApi;
	}

	public void setIdApi(Long idApi) {
		this.idApi = idApi;
	}
	
	

	
}
