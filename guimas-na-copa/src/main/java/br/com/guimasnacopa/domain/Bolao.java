package br.com.guimasnacopa.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Bolao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nome;
	
	@Column(columnDefinition = "TEXT")
	private String regras;
	
	@Column(columnDefinition = "TEXT")
	private String instrucoesPagamento;
	
	private String permalink;
	
	private Double valor;
	
	private Double taxaAdministrativa;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime dataInicio;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime dataFim;
	
	
	@Transient
	private Competicao competicao;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getRegras() {
		return regras;
	}

	public void setRegras(String regras) {
		this.regras = regras;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getTaxaAdministrativa() {
		return taxaAdministrativa;
	}

	public void setTaxaAdministrativa(Double taxaAdministrativa) {
		this.taxaAdministrativa = taxaAdministrativa;
	}

	public String getInstrucoesPagamento() {
		return instrucoesPagamento;
	}

	public void setInstrucoesPagamento(String instrucoesPagamento) {
		this.instrucoesPagamento = instrucoesPagamento;
	}

	public LocalDateTime getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDateTime dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDateTime getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDateTime dataFim) {
		this.dataFim = dataFim;
	}

	public Competicao getCompeticao() {
		return competicao;
	}

	public void setCompeticao(Competicao competicao) {
		this.competicao = competicao;
	}
		
}
