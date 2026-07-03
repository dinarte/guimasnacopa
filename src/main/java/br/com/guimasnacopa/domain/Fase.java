package br.com.guimasnacopa.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class Fase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private Bolao bolao;
	
	@ManyToOne
	private Competicao competicao;
	
	private String nome;
	
	private String tipo;
	
	private Date inicioPalpite;
	
	private Date fimPaltpite;
	
	private Double pontuacaoAcertarPlacar;
	
	private Double pontuacaoAcertarVencedorEQtdGols;
	
	private Double pontuacaoAcertarVencedor;
	
	private Double acertarEmpate;
	
	private Double acertarQtdGolsUmDosTimes;
	
	private Double acertarUmTime;
	
	private Double acertarTimes;
	
	private Integer qtdGolsConsideraPlacarAlto;
	
	private Double pontuacaoAcertarPlacarAlto;
	
	private Double maximoPontuacaoPossivelPalpite;
	
	private Boolean faseFinal;
	
	private Integer ordinal;

	@NotNull(message = "A quantidade de jogos é obrigatoria.")
	@Min(value = 1, message = "A quantidade de jogos deve ser maior ou igual a 1.")
	@Column(name = "qtd_jogos", nullable = false, columnDefinition = "int4 default 1")
	private Integer qtdJogos = 1;
	
	private Long idApi;
	
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
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Date getInicioPalpite() {
		return inicioPalpite;
	}

	public void setInicioPalpite(Date inicioPalpite) {
		this.inicioPalpite = inicioPalpite;
	}

	public Date getFimPaltpite() {
		return fimPaltpite;
	}

	public void setFimPaltpite(Date fimPaltpite) {
		this.fimPaltpite = fimPaltpite;
	}

	public Bolao getBolao() {
		return bolao;
	}

	public void setBolao(Bolao bolao) {
		this.bolao = bolao;
	}

	public Double getPontuacaoAcertarPlacar() {
		return pontuacaoAcertarPlacar;
	}

	public void setPontuacaoAcertarPlacar(Double pontuacaoAcertarPlacar) {
		this.pontuacaoAcertarPlacar = pontuacaoAcertarPlacar;
	}

	public Double getPontuacaoAcertarVencedorEQtdGols() {
		return pontuacaoAcertarVencedorEQtdGols;
	}

	public void setPontuacaoAcertarVencedorEQtdGols(Double pontuacaoAcertarVencedorEQtdGols) {
		this.pontuacaoAcertarVencedorEQtdGols = pontuacaoAcertarVencedorEQtdGols;
	}

	public Double getPontuacaoAcertarVencedor() {
		return pontuacaoAcertarVencedor;
	}

	public void setPontuacaoAcertarVencedor(Double pontuacaoAcertarVencedor) {
		this.pontuacaoAcertarVencedor = pontuacaoAcertarVencedor;
	}

	public Double getAcertarEmpate() {
		return acertarEmpate;
	}

	public void setAcertarEmpate(Double acertarEmpate) {
		this.acertarEmpate = acertarEmpate;
	}

	public Double getAcertarQtdGolsUmDosTimes() {
		return acertarQtdGolsUmDosTimes;
	}

	public void setAcertarQtdGolsUmDosTimes(Double acertarQtdGolsDeUmDosTimes) {
		this.acertarQtdGolsUmDosTimes = acertarQtdGolsDeUmDosTimes;
	}

	public Double getAcertarUmTime() {
		return acertarUmTime;
	}

	public void setAcertarUmTime(Double acertarUmTime) {
		this.acertarUmTime = acertarUmTime;
	}

	public Double getAcertarTimes() {
		return acertarTimes;
	}

	public void setAcertarTimes(Double acertarTimes) {
		this.acertarTimes = acertarTimes;
	}

	public Competicao getCompeticao() {
		return competicao;
	}

	public void setCompeticao(Competicao competicao) {
		this.competicao = competicao;
	}

	public Long getIdApi() {
		return idApi;
	}

	public void setIdApi(Long idApi) {
		this.idApi = idApi;
	}

	public Integer getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	public Integer getQtdJogos() {
		return qtdJogos;
	}

	public void setQtdJogos(Integer qtdJogos) {
		this.qtdJogos = qtdJogos;
	}

	public Integer getQtdGolsConsideraPlacarAlto() {
		return qtdGolsConsideraPlacarAlto;
	}

	public void setQtdGolsConsideraPlacarAlto(Integer qtdGolsConsideraPlacarAlto) {
		this.qtdGolsConsideraPlacarAlto = qtdGolsConsideraPlacarAlto;
	}

	public Double getPontuacaoAcertarPlacarAlto() {
		return pontuacaoAcertarPlacarAlto;
	}

	public void setPontuacaoAcertarPlacarAlto(Double pontuacaoAcertarPlacarAlto) {
		this.pontuacaoAcertarPlacarAlto = pontuacaoAcertarPlacarAlto;
	}

	public Double getMaximoPontuacaoPossivelPalpite() {
		return maximoPontuacaoPossivelPalpite;
	}

	public void setMaximoPontuacaoPossivelPalpite(Double maximoPontuacaoPossivelPalpite) {
		this.maximoPontuacaoPossivelPalpite = maximoPontuacaoPossivelPalpite;
	}

	public Boolean getFaseFinal() {
		return faseFinal == null ? false : faseFinal;
	}

	public void setFaseFinal(Boolean faseFinal) {
		this.faseFinal = faseFinal;
	}			

}
