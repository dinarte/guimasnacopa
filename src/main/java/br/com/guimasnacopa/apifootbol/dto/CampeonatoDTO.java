package br.com.guimasnacopa.apifootbol.dto;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "campeonatos")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CampeonatoDTO {

    @JsonProperty("campeonato_id")
    private Long campeonatoId;

    private String nome;

    private String slug;

    @JsonProperty("nome_popular")
    private String nomePopular;

    @JsonProperty("edicao_atual")
    private EdicaoAtualDTO edicaoAtual;

    private String status;

    private String tipo;

    private String logo;

    private String regiao;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(5);

    @JsonProperty("_link")
    private String link;

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

    public String getNomePopular() {
        return nomePopular;
    }

    public void setNomePopular(String nomePopular) {
        this.nomePopular = nomePopular;
    }

    public EdicaoAtualDTO getEdicaoAtual() {
        return edicaoAtual;
    }

    public void setEdicaoAtual(EdicaoAtualDTO edicaoAtual) {
        this.edicaoAtual = edicaoAtual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
}