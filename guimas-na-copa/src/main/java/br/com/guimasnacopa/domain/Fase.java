package br.com.guimasnacopa.domain;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
