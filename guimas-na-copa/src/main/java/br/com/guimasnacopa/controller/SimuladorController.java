package br.com.guimasnacopa.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.PalpiteProcessor;
import br.com.guimasnacopa.service.ParticipanteNoRankingProcessor;
import br.com.guimasnacopa.service.ProcessaFlatRankingService;

@Controller
@RequestScope
public class SimuladorController {

	@Autowired 
	JogoRepository jogoRepo;
	
	@Autowired
	TimeNoJogoRepository timeNoJogoRepo;
	
	@Autowired
	Autenticacao autenticacao;
	
	@Autowired
	PalpiteRepository palpiteRepo;
	
	@Autowired
	ParticipanteRepository participanteRepo;
	
	@Autowired
	ProcessaFlatRankingService rankingService;
	
	@GetMapping("/simulador")
	public String index(HttpServletRequest reuqest, Model m) throws LoginException {
		autenticacao.checkAthorization();
		List<Jogo> jogos = carregarJogos();
		processaPaginaSimulador(reuqest, m, jogos, false);
		return "/pages/simulador";
	}

	private List<Jogo> carregarJogos() {
		List<Jogo> jogos = jogoRepo.findAllByBolaoOrderByDataDescCompeticaoFaseGrupo(autenticacao.getBolao());
		//.filter(j -> j.getLiberarCriacaoPalpites() == true)
		return jogos.stream().collect(Collectors.toList());
	}
	
	@PostMapping("/simulador")
	public String simular(HttpServletRequest reuqest, Model m) throws LoginException {
		autenticacao.checkAthorization();
		List<Jogo> jogos = carregarJogos();
		processaPaginaSimulador(reuqest, m, jogos, true);
		return "/pages/simulador";
	}

	private void processaPaginaSimulador(HttpServletRequest reuqest, Model m, List<Jogo> jogos, boolean cashe) throws LoginException {
		
		List<PalpiteProcessor> processors = associaDadosDoFormulario(reuqest, jogos, cashe);
		
		List<ParticipanteNoRankingProcessor> participantes = rankingService.processarByBolaoId(processors,  autenticacao.getBolao().getId());
		
		
		m.addAttribute("jogos", jogos);
		m.addAttribute("participantes" ,participantes);
		m.addAttribute(autenticacao);
	}

	private List<PalpiteProcessor> associaDadosDoFormulario(HttpServletRequest reuqest, List<Jogo> jogos, boolean cache) {
		List<PalpiteProcessor> processors = null;
		if (cache)
			processors = rankingService.loadPalpitesProcessorsFromCache(autenticacao.getBolao().getId());
		else
			processors = rankingService.loadPalpitesProcessors(autenticacao.getBolao().getId());
		
  		Enumeration<String> parameters = reuqest.getParameterNames();
  		while (parameters.hasMoreElements()) {
			String paramName = (String) parameters.nextElement();
			
			String[] paramNameSplit =  paramName.split("-");
			String[] paramJogoSplit = paramNameSplit[0].split(":");
			String[] paramTimeSplit = paramNameSplit[1].split(":");
			String[] paramTimeNoJogoSplit = paramNameSplit[2].split(":");
			String jogoId = paramJogoSplit[1]; 
			String timeId = paramTimeSplit[1]; 
			String timeNoJogoId = paramTimeNoJogoSplit[1];
			String gols = reuqest.getParameter(paramName);
			
			
			processors
			.stream()
				.filter(p -> p.getJogoId() != null)
				.filter(p -> p.getTipo().equals(Palpite.RESULTADO))
				.filter(p -> p.getJogoId().toString().equals(jogoId))
				.filter(p -> p.getTimeAId().toString().equals(timeId))
				.forEach(p -> {
					if (gols != null && gols != "") {
						p.setGolsJogoTimeA(Long.parseLong(gols));
					}	
				});
			
			
			processors
				.stream()
				.filter(p -> p.getJogoId() != null)
				.filter(p -> p.getTipo().equals(Palpite.RESULTADO))
				.filter(p -> p.getJogoId().toString().equals(jogoId))
				.filter(p -> p.getTimeBId().toString().equals(timeId))
				.forEach(p -> {
					if (gols != null && gols != "") {
						p.setGolsJogoTimeB(Long.parseLong(gols));
					}	
				});
			
			
			jogos
				.stream()
				.filter(jogo -> jogo.getId().toString().equals(jogoId))
				.findFirst()
				.get()
				.getTimesNoJogo()
				.stream()
				.filter(tnj -> tnj.getId().toString().equals(timeNoJogoId))
				.findFirst()
				.get()
				.setGols(gols != "" ? Integer.parseInt(gols) : null);
				
			
		}
		
		
	
		
		return processors;
	}


	
	
	
}
