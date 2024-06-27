package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.PalpiteForProcessingVo;

public class PalpiteProcessor implements PalpiteForProcessingVo, IParticipanteBasico, IPalpiteBasico {
    
    private static final String Z_PONTUACAO_MAXIMA_ULTRAPASSADA_AJUSTE = "zPontuação máxima ultrapassada (Ajuste)";
	private static final String PAI_LOLO_NAO_DEIXOU = "Pai Lolô não deixou";
	private static final String BONUS_PLACAR_ALTO_DO_TIME_A = "Bonus placar alto do time A";
	private static final String BONUS_PLACAR_ALTO_DO_TIME_B = "Bonus placar alto do time B";
	private static final String ACERTOU_O_VENCEDOR = "Acertou o vencedor";
	private static final String ACERTOU_EMPATE = "Acertou empate";
	private static final String PREMIO_DE_PLACAR_EXATO = "Prêmio de placar exato";
	private static final String ACERTOU_GOLS_DO_TIME_B = "Acertou gols do time B";
	private static final String ACERTOU_GOLS_DO_TIME_A = "Acertou gols do time A";
	private static final Integer QTD_GOLS_CONSIDERA_PLACAR_ALTO = 4;
    
	private Long id;
	private Long jogoId;
    private Long participanteId;
    private String name;
    private String email;
    private String urlFoto;
	private String tipo;
    private Long timeAId;
    private Long timeBId;
    private Long golsTimeA;
    private Long golsTimeB;
    private Long golsJogoTimeA;
    private Long golsJogoTimeB;
    private Double pontuacaoAcertarVencedor;
    private Double pontuacaoAcertarEmpate;
    private Double pontuacaoAcertarPlacar;
    private Double pontuacaoAcertarQtdGolsTime;
    private Double pontuacaoAtingida;
    private Map<String, Double> detalhePontuacao = new HashMap<>();
	
    
    public boolean canProcess() {
    	    	
    	return (tipo.equals( Palpite.RESULTADO ) 
	    			&& Objects.nonNull(golsJogoTimeA) 
	    			&& Objects.nonNull(golsJogoTimeB) 
	    			&& Objects.nonNull(golsTimeA) 
	    			&& Objects.nonNull(golsTimeB));
    }
    
    public Double processarPontuacao() {
 
    	List<Double> processList = new ArrayList<>() ;
	
		processList.add(calcularPontuacaoPorAcertarVencedor());
		processList.add(calcularPontuacaoPorAcertarEmpate());
		processList.add(calularPontuacaoAcertarPlacar());
		processList.add(calularPontuacaoAcertarGolsTimeA());
		processList.add(calularPontuacaoAcertarGolsTimeB());
		processList.add(calularPontuacaoAcertarPlacarAltoTimeA());
		processList.add(calularPontuacaoAcertarPlacarAltoTimeB());
    	
    	Double pontuacaoAtingida = processList.stream().reduce(0.0, Double::sum);
    	Double pontuacaoAjustada = calcularAjustePontuacaoMaximaAtingida(pontuacaoAtingida);
    	
    	if (pontuacaoAjustada.equals(0.0))
    		detalhePontuacao.put(PAI_LOLO_NAO_DEIXOU, 0.0);
    	
    	this.pontuacaoAtingida = pontuacaoAjustada;
    	return pontuacaoAtingida;
    }
    
    
    public Double calcularPontuacaoPorAcertarVencedor() {
    	if ( (golsJogoTimeA > golsJogoTimeB && golsTimeA > golsJogoTimeB) || (golsJogoTimeA < golsJogoTimeB && golsTimeA < golsJogoTimeB)) {
    		detalhePontuacao.put(ACERTOU_O_VENCEDOR, pontuacaoAcertarVencedor);
    		return pontuacaoAcertarVencedor;
    	}
    	return 0.0;
    }
    
