package br.com.guimasnacopa.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.guimasnacopa.componentes.GroupNode;
import br.com.guimasnacopa.componentes.ObjectsGroup;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;

@Transactional
@Service
public class ProcessaRankingService {
	
	@Autowired JogoRepository jogoRepo;
	@Autowired PalpiteRepository palpiteRepo;
	@Autowired ParticipanteRepository participanteRepo;
	@Autowired BolaoRepository bolaoRespo;
	
	private final static boolean PERSIST = true;
	private final static boolean NO_PERSIST = false;
	
	
	
	public List<Participante> processar(String bolaoPermaLink) { 
		Bolao bolao = bolaoRespo.findOneByPermalink(bolaoPermaLink);
		List<Participante>  participantes = processar(bolao);
		return participantes;
	}
	
	public List<Participante> processarNoPersist(String bolaoPermaLink) { 
		Bolao bolao = bolaoRespo.findOneByPermalink(bolaoPermaLink);
		List<Participante>  participantes = processarNoPersist(bolao);
		return participantes;
	}
	
	public List<Participante>   processarNoPersist(Bolao bolao) {
		processarPontuacaoPalpite(bolao, NO_PERSIST);
		List<Participante>  participantes = processarPontuacaoParticipante(bolao, NO_PERSIST);
		return participantes;
	}
	
	
	public List<Participante> processar(Bolao bolao) {
		processarPontuacaoPalpite(bolao, PERSIST);
		List<Participante>  participantes = processarPontuacaoParticipante(bolao, PERSIST);
		return participantes;
	}


	
	private void processarPontuacaoPalpite(Bolao bolao, boolean persist) {
		
		List<Jogo> jogos = jogoRepo.findAllByBolaoOrderByCompeticaoGrupoData(bolao);
		
		jogos.forEach(j -> {
			//processa o ranking
			Iterable<Palpite> palpites = palpiteRepo.findAllByJogo(j) ;
			palpites.forEach(palpite ->{
				System.out.println("..."+palpite.getGolsTimeA()+" x "+palpite.getGolsTimeB());
				if(palpite.getGolsTimeA() != null && palpite.getGolsTimeB() != null) {
					palpite.popularGolsDoJogo();
					palpite.setPontuacaoAtingida(null);
					palpite.processarPontuacaoAcumulativa();
					if (persist)
						palpiteRepo.updatePontuacao(palpite.getPontuacaoAtingida(), palpite.getRegraPontuacao(), palpite.getId());
				}
				if(palpite.getTipo().equals(Palpite.ACERTAR_TIMES)) {
					palpite.processarPontuacaoAcertarTimes();
					if (persist)
						palpiteRepo.updatePontuacao(palpite.getPontuacaoAtingida(), palpite.getRegraPontuacao(), palpite.getId());
				}
			});
		
		});
		
		
	}
	

	private List<Participante> processarPontuacaoParticipante(Bolao bolao, boolean persist) {
		
		boolean apenasParticipantesPagos = true;
		List<Participante> participantes = participanteRepo
				.findAllByBolaoAndPgOrderByPontuacaoDesc(bolao, apenasParticipantesPagos);
		
		participantes.forEach(participante -> {
			
			
			List<Palpite> palpites = palpiteRepo.findAllByParticipante(participante);
			List<Palpite> palpitesProcessados = palpites.stream().filter(Palpite::isProcessado).collect(Collectors.toList());
			
			
			List<GroupNode> jogosGroup = ObjectsGroup 
					  .from(palpitesProcessados
							  .stream()
							  .filter(Palpite::isResultado)
							  .collect(Collectors.toList())
							 ) 
					  .groupBy(( p -> ((Palpite) p).getJogo().getId())) 
					  .getNodes();
			
			Integer totalJogosParocessados = jogosGroup.size();
			Double totalPontuacaoParticipante = palpitesProcessados.stream().collect(Collectors.summingDouble(Palpite::getPontuacaoAtingida));
			Double aproveitamentoParticipante = palpitesProcessados.stream().collect(Collectors.summingDouble(Palpite::getAproveitamento)) / totalJogosParocessados.intValue();
			participante.setPontuacao(totalPontuacaoParticipante);
			participante.setAproveitamento(aproveitamentoParticipante.intValue());
			
			if (persist)
				participanteRepo.save(participante);
			
		});
		
		processarExibicaoNoRanking(participantes, persist);
		
		return participantes;
	}
	
	
	private void processarExibicaoNoRanking(List<Participante> participantes, boolean persist) {
		
		participantes.sort( (p1, p2) -> {return p2.getPontuacao().compareTo(p1.getPontuacao());});
		
		int count = 1;
		int classificacaoAnterior = 0;
		Double pontuacaoAnterior = -0.1;
		for (Participante p : participantes) {

			p.setExibirClassificacaoNoRanking(true);
			if (!p.getPontuacao().equals(pontuacaoAnterior)) {
				classificacaoAnterior = count;
				p.setClassificacao(classificacaoAnterior);
			}else{
				p.setClassificacao(classificacaoAnterior);
				p.setExibirClassificacaoNoRanking(false);
			}
			count++;
			pontuacaoAnterior = p.getPontuacao();
			
			if (persist)
				participanteRepo.save(p);
		}
	}
	
	
	
	
}
