package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.repository.TimeRepository;
import br.com.guimasnacopa.repository.UserRepository;

@Service
@Transactional
public class StartUpService {
	
	@Autowired 
	private UserRepository uRepo;
	
	@Autowired 
	private BolaoRepository bolaoRepo;
	
	@Autowired 
	private FaseRepository faseRepo;
	
	@Autowired 
	private TimeRepository timeRepo;
	
	@Autowired
	private JogoRepository jogoRepo;
	
	@Autowired
	private TimeNoJogoRepository timeNoJogoRepo;

	public void startupSistema() {
		
		Usuario u = new Usuario();
		u.setAdmin(true);
		u.setName("Administrador do Bolão");
		u.setEmail("admin@admin");
		u.setPass("admin");
		uRepo.save(u);
	
		Bolao bolao = new Bolao();
		bolao.setNome("Guimas na Copa da Russia 2018");
		bolao.setPermalink("russia2018");
		bolao.setValor(30.00);
		bolao.setRegras(getRegra());
		bolaoRepo.save(bolao);
		
		
		Fase grupos = new Fase();
		grupos.setBolao(bolao);
		grupos.setNome("Fase de Grupos");
		faseRepo.save(grupos);
		
		Fase oitavas = new Fase();
		oitavas.setBolao(bolao);
		oitavas.setNome("Oitavas de Final");
		faseRepo.save(oitavas);
		
		Fase quartas = new Fase();
		quartas.setNome("Quartas de Final");
		quartas.setBolao(bolao);
		faseRepo.save(quartas);
		
		Fase semifinal = new Fase();
		semifinal.setBolao(bolao);
		semifinal.setNome("Semi-final");
		faseRepo.save(semifinal);
		
		Fase gFinal = new Fase();
		gFinal.setBolao(bolao);
		gFinal.setNome("Final");
		faseRepo.save(gFinal);
		
		Map<String, String> timesMap = new HashMap<>();
		timesMap.put("Grupo A","Rússia, Arábia Saudita, Egito, Uruguai");
		timesMap.put("Grupo B","Portugal, Espanha, Marrocos, Irã");
		timesMap.put("Grupo C","França, Austrália, Peru, Dinamarca");
		timesMap.put("Grupo D","Argentina, Islândia, Croácia, Nigéria");
		timesMap.put("Grupo E","Brasil, Suíça, Costa Rica, Sérvia");
		timesMap.put("Grupo F","Alemanha, México, Suécia, Coreia do Sul");
		timesMap.put("Grupo G","Bélgica, Panamá, Tunísia, Inglaterra");
		timesMap.put("Grupo H","Polônia, Senegal, Colômbia, Japão");

		
		Set<String> gruposSet = timesMap.keySet();
		gruposSet.forEach(grupo -> {
			StringTokenizer toknz = new StringTokenizer(timesMap.get(grupo), ",");
			while (toknz.hasMoreElements()) {
				Time t = new Time();
				t.setNome(toknz.nextElement().toString().trim());
				t.setFlag(grupo);
				timeRepo.save(t);
			 }
		});
		
		gruposSet.forEach(grupo -> {
			List<Time> times = timeRepo.findAllByFlag(grupo);
			
			List<String> jogoCriado = new ArrayList<>();
			for (int i = 0; i< times.size(); i++)
				for (int y = 0; y< times.size(); y++)	
					if (i != y)
						if (!jogoCriado.contains(i+"x"+y))
							if (!jogoCriado.contains(y+"x"+i))
								criarJogo(times, i, y, grupos, grupo, jogoCriado);
			
		});
		
		
	}

	private void criarJogo(List<Time> times, int i, int y, Fase fase, String grupo, List<String> jogoCriado) {
		Jogo jogo = new Jogo();
		jogo.setFase(fase);
		jogo.setGrupo(grupo);
		jogoRepo.save(jogo);
		
		TimeNoJogo timeA = new TimeNoJogo();
		timeA.setJogo(jogo);
		timeA.setTime(times.get(i));
		timeNoJogoRepo.save(timeA);
		
		TimeNoJogo timeB = new TimeNoJogo();
		timeB.setJogo(jogo);
		timeB.setTime(times.get(y));
		timeNoJogoRepo.save(timeB);
		
		jogoCriado.add(i+"x"+y);
	}
	
	private String getRegra() {
		
		return "Regras de pontuação "
				+" <br /><br /> "
				+" Acertar o placar exato: 90 pontos. Ex: se a pessoa colocou o placar Brasil 2 x 1 Alemanha e o jogo terminou Brasil 2 x 1 Alemanha, ela marcará 90 pontos. "
				+" <br /><br />  "
				+" Acertar o vencedor do jogo e a quantidade de gols de uma seleção: 45 pontos "
				+" Ex. A pessoa colocou Brasil 2 x 0 Alemanha e o jogo terminou Brasil 2 x 1 Alemanha. Ela acertou que o Brasil ia ganhar e acertou a quantidade de gols de das seleções, assim, ela marcará 45 pontos. "
				+" <br /><br /> "
				+" Acertar apenas o vencedor do jogo: 30 pontos. Exemplo: se a pessoa colocar o placar Brasil 1 x 0 Alemanha e o jogo terminar Brasil 2 x 1 Alemanha, ela acertará que o Brasil ganhou o jogo, mas não estará acertando o número de gols de uma das seleções. Então, ira marcar 30 pontos  "
				+" <br /><br /> "
				+" Acertar apenas a quantidade de gols de um time: 10 pontos. Exemplo: Se a pessoa colocar o placar Brasil 0 x 1 Alemanha e o jogo terminar Brasil 2 x 1 Alemanha, não estará acertando o vencedor do jogo, mas estará acertando o número de gols de um dos times. Então, ira marcar 10 pontos."
				+" <br /><br /> "
				+" Acertar 1 finalista: 45 pontos " 
				+" <br /><br /> "
				+" Acertar a final: 125 pontos "
				+" <br /><br /> "
				+" Acertar  campeão da copa: 75 "
				+" <br /><br /> "
				+" A partir da segunda fase: multiplicar a pontuação de cada jogo por 1.5 "
				+" <br /><br /> "
				+" Final da copa "
				+" Multiplicar a pontuação do jogo por 2 "
				+" <br /><br /> "
				+" Valor : 30 Reais "
				+" <br /><br /> "
				+" Prazo para entrega dos placares e pagamento "
				+" Até o dia 11 de junho (segunda feira), 3 dias antes da estreia da copa do mundo. A partir do dia 12 de junho, o bolão estará, terminantemente, encerrado, para que possamos utilizar o tempo restante na alimentação  dos dados e nos últimos ajustes operacionais. "
				+" <br /><br />"
				+" OBS : Foram realizados pequenos ajustes nas pontuações relacionadas ao acerto da final , do campeão e de um finalista. "
				+" <br /><br /> "
				+" Conta para depósito ou transferência "
				+" <br /><br /> "
				+" Banco do Brasil "
				+" Ag 2623-9<br /> "
				+" Conta: 821321-6<br /> "
				+" Variação : 51 (Poupança)<br /> " 
				+" Antônio Afonso Guimarães<br /> "
				+" <br /><br /><br /> "
				+" Caixa Econômica<br /> "
				+" Ag 0034<br /> "
				+" Conta 4483-4<br /><br /> "
				+" Afonso Henrique Guimarães "
				+" <br /><br /> "
				+" Obs: Pedimos a gentileza de todos para que, ao realizar o pagamento, coloquem o comprovante aqui no Grupo! A organização agradece a atenção! ";


		
		
	}
	
}
