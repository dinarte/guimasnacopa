package br.com.guimasnacopa.controller;

import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.ApiService;

@Controller
@RequestScope
public class GerenciarBolaoController{

	@Autowired
	BolaoRepository bolaoRepo;
	
	@Autowired 
	Autenticacao autenticacao;
	
	@Autowired
	ApiService apiService;
	
	@Autowired
	AppMessages appMessages;
	
	@GetMapping("/bolao/listar")
	public String listar(Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		List<Bolao> bolaoList = (List<Bolao>) bolaoRepo.findAll();
		model.addAttribute("bolaoList", bolaoList);
		return "/bolao/listar";
	}
	
	@GetMapping("/bolao/{id}/editar")
	public String editar(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = bolaoRepo.findById(id).get();
		model.addAttribute(bolao);
		return "/bolao/form";
	}
	
	
	@GetMapping("/bolao/novo")
	public String novo(Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = new Bolao();
		model.addAttribute(bolao);
		return "/bolao/form";
	}
	
	@PostMapping("/bolao/salvar")
	public String salvar(Bolao bolao, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		bolaoRepo.save(bolao);
		model.addAttribute(bolao);
		appMessages.getSuccessList().add("Operação realizada com sucesso.");
		return listar(model);
	}
	
	
	@GetMapping("/bolao/{id}/remover")
	public String remover(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = bolaoRepo.findById(id).get();
		bolaoRepo.delete(bolao);
		model.addAttribute(bolao);
		appMessages.getSuccessList().add("Registro removido com sucesso.");
		return listar(model);
	}
	
	
	@GetMapping("/bolao/erro")
	public String erro(Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		return "/bolao/erro";
	}
	
	
	@GetMapping("/bolao/{id}/associar-competicoes")
	public String associarCompeticoes(Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = bolaoRepo.findById(id).get();
		apiService.gerarDadosDoBolao(bolao);
		model.addAttribute(bolao);
		appMessages.getSuccessList().add("Operação realizada com sucesso: O Bolão foi associado as competiçoes disponíveis");
		return listar(model);
	}
	
	
}
