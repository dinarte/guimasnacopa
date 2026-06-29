package br.com.guimasnacopa.apifootbol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fases")
public class FaseApiDTO {

    @JsonProperty("fase_id")
    private Long faseId;
    
    @JsonProperty("campeonato_id")
    private Long campeonatoId;

    private EdicaoDTO edicao;

    private String nome;

    private String slug;

    private String status;

    private Boolean decisivo;

    private Boolean eliminatorio;

    @JsonProperty("ida_e_volta")
    private Boolean idaEVolta;

    private String tipo;

    private List<Object> grupos;

    private List<Object> chaves;

    private List<Object> rodadas;

    @JsonProperty("proxima_fase")
    private FaseReferenciaDTO proximaFase;

    @JsonProperty("fase_anterior")
    private FaseReferenciaDTO faseAnterior;

    @JsonProperty("_link")
    private String link;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(5);

    // Getters e Setters

    public Long getFaseId() {
        return faseId;
    }

    public void setFaseId(Long faseId) {
        this.faseId = faseId;
    }

    public EdicaoDTO getEdicao() {
        return edicao;
    }

    public void setEdicao(EdicaoDTO edicao) {
        this.edicao = edicao;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDecisivo() {
        return decisivo;
    }

    public void setDecisivo(Boolean decisivo) {
        this.decisivo = decisivo;
    }

    public Boolean getEliminatorio() {
        return eliminatorio;
    }

    public void setEliminatorio(Boolean eliminatorio) {
        this.eliminatorio = eliminatorio;
    }

    public Boolean getIdaEVolta() {
        return idaEVolta;
    }

    public void setIdaEVolta(Boolean idaEVolta) {
        this.idaEVolta = idaEVolta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Object> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Object> grupos) {
        this.grupos = grupos;
    }

    public List<Object> getChaves() {
        return chaves;
    }

    public void setChaves(List<Object> chaves) {
        this.chaves = chaves;
    }

    public List<Object> getRodadas() {
        return rodadas;
    }

    public void setRodadas(List<Object> rodadas) {
        this.rodadas = rodadas;
    }

    public FaseReferenciaDTO getProximaFase() {
        return proximaFase;
    }

    public void setProximaFase(FaseReferenciaDTO proximaFase) {
        this.proximaFase = proximaFase;
    }

    public FaseReferenciaDTO getFaseAnterior() {
        return faseAnterior;
    }

    public void setFaseAnterior(FaseReferenciaDTO faseAnterior) {
        this.faseAnterior = faseAnterior;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // =========================
    // CLASSES INTERNAS
    // =========================

    public Long getCampeonatoId() {
		return campeonatoId;
	}

	public void setCampeonatoId(Long campeonatoId) {
		this.campeonatoId = campeonatoId;
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



	public static class EdicaoDTO {

        @JsonProperty("edicao_id")
        private Long edicaoId;

        private String temporada;

        private String nome;

        @JsonProperty("nome_popular")
        private String nomePopular;

        private String slug;

        public Long getEdicaoId() {
            return edicaoId;
        }

        public void setEdicaoId(Long edicaoId) {
            this.edicaoId = edicaoId;
        }

        public String getTemporada() {
            return temporada;
        }

        public void setTemporada(String temporada) {
            this.temporada = temporada;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getNomePopular() {
            return nomePopular;
        }

        public void setNomePopular(String nomePopular) {
            this.nomePopular = nomePopular;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }
    }

    public static class FaseReferenciaDTO {

        @JsonProperty("fase_id")
        private Long faseId;

        private String nome;

        private String slug;

        private String tipo;

        @JsonProperty("_link")
        private String link;

        public Long getFaseId() {
            return faseId;
        }

        public void setFaseId(Long faseId) {
            this.faseId = faseId;
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

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}