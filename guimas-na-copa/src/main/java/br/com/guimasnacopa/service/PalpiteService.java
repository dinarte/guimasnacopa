package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.BolaoCompeticao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.repository.BolaoCompeticaoRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;

@Transactional
@Service
public class PalpiteService {
	
	@Autowired
	PalpiteRepository palpiteRepo;
	
	@Autowired
	JogoRepository jogoRepo;
	
	@Autowired
	BolaoCompeticaoRepository bolaoCompeticaoRepo;
	
	@Autowired
	TimeNoJogoRepository timesNoJogoRepo;
	
	@Autowired
	EntityManager entityManager;
	
	public List<Palpite> criarPalpitesCasoNecessario(Participante p) {
		List<Jogo> jogos = jogoRepo.findAllByBolaoAndPrticipanteAndNotExistsPalpiteOrderByFaseGrupoData(p.getBolao(),p);
		List<BolaoCompeticao> bolaoCompeticaoList = bolaoCompeticaoRepo.findAllByBolao(p.getBolao()); 
		if (jogos != null && jogos.size() > 0) {
			
			List<Palpite> novosPalpites = new ArrayList<>();
			jogos.forEach(jogo -> {
				
				BolaoCompeticao bolaoCompeticao = getBolaoCompeticao(bolaoCompeticaoList, jogo);
				
				Palpite palpite = new Palpite();
				palpite.setBolaoCompeticao(bolaoCompeticao);
				palpite.setJogo(jogo);
				palpite.setParticipante(p);
				palpite.setTipo(Palpite.RESULTADO);
				
				Map<Integer, TimeNoJogo> times = new HashMap<>();
				int i = 1;
				List<TimeNoJogo> timesNojogo = timesNoJogoRepo.findAllByJogoOrderById(jogo);
				for (TimeNoJogo timeNoJogo : timesNojogo) {
					times.put(i, timeNoJogo);
					i++;
				}
				
				palpite.setTimeA(times.get(1).getTime());
				palpite.setTimeB(times.get(2).getTime());
				palpite.setLimiteAposta(jogo.getLimiteAposta());
				palpiteRepo.save(palpite);
				novosPalpites.add(palpite);
			});
			
			/*
			 * bolaoCompeticaoList.forEach( bc -> {
			 * 
			 * Palpite pfinal = new Palpite(); pfinal.setBolaoCompeticao(bc);
			 * pfinal.setParticipante(p); pfinal.setTipo(Palpite.ACERTAR_TIMES);
			 * pfinal.setLimiteAposta(jogos.get(0).getLimiteAposta());
			 * palpiteRepo.save(pfinal); novosPalpites.add(pfinal);
			 * 
			 * System.out.println("Palpite " + Palpite.ACERTAR_TIMES + "Criado para " +
			 * bc.getCompeticao().getNome());
			 * 
			 * entityManager.flush();
			 * 
			 * Palpite campeao = new Palpite(); campeao.setBolaoCompeticao(bc);
			 * campeao.setParticipante(p); campeao.setTipo(Palpite.ACERTAR_CAMPEAO);
			 * campeao.setLimiteAposta(jogos.get(0).getLimiteAposta());
			 * palpiteRepo.save(campeao); novosPalpites.add(campeao);
			 * 
			 * System.out.println("Palpite " + Palpite.ACERTAR_CAMPEAO + "Criado para " +
			 * bc.getCompeticao().getNome());
			 * 
			 * entityManager.flush();
			 * 
			 * });
			 */
		
		}
		return palpiteRepo.findAllByParticipante(p);
	}


	private BolaoCompeticao getBolaoCompeticao(List<BolaoCompeticao> bolaoCompeticaoList, Jogo jogo) {
		BolaoCompeticao bolaoCompeticao = bolaoCompeticaoList
				.stream()
				.filter(bc -> { 
					return bc.getCompeticao().getId().equals(jogo.getFase().getCompeticao().getId());
		})
		.findFirst()
		.get() ;
		return bolaoCompeticao;
	}
}
