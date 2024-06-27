package br.com.guimasnacopa.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.guimasnacopa.domain.PalpiteForProcessingVo;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;


@Service
public class ProcessaFlatRankingService {
	
	
	@Autowired PalpiteRepository palpiteRepo;
	@Autowired ParticipanteRepository participanteRepo;
	
	
	@Transactional
	public List<ParticipanteNoRankingProcessor> processarByBolaoId(Integer bolaoId){
		List<PalpiteProcessor> palpiteProcessados = processarPalpites(bolaoId);
		List<ParticipanteNoRankingProcessor> participantesProcessados = processarParticipantes(palpiteProcessados);
		for (ParticipanteNoRankingProcessor participanteNoRankingProcessor : participantesProcessados) {
			System.out.println(participanteNoRankingProcessor.getName());
		}
		return processarClassificacao(participantesProcessados);		
	}

	private List<PalpiteProcessor>  processarPalpites(Integer bolaoId) {
		
		
		List<PalpiteForProcessingVo> palpitesForProcess = palpiteRepo.findAllForProssingByBolaoId(bolaoId);
		
		List<PalpiteProcessor> processors = palpitesForProcess
				.parallelStream()
				.map( palpiteToProccess -> {
						PalpiteProcessor processor = new ObjectMapper().convertValue(palpiteToProccess, PalpiteProcessor.class);
						if (processor.canProcess()) {
							processor.processarPontuacao();
							palpiteRepo.updatePontuacao(processor.getPontuacaoAtingida(), getDetahePonuacaoString(processor), processor.getId().intValue());
						}
						return processor;
				})
				.filter( PalpiteProcessor::canProcess )
				.collect(Collectors.toList());
	
		
		return processors;
	}

	private String getDetahePonuacaoString(PalpiteProcessor processor) {
		try {
			return new ObjectMapper().writeValueAsString(processor.getDetalhePontuacao());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{\"Problema ao detalhar pontuação\":0.0}";
		}
	}	
	
	private List<ParticipanteNoRankingProcessor> processarParticipantes(List<PalpiteProcessor> palpiteProcessados) {
		List<ParticipanteNoRankingProcessor> participantesProcessor =  
				palpiteProcessados.parallelStream().map(palpiteProcessor ->{
					
					ParticipanteNoRankingProcessor participante = new ParticipanteNoRankingProcessor();
					participante.setId(palpiteProcessor.getParticipanteId().intValue());
					participante.setName(palpiteProcessor.getName());
					participante.setEmail(palpiteProcessor.getEmail());
					participante.setUrlFoto(palpiteProcessor.getUrlFoto());
					return participante;
					
					
				})
				
				.distinct()
					
				.map( participante -> {
				  
				  List<IPalpiteBasico> palpitesDoPartcipante = palpiteProcessados
					  .parallelStream() 
					  .filter(p -> {return p.getParticipanteId().equals(participante.getParticipanteId());}) 
					  .map(p -> {return mapToPalpiteBasico(p);}) 
					  .collect(Collectors.toList());
					  
				  participante.setPalpitesProcessados(palpitesDoPartcipante); return
				  participante;
				  
				})
				
				.collect(Collectors.toList());
	
		return participantesProcessor;
	}

	private IPalpiteBasico mapToPalpiteBasico(PalpiteProcessor p) {
		return new IPalpiteBasico() {
			
			@Override
			public Double getPontuacaoAtingida() {
				return p.getPontuacaoAtingida();
			}
			
			@Override
			public Long getGolsTimeB() {
				return p.getGolsTimeB();
			}
			
			@Override
			public Long getGolsTimeA() {
				return p.getGolsTimeA();
			}
			
			@Override
			public Long getGolsJogoTimeB() {
				return p.getGolsJogoTimeB();
			}
			
			@Override
			public Long getGolsJogoTimeA() {
				return p.getGolsJogoTimeA();
			}
			
			@Override
			public Map<String, Double> getDetalhePontuacao() {
				return p.getDetalhePontuacao();
			}
			
			@Override
			public Double getAproveitamentoPalpite() {
				return p.getAproveitamentoPalpite();
			}
		};
	}
	
	private List<ParticipanteNoRankingProcessor> processarClassificacao(List<ParticipanteNoRankingProcessor> participantes) {
		
		participantes.sort( (p1, p2) -> {return p2.getPontuacao().compareTo(p1.getPontuacao());});
		ParticipanteNoRankingProcessor anterior = participantes.get(0);
		anterior.setClassificacao(1);
		anterior.setExibirClassificacaoNoRanking(true);
		participanteRepo.updateRaking(anterior.getId(), anterior.getPontuacao(), anterior.getAproveitamento(), anterior.getClassificacao(), anterior.getExibirClassificacaoNoRanking());
		for (int i = 1; i < participantes.size()-1; i++) {
			ParticipanteNoRankingProcessor atual = participantes.get(i);
			if (atual.getPontuacao().equals(anterior.getPontuacao())) {
				atual.setClassificacao(anterior.getClassificacao());
				atual.setExibirClassificacaoNoRanking(false);
			} else {
				atual.setClassificacao(i+1);
				atual.setExibirClassificacaoNoRanking(true);
			}
			
			anterior = atual;
			participanteRepo.updateRaking(atual.getId(), atual.getPontuacao(), atual.getAproveitamento(), atual.getClassificacao(), atual.getExibirClassificacaoNoRanking());
		}
		
		return participantes;
		
	}			
	
}
