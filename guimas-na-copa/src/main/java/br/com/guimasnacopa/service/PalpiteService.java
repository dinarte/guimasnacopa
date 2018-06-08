package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.TimeNoJogo;
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
	TimeNoJogoRepository timesNoJogoRepo;
	
	
	public List<Palpite> criarPalpites(Participante p) {
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(p.getBolao());
		List<Palpite> novosPalpites = new ArrayList<>();
		jogos.forEach(jogo -> {
			
			Palpite palpite = new Palpite();
			palpite.setJogo(jogo);
			palpite.setParticipante(p);
			palpite.setTipo(Palpite.RESULTADO);
			
			Map<Integer, TimeNoJogo> times = new HashMap<>();
			int i = 1;
			List<TimeNoJogo> timesNojogo = timesNoJogoRepo.findAllByJogo(jogo);
			for (TimeNoJogo timeNoJogo : timesNojogo) {
				times.put(i, timeNoJogo);
				i++;
			}
			
			palpite.setTimeA(times.get(1).getTime());
			palpite.setTimeB(times.get(2).getTime());
			
			palpiteRepo.save(palpite);
			novosPalpites.add(palpite);
		});
		
		Palpite pfinal = new Palpite();
		pfinal.setParticipante(p);
		pfinal.setTipo(Palpite.ACERTAR_TIMES);
		palpiteRepo.save(pfinal);
		novosPalpites.add(pfinal);
		
		Palpite campeao = new Palpite();
		campeao.setParticipante(p);
		campeao.setTipo(Palpite.ACERTAR_CAMPEAO);
		palpiteRepo.save(campeao);
		novosPalpites.add(campeao);
		
		return novosPalpites;

	}
}
