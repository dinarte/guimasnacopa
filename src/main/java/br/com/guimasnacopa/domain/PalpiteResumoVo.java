package br.com.guimasnacopa.domain;

public class PalpiteResumoVo {
	
	private String palpite;
	
	private Long qtd;

	public String getPalpite() {
		return palpite;
	}

	public void setPalpite(String palpite) {
		this.palpite = palpite;
	}

	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((PalpiteResumoVo)obj).getPalpite().equals(this.palpite);
	}

}