    public Double calcularPontuacaoPorAcertarEmpate() {
    	if (golsJogoTimeA == golsJogoTimeB && golsTimeA == golsTimeB) {
    		detalhePontuacao.put(ACERTOU_EMPATE, pontuacaoAcertarEmpate);
    		return pontuacaoAcertarEmpate;
    	}
    	return 0.0;
    }
    
    public Double calularPontuacaoAcertarPlacar() {
    	if (golsJogoTimeA == golsTimeA  && golsJogoTimeB == golsTimeB) {
    		detalhePontuacao.put(PREMIO_DE_PLACAR_EXATO, pontuacaoAcertarPlacar);
    		return pontuacaoAcertarPlacar;	
    	}
    	return 0.0;
    }
    
    public Double calularPontuacaoAcertarGolsTimeA() {
    	if (golsJogoTimeA == golsTimeA){
    		detalhePontuacao.put(ACERTOU_GOLS_DO_TIME_A, pontuacaoAcertarQtdGolsTime);
    		return pontuacaoAcertarQtdGolsTime;
    	}
    	return 0.0;
    }
    
    public Double calularPontuacaoAcertarGolsTimeB() {
    	if (golsJogoTimeB == golsTimeB){
    		detalhePontuacao.put(ACERTOU_GOLS_DO_TIME_B, pontuacaoAcertarQtdGolsTime);
    		return pontuacaoAcertarQtdGolsTime;
    	}
    	return 0.0;
    }
    
    public Double calularPontuacaoAcertarPlacarAltoTimeA() {
    	if (golsJogoTimeA >= QTD_GOLS_CONSIDERA_PLACAR_ALTO && golsTimeA >= QTD_GOLS_CONSIDERA_PLACAR_ALTO) {
    		detalhePontuacao.put(BONUS_PLACAR_ALTO_DO_TIME_A, pontuacaoAcertarQtdGolsTime);
    		return pontuacaoAcertarQtdGolsTime;
    	}
    	return 0.0;
    }
    
    public Double calularPontuacaoAcertarPlacarAltoTimeB() {
    	if (golsJogoTimeB >= QTD_GOLS_CONSIDERA_PLACAR_ALTO && golsTimeB >= QTD_GOLS_CONSIDERA_PLACAR_ALTO){
    		detalhePontuacao.put(BONUS_PLACAR_ALTO_DO_TIME_B, pontuacaoAcertarQtdGolsTime);
    		return pontuacaoAcertarQtdGolsTime;
    	}
    	return 0.0;
    }
    
    public Double calcularAjustePontuacaoMaximaAtingida(Double pontuacao) {
    	Double pontuacaoMaximaPermitida = calcularPontuacaoMaximaPossivel();
    	if (Double.compare(pontuacao, pontuacaoMaximaPermitida) > 0) {
    		Double ajuste = Double.sum(pontuacao, -pontuacaoMaximaPermitida);
    		detalhePontuacao.put(Z_PONTUACAO_MAXIMA_ULTRAPASSADA_AJUSTE, ajuste);
    		return pontuacaoMaximaPermitida;
    	} 
    	return pontuacao;
    }
    
    public Double getAproveitamentoPalpite() {
    	return (getPontuacaoAtingida() * 100) / calcularPontuacaoMaximaPossivel();
    }

