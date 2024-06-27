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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.BolaoCompeticao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.BolaoCompeticaoRepository;
import br.com.guimasnacopa.repository.FaseRepository;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.repository.TimeRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.ProcessaRankingService;
import br.com.guimasnacopa.service.TimeNoJogoService;

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
	
	@Autowired 
	private ProcessaRankingService rankingService;
	
	@Autowired
	private TimeNoJogoService tmjService;
	
	@Autowired
	private BolaoCompeticaoRepository bolaoCompeticaoRepo;
	
	@GetMapping("/jogo/listar")
	public String listar(Model model) throws LoginException, BolaoNaoSelecionadoException{
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();
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
		model.addAttribute("faseList", faseRepo.findAllByBolaoOrderByCompeticao_nomeAscNomeAsc(autenticacao.getBolao()));
		model.addAttribute("timeList", timeRepo.findAll());
		model.addAttribute(jogo);
		return "/jogo/form";
	}
	
	
	@GetMapping("/jogo/novo")
	public String novo(Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		model.addAttribute("faseList", faseRepo.findAllByBolaoOrderByCompeticao_nomeAscNomeAsc(autenticacao.getBolao()));
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
		
		//caso esteja dando update atualiza o limite dos palpites
		if (!newState) {
		
			List<Palpite> palpites = palpiteRepo.findAllByJogoAndTipo(jogo,Palpite.RESULTADO);
			palpites.forEach(p -> {
				List<BolaoCompeticao>  bolaoCompeticao = bolaoCompeticaoRepo.findAllByBolaoAndCompeticao(jogo.getFase().getBolao(), jogo.getFase().getCompeticao());
				p.setLimiteAposta(jogo.getLimiteAposta());
				p.setBolaoCompeticao(bolaoCompeticao.get(0));
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
	public String remover(@PathVariable("id") Integer id, Model model) throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		Jogo jogo = jogoRepo.findById(id).get();
		jogoRepo.delete(jogo);
		model.addAttribute(jogo);
		appMessages.getSuccessList().add("Registro removido com sucesso.");
		return listar(model);
	}
	
	@PutMapping("/{bolao}/jogo/{id}/execucao")
	public String setEmAndamento(
			@PathVariable("bolao") String bolao, 
			@PathVariable("id") Integer id, 
			Integer idTimeA, 
			Integer golsTimeA,
			Integer idTimeB, 
			Integer golsTimeB, 
			String execucao) {
		
		Jogo jogo = jogoRepo.findById(id).get();
		jogo.setExecucao(execucao);
		jogoRepo.save(jogo);
		
		tmjService.updateGols(golsTimeA == null ? 0 : golsTimeA, idTimeA);
		tmjService.updateGols(golsTimeB == null ? 0 : golsTimeB, idTimeB);
		
		rankingService.processar(bolao);
		return "redirect:/"+bolao+"/";
	}
	

}
