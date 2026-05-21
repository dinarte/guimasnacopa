package br.com.guimasnacopa.apifootbol.dto;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "campeonatoDetalhes")
public class CampeonatoDetalhesApiDTO {

    private Campeonato campeonato;

    private Partidas partidas;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(5);

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public Partidas getPartidas() {
        return partidas;
    }

    public void setPartidas(Partidas partidas) {
        this.partidas = partidas;
    }
   
    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}



	public static class Campeonato {

        @JsonProperty("campeonato_id")
        private Long campeonatoId;

        private String nome;

        private String slug;

        public Long getCampeonatoId() {
            return campeonatoId;
        }

        public void setCampeonatoId(Long campeonatoId) {
            this.campeonatoId = campeonatoId;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }
    }
	
	
	public static class Grupos{
		
		@JsonProperty("1a-rodada")
        private List<Partida> rodada1;
		
		@JsonProperty("2a-rodada")
        private List<Partida> rodada2;
		
		@JsonProperty("3a-rodada")
        private List<Partida> rodada3;
		
		@JsonProperty("grupo-a")
		private Rodadas grupoa;
		
		@JsonProperty("grupo-b")
		private Rodadas grupob;
		
		@JsonProperty("grupo-c")
		private Rodadas grupoc;
		
		@JsonProperty("grupo-d")
		private Rodadas grupod;
		
		@JsonProperty("grupo-e")
		private Rodadas grupoe;
		
		@JsonProperty("grupo-f")
		private Rodadas grupof;
		
		@JsonProperty("grupo-g")
		private Rodadas grupog;
		
		@JsonProperty("grupo-h")
		private Rodadas grupoh;
		
		@JsonProperty("grupo-i")
		private Rodadas grupoi;
		
		@JsonProperty("grupo-j")
		private Rodadas grupoj;
		
		@JsonProperty("grupo-k")
		private Rodadas grupok;
		
		@JsonProperty("grupo-l")
		private Rodadas grupol;

		public List<Partida> getRodada1() {
			return rodada1;
		}

		public void setRodada1(List<Partida> rodada1) {
			this.rodada1 = rodada1;
		}

		public List<Partida> getRodada2() {
			return rodada2;
		}

		public void setRodada2(List<Partida> rodada2) {
			this.rodada2 = rodada2;
		}

		public List<Partida> getRodada3() {
			return rodada3;
		}

		public void setRodada3(List<Partida> rodada3) {
			this.rodada3 = rodada3;
		}

		public Rodadas getGrupoa() {
			return grupoa;
		}

		public void setGrupoa(Rodadas grupoa) {
			this.grupoa = grupoa;
		}

		public Rodadas getGrupob() {
			return grupob;
		}

		public void setGrupob(Rodadas grupob) {
			this.grupob = grupob;
		}

		public Rodadas getGrupoc() {
			return grupoc;
		}

		public void setGrupoc(Rodadas grupoc) {
			this.grupoc = grupoc;
		}

		public Rodadas getGrupod() {
			return grupod;
		}

		public void setGrupod(Rodadas grupod) {
			this.grupod = grupod;
		}

		public Rodadas getGrupoe() {
			return grupoe;
		}

		public void setGrupoe(Rodadas grupoe) {
			this.grupoe = grupoe;
		}

		public Rodadas getGrupof() {
			return grupof;
		}

		public void setGrupof(Rodadas grupof) {
			this.grupof = grupof;
		}

		public Rodadas getGrupog() {
			return grupog;
		}

		public void setGrupog(Rodadas grupog) {
			this.grupog = grupog;
		}

		public Rodadas getGrupoh() {
			return grupoh;
		}

		public void setGrupoh(Rodadas grupoh) {
			this.grupoh = grupoh;
		}

		public Rodadas getGrupoi() {
			return grupoi;
		}

		public void setGrupoi(Rodadas grupoi) {
			this.grupoi = grupoi;
		}

		public Rodadas getGrupoj() {
			return grupoj;
		}

		public void setGrupoj(Rodadas grupoj) {
			this.grupoj = grupoj;
		}

		public Rodadas getGrupok() {
			return grupok;
		}

		public void setGrupok(Rodadas grupok) {
			this.grupok = grupok;
		}

		public Rodadas getGrupol() {
			return grupol;
		}

		public void setGrupol(Rodadas grupol) {
			this.grupol = grupol;
		}
	}
	
	public static class Rodadas{
		@JsonProperty("1a-rodada")
        private List<Partida> rodada1;
		
		@JsonProperty("2a-rodada")
        private List<Partida> rodada2;
		
		@JsonProperty("3a-rodada")
        private List<Partida> rodada3;

		public List<Partida> getRodada1() {
			return rodada1;
		}

		public void setRodada1(List<Partida> rodada1) {
			this.rodada1 = rodada1;
		}

		public List<Partida> getRodada2() {
			return rodada2;
		}

		public void setRodada2(List<Partida> rodada2) {
			this.rodada2 = rodada2;
		}

		public List<Partida> getRodada3() {
			return rodada3;
		}

		public void setRodada3(List<Partida> rodada3) {
			this.rodada3 = rodada3;
		}
	}

    public static class Partidas {

        @JsonProperty("segunda-fase")
        private List<Partida> segundaFase;

        @JsonProperty("oitavas-de-final")
        private List<Partida> oitavasDeFinal;

        @JsonProperty("quartas-de-final")
        private List<Partida> quartasDeFinal;

        @JsonProperty("semi-final")
        private List<Partida> semiFinal;

        @JsonProperty("disputa-3o-lugar")
        private List<Partida> disputa3oLugar;

        @JsonProperty("final")
        private List<Partida> finalPartidas;

        @JsonProperty("fase-de-grupos")
        private Grupos faseDeGrupos;

		public List<Partida> getSegundaFase() {
			return segundaFase;
		}

		public void setSegundaFase(List<Partida> segundaFase) {
			this.segundaFase = segundaFase;
		}

		public List<Partida> getOitavasDeFinal() {
			return oitavasDeFinal;
		}

		public void setOitavasDeFinal(List<Partida> oitavasDeFinal) {
			this.oitavasDeFinal = oitavasDeFinal;
		}

		public List<Partida> getQuartasDeFinal() {
			return quartasDeFinal;
		}

		public void setQuartasDeFinal(List<Partida> quartasDeFinal) {
			this.quartasDeFinal = quartasDeFinal;
		}

		public List<Partida> getSemiFinal() {
			return semiFinal;
		}

		public void setSemiFinal(List<Partida> semiFinal) {
			this.semiFinal = semiFinal;
		}

		public List<Partida> getDisputa3oLugar() {
			return disputa3oLugar;
		}

		public void setDisputa3oLugar(List<Partida> disputa3oLugar) {
			this.disputa3oLugar = disputa3oLugar;
		}

		public List<Partida> getFinalPartidas() {
			return finalPartidas;
		}

		public void setFinalPartidas(List<Partida> finalPartidas) {
			this.finalPartidas = finalPartidas;
		}

		public Grupos getFaseDeGrupos() {
			return faseDeGrupos;
		}

		public void setFaseDeGrupos(Grupos faseDeGrupos) {
			this.faseDeGrupos = faseDeGrupos;
		}


    }

    public static class Partida {

        @JsonProperty("partida_id")
        private Long partidaId;

        @JsonProperty("time_mandante")
        private TimeApiDTO timeMandante;

        @JsonProperty("time_visitante")
        private TimeApiDTO timeVisitante;

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

        public TimeApiDTO getTimeMandante() {
            return timeMandante;
        }

        public void setTimeMandante(TimeApiDTO timeMandante) {
            this.timeMandante = timeMandante;
        }

        public TimeApiDTO getTimeVisitante() {
            return timeVisitante;
        }

        public void setTimeVisitante(TimeApiDTO timeVisitante) {
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

    public static class TimeApiDTO {

        @JsonProperty("time_id")
        private Long timeId;

        @JsonProperty("nome_popular")
        private String nomePopular;

        private String sigla;

        private String escudo;

        public Long getTimeId() {
            return timeId;
        }

        public void setTimeId(Long timeId) {
            this.timeId = timeId;
        }

        public String getNomePopular() {
            return nomePopular;
        }

        public void setNomePopular(String nomePopular) {
            this.nomePopular = nomePopular;
        }

        public String getSigla() {
            return sigla;
        }

        public void setSigla(String sigla) {
            this.sigla = sigla;
        }

        public String getEscudo() {
            return escudo;
        }

        public void setEscudo(String escudo) {
            this.escudo = escudo;
        }
    }
}