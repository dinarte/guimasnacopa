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

import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.CompeticaoRepository;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class GerenciarFaseController {
	
	@Autowired 
	Autenticacao autenticacao;
	
	@Autowired
	AppMessages appMessages;
	
	@Autowired
	FaseRepository faseRepo;
	
	@Autowired
	CompeticaoRepository competicaoRepo;
	
	
	@GetMapping("/fase/listar")
	public String listar(Model model) throws LoginException, BolaoNaoSelecionadoException{
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();
		List<Fase> faseList = (List<Fase>) faseRepo.findAllByBolao(autenticacao.getBolao());
		model.addAttribute("faseList", faseList);
		return "/fase/listar";
	}
	
	@GetMapping("/fase/{id}/editar")
	public String editar(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Fase fase = faseRepo.findById(id).get();
		model.addAttribute("falseList", faseRepo.findAll());
		model.addAttribute("competicaoList", competicaoRepo.findAll());
		model.addAttribute(fase);
		return "/fase/form";
	}
	
	
	@GetMapping("/fase/novo")
	public String novo(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Fase fase = new Fase();
		model.addAttribute(fase);
		return "/fase/form";
	}
	
	@PostMapping("/fase/salvar")
	@Transactional
	public String salvar(Fase fase, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		fase.setBolao(autenticacao.getBolao());
		faseRepo.save(fase);
		model.addAttribute(fase);
		appMessages.getSuccessList().add("Operação realizada com sucesso.");
		model.addAttribute(appMessages);
		return "redirect:/fase/listar";
	}
	
	
	@GetMapping("/fase/{id}/remover")
	@Transactional
	public String remover(@PathVariable("id") Integer id, Model model) throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		Fase fase = faseRepo.findById(id).get();
		faseRepo.delete(fase);
		model.addAttribute(fase);
		appMessages.getSuccessList().add("Registro removido com sucesso.");
		return listar(model);
	}
}
