package br.com.guimasnacopa.domain;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class Palpite implements Comparable<Palpite> {

	public final static String RESULTADO = "Resultado"; 
	
	public final static String ACERTAR_TIMES = "Acertar Times"; 
	
	public final static String ACERTAR_CAMPEAO  = "Acertar Compeao";
	
	public final static Integer QTD_GOLS_BENEFICIO_PLACAR_ALTO = 4; 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String tipo;
	
	@ManyToOne
	private Participante participante;
	
	@ManyToOne
	private Jogo jogo;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Time timeA;
	
	private Integer golsTimeA;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Time timeB;
	
	private Integer golsTimeB;
	
	private LocalDateTime limiteAposta;
	
	private Double pontuacaoAtingida; 
	
	private String regraPontuacao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private BolaoCompeticao bolaoCompeticao;

	@Transient
	private Palpite palpiteComparado;
	
	@Transient
	private Integer golsDoJogoTimaA;
	
	@Transient
	private Integer golsDoJogoTimaB;
	
	

	@Transient
	public boolean isApostaAberta() {
		if (limiteAposta != null) {
			ZonedDateTime agora = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Sao_Paulo"));
			ZonedDateTime limite = ZonedDateTime.of(limiteAposta, ZoneId.of("America/Sao_Paulo"));
			return agora.isBefore(limite);
		}else {
			return true;
		}
	}
	
	@Transient
	public String getColorClass() {
		return jogo != null ? jogo.getColorClass() : "";
	}
	
	@Transient
	public String getDescricaoDaFase() {
		
		if (isResultado()) 
			return jogo.getFase().getNome() + " - " +(jogo.getGrupo() != null ? jogo.getGrupo() : "");
		if (isAcertarTimes()) 
			return "Acertar a Final";
		if (isAcertarCampeao()) {
			return "Acertar o Campeão";
		}
		
		return "";
		
	}
	
	@Transient
	public String getDescricaoGrupo() {
		
		if (isResultado()) 
			return (jogo.getGrupo() != null ? jogo.getGrupo() : "");
		if (isAcertarTimes()) 
			return "BONUS FINAL";
		if (isAcertarCampeao()) {
			return "BONUS FINAL";
		}
		
		return "";
		
	}
	
	public boolean isResultado() {
		return tipo.equals( RESULTADO );
	}
	
	public boolean isAcertarTimes() {
		return tipo.equals( ACERTAR_TIMES );
	}
	
	public boolean isAcertarCampeao() {
		return tipo.equals( ACERTAR_CAMPEAO );
	}
	
	private void incrementarPontuacao(Double pontuacao) {
		this.pontuacaoAtingida = this.pontuacaoAtingida + pontuacao;
	}
	
	public void popularGolsDoJogo() {
		getJogo().getTimesNoJogo().forEach(tmj ->{
			
			if (getTimeA().getId().equals( tmj.getTime().getId() )) {
				setGolsDoJogoTimaA(tmj.getGols());
			} 
			if (getTimeB().getId().equals( tmj.getTime().getId() )){
				setGolsDoJogoTimaB(tmj.getGols());
			} 
		});
	}
	
	public void processarPontuacaoAcertarTimes() {
		setPontuacaoAtingida(0.0);
		setRegraPontuacao("Para fora! não acertou nada");
		getJogo().getTimesNoJogo().forEach(tmj -> {
			if(tmj.getTime().getId().equals(timeA.getId()) ||
					tmj.getTime().getId().equals(timeB.getId())	){
				regraPontuacao = "Acertou um finalista";
				
				pontuacaoAtingida = pontuacaoAtingida.equals(0.0) 
						? jogo.getFase().getAcertarUmTime() 
								: jogo.getFase().getAcertarTimes();
			}
		});
	}
	
	public void processarPontuacao() {
		popularGolsDoJogo();
		
		Integer golsPalpiteTimeA = golsDoJogoTimaA;
		Integer golsPalpiteTimeB = golsDoJogoTimaB;
		Integer golsJogoTimeA = getGolsTimeA();
		Integer golsJogoTimeB = getGolsTimeB();
		
		if (golsPalpiteTimeA != null && golsPalpiteTimeB != null) {
			setPontuacaoAtingida(0.0);
			setRegraPontuacao("Para fora! não acertou nada");
			
			
			//acertar placar exato
			if (golsPalpiteTimeA.equals(golsJogoTimeA)
					&& golsPalpiteTimeB.equals(golsJogoTimeB)) {
				incrementarPontuacao(jogo.getFase().getPontuacaoAcertarPlacar());
				setRegraPontuacao("Golaço! Acertou placar exato");
				return;
			}
			//--
			
			//acertar placar exato com benefício do placar alto
			if ( 
					(golsPalpiteTimeA.equals(golsJogoTimeA) 
						|| verificaBeneficioPlacarAlto(golsPalpiteTimeA, golsJogoTimeA))
					&& (golsDoJogoTimaB.equals(golsJogoTimeB) 
							|| verificaBeneficioPlacarAlto(golsPalpiteTimeB, golsJogoTimeB) ) 
			) {
				
				incrementarPontuacao(jogo.getFase().getPontuacaoAcertarPlacar());
				setRegraPontuacao("Golaço! Acertou placar com benefíco do placar alto)");
				return;
			}
			//--
			
			
			//acertar empate  
			if (golsJogoTimeA.equals(golsJogoTimeB) 
					&& golsPalpiteTimeA.equals(golsPalpiteTimeB)) {
				incrementarPontuacao(jogo.getFase().getAcertarEmpate());
				setRegraPontuacao("Acertou empate");
				return;
			}
			//--
			
			//acertar apenas vencedor  
			if ( (golsPalpiteTimeA > golsPalpiteTimeB && golsJogoTimeA > golsJogoTimeB)
					|| (golsPalpiteTimeA > golsPalpiteTimeB && golsJogoTimeB > golsJogoTimeA) ) {
				incrementarPontuacao(jogo.getFase().getPontuacaoAcertarVencedor());
				setRegraPontuacao("Acertou o vencedor");
				return;
			}
			//--
			
			//acertar vencedor com gols  
			if ( (golsPalpiteTimeA > golsPalpiteTimeB && golsJogoTimeA > golsJogoTimeB) 
					&& (golsPalpiteTimeA.equals(golsJogoTimeA) || golsPalpiteTimeB.equals(golsJogoTimeB) )) {
				incrementarPontuacao(jogo.getFase().getPontuacaoAcertarVencedorEQtdGols());
				setRegraPontuacao("Acertou o vencedor e gols de um dos times");
				return;
			}
			
			if( (golsPalpiteTimeB > golsPalpiteTimeA && golsJogoTimeB > golsJogoTimeA) 
					&& (golsPalpiteTimeA.equals(golsJogoTimeA) || golsPalpiteTimeB.equals(golsJogoTimeB) )) {
				incrementarPontuacao(jogo.getFase().getPontuacaoAcertarVencedorEQtdGols());
				setRegraPontuacao("Acertou vencedor e gols de um dos times");
				return;
			}
			
			//acertar gols de um time 
			if (golsPalpiteTimeA.equals(golsJogoTimeA) || golsPalpiteTimeB.equals(golsJogoTimeB)) {
				incrementarPontuacao(jogo.getFase().getAcertarQtdGolsUmDosTimes());
				setRegraPontuacao("Acertou os gols de um dos times");
				return;
			}
			//----
			
		}	
	}

	private boolean verificaBeneficioPlacarAlto(Integer golsPalpiteTime, Integer golsJogoTime) {
		return golsPalpiteTime >= QTD_GOLS_BENEFICIO_PLACAR_ALTO && golsJogoTime >= QTD_GOLS_BENEFICIO_PLACAR_ALTO;
	}
	
	
	
	public void processarPontuacaoAcumulativa() {
		popularGolsDoJogo();
		
		Integer golsPalpiteTimeA = golsDoJogoTimaA;
		Integer golsPalpiteTimeB = golsDoJogoTimaB;
		Integer golsJogoTimeA = getGolsTimeA();
		Integer golsJogoTimeB = getGolsTimeB();
		
		setPontuacaoAtingida(0.0);
		
		//não jogou
		if (golsPalpiteTimeA == null && golsPalpiteTimeB == null)
			return;
		
		
		Map<String, Double> detalhePontuacao = new HashMap<>();
		
		//acertar gols do Time A 
		if (golsPalpiteTimeA.equals(golsJogoTimeA)) {
			incrementarPontuacao(jogo.getFase().getAcertarQtdGolsUmDosTimes());
			detalhePontuacao.put("Acertou gols do time A", jogo.getFase().getAcertarQtdGolsUmDosTimes());
		}

		//acertar gols do Time B 
		if (golsPalpiteTimeB.equals(golsJogoTimeB)) {
			incrementarPontuacao(jogo.getFase().getAcertarQtdGolsUmDosTimes());
			detalhePontuacao.put("Acertou gols do time B", jogo.getFase().getAcertarQtdGolsUmDosTimes());
		}
		
		//acertou vencendor  
		if ( (golsPalpiteTimeA > golsPalpiteTimeB && golsJogoTimeA > golsJogoTimeB)
				|| (golsPalpiteTimeA < golsPalpiteTimeB && golsJogoTimeA < golsJogoTimeB) ) {
			incrementarPontuacao(jogo.getFase().getPontuacaoAcertarVencedor());
			detalhePontuacao.put("Acertou o vencedor", jogo.getFase().getPontuacaoAcertarVencedor());
		}

		//acertou o placar exato
		if (golsPalpiteTimeA.equals(golsJogoTimeA)
				&& golsPalpiteTimeB.equals(golsJogoTimeB)) {
			incrementarPontuacao(jogo.getFase().getPontuacaoAcertarPlacar());
			detalhePontuacao.put("Prêmio de placar exato", jogo.getFase().getPontuacaoAcertarPlacar());
		}
		
		//acertar empate  
		if (golsJogoTimeA.equals(golsJogoTimeB) 
				&& golsPalpiteTimeA.equals(golsPalpiteTimeB)) {
			incrementarPontuacao(jogo.getFase().getAcertarEmpate());
			detalhePontuacao.put("Acertou empate", jogo.getFase().getAcertarEmpate());
		}
		
		//placar alto Time A 
		if (verificaBeneficioPlacarAlto(golsPalpiteTimeA, golsJogoTimeA)) {
			incrementarPontuacao(jogo.getFase().getAcertarQtdGolsUmDosTimes());
			detalhePontuacao.put("Bonus placar alto do time A", jogo.getFase().getAcertarQtdGolsUmDosTimes());
		}

		//Placar Alto Time B 
		if (verificaBeneficioPlacarAlto(golsPalpiteTimeB, golsJogoTimeB)) {
			incrementarPontuacao(jogo.getFase().getAcertarQtdGolsUmDosTimes());
			detalhePontuacao.put("Bonus placar alto do time B", jogo.getFase().getAcertarQtdGolsUmDosTimes());
		}
		
		if (getPontuacaoAtingida().equals(0.0)) {
			detalhePontuacao.put("Pai Lolô não deixou", 0.0);
		}
		
		Double pontuacaoMaxima = (jogo.getFase().getAcertarQtdGolsUmDosTimes() * 2) + jogo.getFase().getPontuacaoAcertarVencedor() + jogo.getFase().getPontuacaoAcertarPlacar();
		if (getPontuacaoAtingida() > pontuacaoMaxima) {
			Double ajuste = pontuacaoMaxima-getPontuacaoAtingida();
			incrementarPontuacao(ajuste);
			detalhePontuacao.put("zPontuação máxima ultrapassada (Ajuste)", ajuste);
		}
		
		try {
			setRegraPontuacao( new ObjectMapper().writeValueAsString(detalhePontuacao)) ;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Jogo getJogo() {
		return jogo;
	}

	public void setJogo(Jogo jogo) {
		this.jogo = jogo;
	}

	public Time getTimeA() {
		return timeA;
	}

	public void setTimeA(Time timeA) {
		this.timeA = timeA;
	}

	public Integer getGolsTimeA() {
		return golsTimeA;
	}

	public void setGolsTimeA(Integer golsTimeA) {
		this.golsTimeA = golsTimeA;
	}

	public Time getTimeB() {
		return timeB;
	}

	public void setTimeB(Time timeB) {
		this.timeB = timeB;
	}

	public Integer getGolsTimeB() {
		return golsTimeB;
	}

	public void setGolsTimeB(Integer golsTimeB) {
		this.golsTimeB = golsTimeB;
	}

	public Participante getParticipante() {
		return participante;
	}

	public void setParticipante(Participante participante) {
		this.participante = participante;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getKeyGolsTimeA() {
		return id + "-timeA";
	
	}
	
	public String getKeyGolsTimeB() {
		return id + "-timeB";
	}

	public LocalDateTime getLimiteAposta() {
		return limiteAposta;
	}

	public void setLimiteAposta(LocalDateTime limiteAposta) {
		this.limiteAposta = limiteAposta;
	}

	public void setPalpiteComparado(Palpite palpite) {
		this.palpiteComparado = palpite;
		
	}
	
	public Palpite getPalpiteComparado() {
		return palpiteComparado;
	}

	public Integer getGolsDoJogoTimaA() {
		return golsDoJogoTimaA;
	}

	public void setGolsDoJogoTimaA(Integer golsDoJogoTimaA) {
		this.golsDoJogoTimaA = golsDoJogoTimaA;
	}

	public Integer getGolsDoJogoTimaB() {
		return golsDoJogoTimaB;
	}

	public void setGolsDoJogoTimaB(Integer golsDoJogoTimaB) {
		this.golsDoJogoTimaB = golsDoJogoTimaB;
	}

	public Double getPontuacaoAtingida() {
		return pontuacaoAtingida;
	}

	public void setPontuacaoAtingida(Double pontuacaoAtingida) {
		this.pontuacaoAtingida = pontuacaoAtingida;
	}
	
	
	public String getRegraPontuacao() {
		return regraPontuacao;
	}

	public void setRegraPontuacao(String regraPontuacao) {
		this.regraPontuacao = regraPontuacao;
	}
	
	

	public BolaoCompeticao getBolaoCompeticao() {
		return bolaoCompeticao;
	}

	public void setBolaoCompeticao(BolaoCompeticao bolaoCompeticao) {
		this.bolaoCompeticao = bolaoCompeticao;
	}

	public static void main(String[] args) throws IOException {

		Fase fase = new Fase();
		fase.setPontuacaoAcertarPlacar(3.0);
		//fase.setPontuacaoAcertarVencedorEQtdGols(12.0);
		fase.setPontuacaoAcertarVencedor(9.0);
		fase.setAcertarEmpate(9.0);
		fase.setAcertarQtdGolsUmDosTimes(3.0);
		
		
		Jogo jogo = new Jogo();
		jogo.setFase(fase);
		jogo.setTimesNoJogo(new ArrayList<>());

		TimeNoJogo brasil = new TimeNoJogo();
		brasil.setJogo(jogo);
		jogo.getTimesNoJogo().add(brasil);
		TimeNoJogo alemanha = new TimeNoJogo();
		alemanha.setJogo(jogo);
		jogo.getTimesNoJogo().add(alemanha);
		
		Time a = new Time();
		a.setId(1);
		brasil.setTime(a);
		Time b = new Time();
		b.setId(2);
		alemanha.setTime(b);
		
		
		Palpite palpite = new Palpite();
		palpite.setJogo(jogo);
		palpite.setTimeA(a);
		palpite.setTimeB(b);
		
		
		//testarEmLote(brasil, alemanha, palpite);
		
		//System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		testarIndividual(brasil, alemanha, palpite);
		
	}

	private static void testarIndividual(TimeNoJogo brasil, TimeNoJogo alemanha, Palpite palpite) {
		
		brasil.setGols(5);
		alemanha.setGols(5);
		palpite.setGolsTimeA(4);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(0);
		alemanha.setGols(0);
		palpite.setGolsTimeA(2);
		palpite.setGolsTimeB(2);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(0);
		alemanha.setGols(0);
		palpite.setGolsTimeA(4);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(0);
		alemanha.setGols(1);
		palpite.setGolsTimeA(1);
		palpite.setGolsTimeB(1);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		
		brasil.setGols(0);
		alemanha.setGols(1);
		palpite.setGolsTimeA(3);
		palpite.setGolsTimeB(1);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		
		brasil.setGols(0);
		alemanha.setGols(1);
		palpite.setGolsTimeA(0);
		palpite.setGolsTimeB(1);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		
		brasil.setGols(2);
		alemanha.setGols(3);
		palpite.setGolsTimeA(2);
		palpite.setGolsTimeB(3);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(2);
		alemanha.setGols(3);
		palpite.setGolsTimeA(1);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(2);
		alemanha.setGols(3);
		palpite.setGolsTimeA(1);
		palpite.setGolsTimeB(2);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(2);
		alemanha.setGols(4);
		palpite.setGolsTimeA(2);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		
		brasil.setGols(2);
		alemanha.setGols(4);
		palpite.setGolsTimeA(1);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(2);
		alemanha.setGols(4);
		palpite.setGolsTimeA(5);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(4);
		alemanha.setGols(4);
		palpite.setGolsTimeA(4);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(4);
		alemanha.setGols(4);
		palpite.setGolsTimeA(2);
		palpite.setGolsTimeB(2);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(4);
		alemanha.setGols(4);
		palpite.setGolsTimeA(5);
		palpite.setGolsTimeB(5);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(4);
		alemanha.setGols(4);
		palpite.setGolsTimeA(5);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(5);
		alemanha.setGols(4);
		palpite.setGolsTimeA(5);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(5);
		alemanha.setGols(4);
		palpite.setGolsTimeA(7);
		palpite.setGolsTimeB(4);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(5);
		alemanha.setGols(4);
		palpite.setGolsTimeA(5);
		palpite.setGolsTimeB(6);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		
		brasil.setGols(5);
		alemanha.setGols(4);
		palpite.setGolsTimeA(7);
		palpite.setGolsTimeB(9);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(2);
		alemanha.setGols(4);
		palpite.setGolsTimeA(7);
		palpite.setGolsTimeB(9);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
		
		brasil.setGols(4);
		alemanha.setGols(5);
		palpite.setGolsTimeA(7);
		palpite.setGolsTimeB(9);
		palpite.processarPontuacaoAcumulativa();
		escreverResultado(palpite, brasil, alemanha);
	}

	private static void testarEmLote(TimeNoJogo brasil, TimeNoJogo alemanha, Palpite palpite) {
		final int LIMIT = 5;
		for (int i = 0; i <= LIMIT; i++ ) {
			for (int y = 0; y <= LIMIT; y++ ) {
				for (int x = 0; x <= LIMIT+2; x++ ) {
					for (int z = 0; z <= LIMIT+2; z++ ) {
						brasil.setGols(i);
						alemanha.setGols(y);
						palpite.setGolsTimeA(x);
						palpite.setGolsTimeB(z);
						palpite.processarPontuacaoAcumulativa();
						
						escreverResultado(palpite, brasil, alemanha);
					}
				}
			}
		}
	}

	private static void escreverResultado(Palpite palpite, TimeNoJogo brasil, TimeNoJogo alemanha) {
		Map<String, Double> detalhePontuacao = null;
		try {
			detalhePontuacao = new ObjectMapper().readValue(palpite.regraPontuacao, Map.class);
			detalhePontuacao = detalhePontuacao.entrySet()
		            .stream()
		            .sorted(Map.Entry.comparingByKey())
		            .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Jogo:    Time A "+brasil.getGols()+" x "+alemanha.getGols()+" Time B");
		System.out.println("Palpite: Time A "+palpite.getGolsTimeA()+" x "+palpite.getGolsTimeB()+" Time B");
		System.out.println("Detalhamento da Pontuação: Total(*"+palpite.pontuacaoAtingida+"*)");
		
		for (String regraPontoacao : detalhePontuacao.keySet()) {
			System.out.println("  - " + regraPontoacao + ": " + detalhePontuacao.get(regraPontoacao));
		}
		System.out.println("------------------------------------");
		System.out.println("");
	}

	@Override
	public int compareTo(Palpite o) {
		// TODO Auto-generated method stub
		if (Objects.nonNull(this.getLimiteAposta()) && Objects.nonNull(o.getLimiteAposta())) {
			return this.getLimiteAposta().compareTo(o.getLimiteAposta());
		} 
		return 1;
		
	}
	
}
