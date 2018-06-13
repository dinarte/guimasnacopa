package br.com.guimasnacopa.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Palpite {

	public final static String RESULTADO = "Resultado"; 
	
	public final static String ACERTAR_TIMES = "Acertar Times"; 
	
	public final static String ACERTAR_CAMPEAO  = "Acertar Compeao";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String tipo;
	
	@ManyToOne
	private Participante participante;
	
	@ManyToOne
	private Jogo jogo;
	
	@ManyToOne
	private Time timeA;
	
	private Integer golsTimeA;
	
	@ManyToOne
	private Time timeB;
	
	private Integer golsTimeB;
	
	private LocalDateTime limiteAposta;

	@Transient
	private Palpite palpiteComparado;
	
	@Transient
	private Integer golsDoJogoTimaA;
	
	@Transient
	private Integer golsDoJogoTimaB;
	


	@Transient
	public boolean isApostaAberta() {
		if (limiteAposta != null) {
			ZonedDateTime agora = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Sao_Paulo"));
			ZonedDateTime limite = ZonedDateTime.of(limiteAposta, ZoneId.of("America/Sao_Paulo"));
			return agora.isBefore(limite);
		}else {
			return true;
		}
	}
	
	@Transient
	public String getColorClass() {
		return jogo != null ? jogo.getColorClass() : "";
	}
	
	@Transient
	public String getDescricaoDaFase() {
		
		if (jogo != null) 
			return jogo.getFase().getNome() + " - " +(jogo.getGrupo() != null ? jogo.getGrupo() : "");
		if (isAcertarTimes()) 
			return "Acertar a Final";
		if (isAcertarCampeao()) {
			return "Acertar o Campeão";
		}
		
		return "";
		
	}
	
	public boolean isResultado() {
		return tipo.equals( RESULTADO );
	}
	
	public boolean isAcertarTimes() {
		return tipo.equals( ACERTAR_TIMES );
	}
	
	public boolean isAcertarCampeao() {
		return tipo.equals( ACERTAR_CAMPEAO );
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Jogo getJogo() {
		return jogo;
	}

	public void setJogo(Jogo jogo) {
		this.jogo = jogo;
	}

	public Time getTimeA() {
		return timeA;
	}

	public void setTimeA(Time timeA) {
		this.timeA = timeA;
	}

	public Integer getGolsTimeA() {
		return golsTimeA;
	}

	public void setGolsTimeA(Integer golsTimeA) {
		this.golsTimeA = golsTimeA;
	}

	public Time getTimeB() {
		return timeB;
	}

	public void setTimeB(Time timeB) {
		this.timeB = timeB;
	}

	public Integer getGolsTimeB() {
		return golsTimeB;
	}

	public void setGolsTimeB(Integer golsTimeB) {
		this.golsTimeB = golsTimeB;
	}

	public Participante getParticipante() {
		return participante;
	}

	public void setParticipante(Participante participante) {
		this.participante = participante;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getKeyGolsTimeA() {
		return id + "-timeA";
	
	}
	
	public String getKeyGolsTimeB() {
		return id + "-timeB";
	}

	public LocalDateTime getLimiteAposta() {
		return limiteAposta;
	}

	public void setLimiteAposta(LocalDateTime limiteAposta) {
		this.limiteAposta = limiteAposta;
	}

	public void setPalpiteComparado(Palpite palpite) {
		this.palpiteComparado = palpite;
		
	}
	
	public Palpite getPalpiteComparado() {
		return palpiteComparado;
	}

	public Integer getGolsDoJogoTimaA() {
		return golsDoJogoTimaA;
	}

	public void setGolsDoJogoTimaA(Integer golsDoJogoTimaA) {
		this.golsDoJogoTimaA = golsDoJogoTimaA;
	}

	public Integer getGolsDoJogoTimaB() {
		return golsDoJogoTimaB;
	}

	public void setGolsDoJogoTimaB(Integer golsDoJogoTimaB) {
		this.golsDoJogoTimaB = golsDoJogoTimaB;
	}
	
}
