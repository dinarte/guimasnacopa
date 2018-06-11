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
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
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
		model.addAttribute("falseList", faseRepo.findAll());
		model.addAttribute(jogo);
		return "/jogo/form";
	}
	
	
	@GetMapping("/jogo/novo")
	public String novo(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Jogo jogo = new Jogo();
		model.addAttribute(jogo);
		return "/jogo/form";
	}
	
	@PostMapping("/jogo/salvar")
	@Transactional
	public String salvar(Jogo jogo, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		jogoRepo.save(jogo);
		
		//atualiza o limite dos palpites
		List<Palpite> palpites = palpiteRepo.findAllByJogo(jogo);
		palpites.forEach(p -> {
			p.setLimiteAposta(jogo.getLimiteAposta());
			palpiteRepo.save(p);
		});
		
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
