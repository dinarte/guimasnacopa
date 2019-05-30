package br.com.guimasnacopa.controller;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.ParticipanteHelper;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class ParticipanteController {

	@Autowired
	ParticipanteRepository participanteRepo;
	
	@Autowired
	ParticipanteHelper participanteHelper;
	
	@Autowired
	Autenticacao autenticacao;
	
	
	
	@GetMapping("participante/{participante}/alterarpagamento")
	public String alterarPagamento(@PathVariable("participante") Participante p, Model model) throws AppException, LoginException {	
		
		autenticacao.checkAdminAthorization();
		
		p = participanteRepo.findById(p.getId()).get();
		if (p.getPg() == true)
			p.setPg(false);
		else
			p.setPg(true);
		participanteRepo.save(p);
		return participantes(p.getBolao().getPermalink(), model);
	}
	
	@GetMapping("/{linkBolao}/participantes")
	public String participantes( @PathVariable("linkBolao") String linkBolao, Model model) throws AppException, LoginException {
		autenticacao.checkAthorization();
		participanteHelper.prepareAllParticipantes(linkBolao, model);
		return "pages/participantes";
	}
	
	@GetMapping("/{linkBolao}/ranking")
	public String ranking( @PathVariable("linkBolao") String linkBolao, Model model) throws AppException, LoginException {
		autenticacao.checkAthorization();
		participanteHelper.prepareRankingParticipantes(linkBolao, model);
		return "pages/ranking";
	}
	
}
