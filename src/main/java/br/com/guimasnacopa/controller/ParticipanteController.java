package br.com.guimasnacopa.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.ParticipanteHelper;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class ParticipanteController {

	@Autowired
	ParticipanteRepository participanteRepo;
	
	@Autowired 
	PalpiteRepository palpiteRepo;
	
	@Autowired
	ParticipanteHelper participanteHelper;
	
	@Autowired
	Autenticacao autenticacao;
	
	
	
	@GetMapping("participante/{participanteId}/alterarpagamento")
	public String alterarPagamento(@PathVariable("participanteId") Integer participanteId, Model model) throws AppException, LoginException {	
		
		autenticacao.checkAdminAthorization();
		
		Participante p = participanteRepo.findById(participanteId).get();
		if (p.getPg() == true)
			p.setPg(false);
		else
			p.setPg(true);
		participanteRepo.save(p);
		return participantes(p.getBolao().getPermalink(), model);
	}

	@GetMapping("participante/{participanteId}/alteraradminbolao")
	public String alterarAdminBolao(@PathVariable("participanteId") Integer participanteId, Model model) throws AppException, LoginException {
		autenticacao.checkAdminAthorization();

		Participante p = participanteRepo.findById(participanteId).get();

		if (p.getUsuario().getId().equals(autenticacao.getUsuario().getId())) {
			return participantes(p.getBolao().getPermalink(), model);
		}

		p.setAdmin(!p.getAdmin());
		participanteRepo.save(p);
		return participantes(p.getBolao().getPermalink(), model);
	}
	
	
	@GetMapping("/participantes")
	public String participantes( Model model) throws AppException, LoginException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();
		return participantes(autenticacao.getBolao().getPermalink(), model);
	}
	
	
	@GetMapping("/{linkBolao}/participantes")
	public String participantes( @PathVariable("linkBolao") String linkBolao, Model model) throws AppException, LoginException {
		participanteHelper.prepareAllParticipantes(linkBolao, model);
		return "pages/participantes";
	}
	
	@GetMapping("/{linkBolao}/ranking")
	public String ranking( @PathVariable("linkBolao") String linkBolao, Model model) throws AppException, LoginException {
		autenticacao.checkAthorization();
		participanteHelper.prepareRankingParticipantes(linkBolao, model);
		return "pages/ranking";
	}

	@GetMapping("/{linkBolao}/ranking-analitico")
	public String rankingAnalitico(@PathVariable("linkBolao") String linkBolao, Model model) throws AppException, LoginException {
		autenticacao.checkAthorization();
		participanteHelper.prepareRankingParticipantes(linkBolao, model);
		List<Participante> top10 = participanteRepo.findTop10ByBolaoAndPgOrderByClassificacaoAscExibirClassificacaoNoRankingDesc(
				autenticacao.getBolao(), true);
		model.addAttribute("top10Ids", top10.stream().map(Participante::getId).collect(Collectors.toList()));
		return "pages/ranking-analitico";
	}
	
	@DeleteMapping("/participantes")
	@Transactional
	public String remover(Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Participante participante = new Participante();
		participante.setId(id);
		palpiteRepo.deleteAllByParticipante_id(id);
		participanteRepo.deleteById(id);
		return "redirect:/participantes";
	}
	
}
