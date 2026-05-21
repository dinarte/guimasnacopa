package br.com.guimasnacopa.apifootbol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

	@JsonIgnoreProperties(ignoreUnknown = true)
    public class EdicaoAtualDTO {

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