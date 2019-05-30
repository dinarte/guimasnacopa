package br.com.guimasnacopa.controller;

import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.repository.TimeRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class GerenciarJogoController {
	
	@Autowired
	JogoRepository jogoRepo;
	
	@Autowired 
	Autenticacao autenticacao;
	
	@Autowired
	AppMessages appMessages;
	
	@Autowired
	FaseRepository faseRepo;
	
	@Autowired
	private PalpiteRepository palpiteRepo;
	
	@Autowired
	private TimeRepository timeRepo;
	
	@Autowired 
	private TimeNoJogoRepository tmjRepo;
	
	@GetMapping("/jogo/listar")
	public String listar(Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		List<Jogo> jogoList = (List<Jogo>) jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		model.addAttribute("jogoList", jogoList);
		return "/jogo/listar";
	}
	
	@GetMapping("/jogo/{id}/editar")
	public String editar(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Jogo jogo = jogoRepo.findById(id).get();
		jogo.setTimeA(jogo.getTimesNoJogo().get(0).getTime());
		jogo.setTimeB(jogo.getTimesNoJogo().get(1).getTime());
		model.addAttribute("faseList", faseRepo.findAll());
		model.addAttribute("timeList", timeRepo.findAll());
		model.addAttribute(jogo);
		return "/jogo/form";
	}
	
	
	@GetMapping("/jogo/novo")
	public String novo(Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		model.addAttribute("faseList", faseRepo.findAll());
		model.addAttribute("timeList", timeRepo.findAll());
		Jogo jogo = new Jogo();
		model.addAttribute(jogo);
		return "/jogo/form";
	}
	
	@PostMapping("/jogo/salvar")
	@Transactional
	public String salvar(Jogo jogo, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		boolean newState = jogo.getId() == null ? true : false;
		jogoRepo.save(jogo);
		
		//caso esteja atualizando atualiza o limite dos palpites
		if (!newState) {
		
			List<Palpite> palpites = palpiteRepo.findAllByJogoAndTipo(jogo,Palpite.RESULTADO);
			palpites.forEach(p -> {
				p.setLimiteAposta(jogo.getLimiteAposta());
				palpiteRepo.save(p);
			});
			
		}
		//caso esteja criando cria os times dentro do jogo	
		else{
			TimeNoJogo tmjA = new TimeNoJogo();
			tmjA.setJogo(jogo);
			tmjA.setTime(jogo.getTimeA());
			tmjRepo.save(tmjA);
			
			TimeNoJogo tmjB = new TimeNoJogo();
			tmjB.setJogo(jogo);
			tmjB.setTime(jogo.getTimeB());
			tmjRepo.save(tmjB);
		}
			
		
		model.addAttribute(jogo);
		appMessages.getSuccessList().add("Operação realizada com sucesso.");
		model.addAttribute(appMessages);
		return "redirect:/jogo/listar";
	}
	
	
	@GetMapping("/jogo/{id}/remover")
	public String remover(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Jogo jogo = jogoRepo.findById(id).get();
		jogoRepo.delete(jogo);
		model.addAttribute(jogo);
		appMessages.getSuccessList().add("Registro removido com sucesso.");
		return listar(model);
	}
}
