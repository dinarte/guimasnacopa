package br.com.guimasnacopa.controller;

import java.time.LocalDateTime;
import java.util.List;

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
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class GerenciarDatasEResultadosController {

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
	
	@GetMapping("/jogos/gerenciar")
	public String gerenciar(Model m) throws LoginException {
		autenticacao.checkAdminAthorization();
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		m.addAttribute("jogos",jogos);
		m.addAttribute(autenticacao);
		return "/pages/gerenciar_jogos";
	}
	
	@PostMapping("/jogos/save")
	@Transactional
	public String salvar(HttpServletRequest reuqest, Model m) throws LoginException {
		autenticacao.checkAdminAthorization();
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		jogos.forEach(j -> {
			String value = reuqest.getParameter(j.getId().toString());
			
			//salva as datas
			if (value != null && !value.equals("")) {
				LocalDateTime dateTime = LocalDateTime.parse(value);
				jogoRepo.updateData(dateTime, j.getId());
			}	
			
			//salva os gols
			j.getTimesNoJogo().forEach(tnj ->{
				String gols = reuqest.getParameter("timeNoJogo_"+tnj.getId().toString());
				if (gols != null && !gols.equals(""))
					timeNoJogoRepo.updateGols(Integer.parseInt(gols),tnj.getId());
				
			});
			//processa o ranking
			Iterable<Palpite> palpites = palpiteRepo.findAllByJogo(j);
			palpites.forEach(palpite ->{
				if(palpite.getGolsTimeA() != null && palpite.getGolsTimeB() != null) {
					palpite.processarPontuacao();
					palpiteRepo.updatePontuacao(palpite.getPontuacaoAtingida(), palpite.getRegraPontuacao(), palpite.getId());;
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + palpite.getPontuacaoAtingida());
				}
			});
		});
		
		participanteRepo.updatePontuacao();
		
		return "redirect:/jogos/gerenciar";
	}
	
}
