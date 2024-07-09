package br.com.guimasnacopa.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.guimasnacopa.service.ParticipanteNoRankingProcessor;
import br.com.guimasnacopa.service.ProcessaFlatRankingService;

@RestController()
@RequestMapping("/ranking")
public class ProcessarRankingController {
	
	
	@Autowired
	ProcessaFlatRankingService rankingService;
	
	
	@GetMapping("/{bolaoId}/processar")
	public List<ParticipanteNoRankingProcessor> processar(@PathVariable("bolaoId") Integer bolaoId) {
		Instant start = Instant.now();
		List<ParticipanteNoRankingProcessor> processor = rankingService.processarByBolaoId(bolaoId);
		Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Tempo de execução " + duration.toMillis() + " milissegundos");
		return processor;
	} 
	
	
	@GetMapping("/{bolaoId}/processar/save")
	public List<ParticipanteNoRankingProcessor> processarESalvar(@PathVariable("bolaoId") Integer bolaoId) {
		Instant start = Instant.now();
		List<ParticipanteNoRankingProcessor> processor = rankingService.processarESalvarByBolaoId(bolaoId);
		Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Tempo de execução " + duration.toMillis() + " milissegundos");
		return processor;
	} 

}
