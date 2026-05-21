package br.com.guimasnacopa.apifootbol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JogoApiDTO {

    @JsonProperty("partida_id")
    private Long partidaId;

    private String fase;

    private String rodada;
    
    private String grupo;

    @JsonProperty("time_mandante")
    private CampeonatoDetalhesApiDTO.TimeApiDTO timeMandante;

    @JsonProperty("time_visitante")
    private CampeonatoDetalhesApiDTO.TimeApiDTO timeVisitante;

    private String status;

    private String slug;

    @JsonProperty("data_realizacao")
    private String dataRealizacao;

    @JsonProperty("hora_realizacao")
    private String horaRealizacao;

    @JsonProperty("data_realizacao_iso")
    private String dataRealizacaoIso;

    @JsonProperty("_link")
    private String link;

	public Long getPartidaId() {
		return partidaId;
	}

	public void setPartidaId(Long partidaId) {
		this.partidaId = partidaId;
	}

	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public String getRodada() {
		return rodada;
	}

	public void setRodada(String rodada) {
		this.rodada = rodada;
	}
	
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public CampeonatoDetalhesApiDTO.TimeApiDTO getTimeMandante() {
		return timeMandante;
	}

	public void setTimeMandante(CampeonatoDetalhesApiDTO.TimeApiDTO timeMandante) {
		this.timeMandante = timeMandante;
	}

	public CampeonatoDetalhesApiDTO.TimeApiDTO getTimeVisitante() {
		return timeVisitante;
	}

	public void setTimeVisitante(CampeonatoDetalhesApiDTO.TimeApiDTO timeVisitante) {
		this.timeVisitante = timeVisitante;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(String dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}

	public String getHoraRealizacao() {
		return horaRealizacao;
	}

	public void setHoraRealizacao(String horaRealizacao) {
		this.horaRealizacao = horaRealizacao;
	}

	public String getDataRealizacaoIso() {
		return dataRealizacaoIso;
	}

	public void setDataRealizacaoIso(String dataRealizacaoIso) {
		this.dataRealizacaoIso = dataRealizacaoIso;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}