	private Double calcularPontuacaoMaximaPossivel() {
		double[] pontuacoesPossiveis = {
							    			pontuacaoAcertarPlacar, 
							    			pontuacaoAcertarQtdGolsTime, 
							    			pontuacaoAcertarQtdGolsTime, 
							    			pontuacaoAcertarVencedor 
							    		};
    	Double pontuacaoMaximaPermitida = Arrays.stream( pontuacoesPossiveis ).sum();
		return pontuacaoMaximaPermitida;
	}
    
  
    
    
    public void escreverResultado() {
		System.out.println("---------PALPITE PROCESSOR--------");
		Double total = this.processarPontuacao();
		Map<String, Double> detalhePontuacao = null;
		detalhePontuacao = this.getDetalhePontuacao();
		detalhePontuacao = detalhePontuacao.entrySet()
		        .stream()
		        .sorted(Map.Entry.comparingByKey())
		        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
		System.out.println("Jogo:    Time A "+this.getGolsJogoTimeA()+" x "+this.getGolsJogoTimeB()+" Time B");
		System.out.println("Palpite: Time A "+this.getGolsTimeA()+" x "+this.getGolsTimeB()+" Time B");
		System.out.println("Detalhamento da Pontuação: Total(*"+total+"*)");
		
		for (String regraPontoacao : detalhePontuacao.keySet()) {
			System.out.println("  - " + regraPontoacao + ": " + detalhePontuacao.get(regraPontoacao));
		}
		System.out.println("------------------------------------");
		System.out.println("");
	}
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
	public Long getJogoId() {
		return this.jogoId;
	}
    
    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }
    
    @Override
    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }
    
    @Override
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}


    @Override
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public Long getTimeAId() {
        return timeAId;
    }

    public void setTimeAId(Long timeAId) {
        this.timeAId = timeAId;
    }

    @Override
    public Long getTimeBId() {
        return timeBId;
    }

    public void setTimeBId(Long timeBId) {
        this.timeBId = timeBId;
    }

    @Override
    public Long getGolsTimeA() {
        return golsTimeA;
    }

    public void setGolsTimeA(Long golsTimeA) {
        this.golsTimeA = golsTimeA;
    }

    @Override
    public Long getGolsTimeB() {
        return golsTimeB;
    }

    public void setGolsTimeB(Long golsTimeB) {
        this.golsTimeB = golsTimeB;
    }

    @Override
    public Long getGolsJogoTimeA() {
        return golsJogoTimeA;
    }

    public void setGolsJogoTimeA(Long golsJogoTimeA) {
        this.golsJogoTimeA = golsJogoTimeA;
    }

    @Override
    public Long getGolsJogoTimeB() {
        return golsJogoTimeB;
    }

    public void setGolsJogoTimeB(Long golsJogoTimeB) {
        this.golsJogoTimeB = golsJogoTimeB;
    }

    @Override
    public Double getPontuacaoAcertarVencedor() {
        return pontuacaoAcertarVencedor;
    }

    public void setPontuacaoAcertarVencedor(Double pontuacaoAcertarVencedor) {
        this.pontuacaoAcertarVencedor = pontuacaoAcertarVencedor;
    }

    @Override
    public Double getPontuacaoAcertarEmpate() {
        return pontuacaoAcertarEmpate;
    }

    public void setPontuacaoAcertarEmpate(Double pontuacaoAcertarEmpate) {
        this.pontuacaoAcertarEmpate = pontuacaoAcertarEmpate;
    }

    @Override
    public Double getPontuacaoAcertarPlacar() {
        return pontuacaoAcertarPlacar;
    }

    public void setPontuacaoAcertarPlacar(Double pontuacaoAcertarPlacar) {
        this.pontuacaoAcertarPlacar = pontuacaoAcertarPlacar;
    }

    @Override
    public Double getPontuacaoAcertarQtdGolsTime() {
        return pontuacaoAcertarQtdGolsTime;
    }

    public void setPontuacaoAcertarQtdGolsTime(Double pontuacaoAcertarQtdGolsTime) {
        this.pontuacaoAcertarQtdGolsTime = pontuacaoAcertarQtdGolsTime;
    }


	@Override
	public Double getPontuacaoAtingida() {
		return pontuacaoAtingida;
	}


	public void setPontuacaoAtingida(Double pontuacaoAtingida) {
		this.pontuacaoAtingida = pontuacaoAtingida;
	}


	@Override
	public Map<String, Double> getDetalhePontuacao() {
		return detalhePontuacao;
	}


	public void setDetalhePontuacao(Map<String, Double> detalhePontuacao) {
		this.detalhePontuacao = detalhePontuacao;
	}


    
}