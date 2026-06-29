package br.com.guimasnacopa.ia.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ia/api")
public class IaController {
	
	@Autowired
	IaRepository repo;

	@Autowired
	IaJogosPalpitesService jogosPalpitesService;
	
	@GetMapping("/boloes")
	public List<IaBolao> bolao(){

		return repo.listAllBolao();
	}
	
	@GetMapping("/ranking/{bolaoId}")
	public List<IaRanking> ranking(@PathVariable("bolaoId") Integer bolaoId){
		return repo.getRanking(bolaoId);
	}

	@GetMapping("/ranking/{bolaoId}/pontuacao-por-dia")
	public List<IaPontuacaoPorDia> pontuacaoPorDia(@PathVariable("bolaoId") Integer bolaoId){
		return repo.getPontuacaoPorDia(bolaoId);
	}

	@GetMapping("/boloes/{bolaoId}/jogos-com-resultados-e-palpites")
	public IaBolaoJogosPalpitesDto jogosComResultadosEPalpites(@PathVariable("bolaoId") Integer bolaoId){
		return jogosPalpitesService.getJogosComResultadosEPalpites(bolaoId);
	}

}
