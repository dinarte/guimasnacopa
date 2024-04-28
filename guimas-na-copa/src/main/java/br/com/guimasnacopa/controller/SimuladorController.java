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
	
	@GetMapping("/simulador")
	public String gerenciar(Model m) throws LoginException {
		autenticacao.checkAthorization();
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		m.addAttribute("jogos",jogos);
		m.addAttribute(autenticacao);
		return "/pages/simulador";
	}
	
	@PostMapping("/simulador/simular")
	public String salvar(HttpServletRequest reuqest, Model m) throws LoginException {
		
		autenticacao.checkAthorization();
		
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		
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
					//timeNoJogoRepo.updateGols(Integer.parseInt(gols),tnj.getId());
				}
			});
			//processa o ranking
			Iterable<Palpite> palpites = palpiteRepo.findAllByJogo(j) ;
			palpites.forEach(palpite ->{
				System.out.println("..."+palpite.getGolsTimeA()+" x "+palpite.getGolsTimeB());
				if(palpite.getGolsTimeA() != null && palpite.getGolsTimeB() != null) {
					palpite.processarPontuacao();
					popularPontuacaoParticipanteMap(participantesPontiacaoMap, palpite);
					//palpiteRepo.updatePontuacao(palpite.getPontuacaoAtingida(), palpite.getRegraPontuacao(), palpite.getId());
				}
				if(palpite.getTipo().equals(Palpite.ACERTAR_TIMES)) {
					palpite.processarPontuacaoAcertarTimes();
					popularPontuacaoParticipanteMap(participantesPontiacaoMap, palpite);
					//palpiteRepo.updatePontuacao(palpite.getPontuacaoAtingida(), palpite.getRegraPontuacao(), palpite.getId());
				}
				
			});
		});
		
		
		List<Participante> participantes = participanteRepo
				.findAllByBolaoOrderByPontuacaoDesc(autenticacao.getBolao());
		
		int count = 1;
		int classificacaoAnterior = 0;
		Double pontuacaoAnterior = -0.1;
		for (Participante p : participantes) {
			System.out.println("particiantes>>>>");
			
			p.setPontuacao(participantesPontiacaoMap.get(p.getId()));
			
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
			//participanteRepo.save(p);
		}
		
		//participanteRepo.updateAproveitamento();
		m.addAttribute("jogos",jogos);
		m.addAttribute("participantes", participantes);
		m.addAttribute(autenticacao);
		return "/pages/simulador";
	}

	private void popularPontuacaoParticipanteMap(Map<Integer, Double> participantesPontiacaoMap, Palpite palpite) {
		if (Objects.nonNull(palpite.getPontuacaoAtingida())) {
			Double pontuacao = participantesPontiacaoMap.getOrDefault(palpite.getParticipante().getId(), 0.0); 
			pontuacao = Double.sum(pontuacao, palpite.getPontuacaoAtingida() != null ? palpite.getPontuacaoAtingida() : 0.0);
			participantesPontiacaoMap.put(palpite.getParticipante().getId(), pontuacao);
		}
	}
	
	
	
	
}
