package br.com.guimasnacopa.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.ProcessaRankingService;

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
	ProcessaRankingService rankingService;
	
	@GetMapping("/simulador")
	public String gerenciar(Model m) throws LoginException {
		autenticacao.checkAthorization();
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		processaPaginaSimulador(m, jogos);
		return "/pages/simulador";
	}
	
	@PostMapping("/simulador")
	public String salvar(HttpServletRequest reuqest, Model m) throws LoginException {
		autenticacao.checkAthorization();
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		associaDadosDoFormulario(reuqest, jogos);
		processaPaginaSimulador(m, jogos);
		return "/pages/simulador";
	}

	private void processaPaginaSimulador(Model m, List<Jogo> jogos) throws LoginException {
		List<Participante> participantes = rankingService.processarNoPersist(autenticacao.getBolao());
		m.addAttribute("jogos", jogos);
		m.addAttribute("participantes" ,participantes);
		m.addAttribute(autenticacao);
	}

	private void associaDadosDoFormulario(HttpServletRequest reuqest, List<Jogo> jogos) {
		Map<Integer, Double> participantesPontiacaoMap = new HashMap<Integer, Double>();
		
		//comupta jogo a jogo
		jogos.forEach(j -> {
			System.out.println("jogo: "+j.getId()+"-"+j.getGrupo());
			String value = reuqest.getParameter(j.getId().toString());
			
			//salva os gols
			j.getTimesNoJogo().forEach(tnj ->{
				String gols = reuqest.getParameter("timeNoJogo_"+tnj.getId().toString());
				if (gols != null && !gols.equals("")) {
					System.out.println("jogo:" + tnj.getTime().getNome());
					tnj.setGols(Integer.parseInt(gols));
				}
			});
		});
	}

	private void popularPontuacaoParticipanteMap(Map<Integer, Double> participantesPontiacaoMap, Palpite palpite) {
		if (Objects.nonNull(palpite.getPontuacaoAtingida())) {
			Double pontuacao = participantesPontiacaoMap.getOrDefault(palpite.getParticipante().getId(), 0.0); 
			pontuacao = Double.sum(pontuacao, palpite.getPontuacaoAtingida() != null ? palpite.getPontuacaoAtingida() : 0.0);
			participantesPontiacaoMap.put(palpite.getParticipante().getId(), pontuacao);
		}
	}
	
	
	
	
}
