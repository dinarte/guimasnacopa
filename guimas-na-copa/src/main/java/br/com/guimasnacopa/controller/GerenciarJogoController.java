package br.com.guimasnacopa.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.com.guimasnacopa.service.ProcessaFlatRankingService;
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
	private ProcessaFlatRankingService rankingService;
	
	@Autowired
	private TimeNoJogoService tmjService;
	
	@Autowired
	private BolaoCompeticaoRepository bolaoCompeticaoRepo; 
	
	@GetMapping("/jogo/listar/filterBy/{filterBy}/orderBy/{orderBy}")
	public String listar(Model model, @PathVariable("filterBy") String filterBy, @PathVariable("orderBy") String orderBy) throws LoginException, BolaoNaoSelecionadoException{
		return doList(model, filterBy, orderBy);
	}
	
	@GetMapping("/jogo/listar")
	public String listar(Model model) throws LoginException, BolaoNaoSelecionadoException{
		return doList(model, "all", "competicao");
	}

	private String doList(Model model, String filterBy, String orderBy) throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();
		System.out.println(">>>>>>>");
		System.out.println(orderBy);
		
		List<Map<String, String>> ordenacoesList = new ArrayList<>();
		Map<String, String> competicao = new HashMap<>();
		Map<String, String> dataDesc = new HashMap<>();
		competicao.put("key", "competicao");
		competicao.put("value", "Competição, fase, grupo");
		dataDesc.put("key", "dataDesc");
		dataDesc.put("value", "Data do jogo decrescente");
		ordenacoesList.add(competicao);
		ordenacoesList.add(dataDesc);
		
		List<Jogo> jogoList = null;
		if (orderBy.equals("competicao")) {
			System.out.println("ordena por competicao");
			jogoList = (List<Jogo>) jogoRepo.findAllByBolaoOrderByCompeticaoGrupoData(autenticacao.getBolao());
		}	
		else if (orderBy.equals("dataDesc")) {
			System.out.println("ordena por data desc");
			jogoList = (List<Jogo>) jogoRepo.findAllByBolaoOrderByDataDescCompeticaoFaseGrupo(autenticacao.getBolao());
		}	
		model.addAttribute("jogoList", jogoList);
		model.addAttribute("ordenacoesList", ordenacoesList);
		model.addAttribute("orderBy", orderBy);
		model.addAttribute("filterBy", filterBy);
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
		
		
		if (!newState) {
			onUpdate(jogo);
		}else{
			onCreate(jogo);
		}
		
		//caso o jogo seja marcado como final, relaciona os palpites do tipo Acertar times e Acertar Campeao:
		if (jogo.getFase().getFaseFinal()) {
			List<Palpite> palpitesFinais = palpiteRepo.findAllByBolaoCompeticao_bolaoAndBolaoCompeticao_competicaoAndTipoIn(autenticacao.getBolao(), jogo.getFase().getCompeticao(), new String[]{Palpite.ACERTAR_CAMPEAO, Palpite.ACERTAR_TIMES});
			if (palpitesFinais != null && palpitesFinais.size() > 0) {
				palpitesFinais.forEach(palpite -> {
					palpite.setJogo(jogo);
					palpiteRepo.save(palpite);
				});
			}
		}	
		
		model.addAttribute(jogo);
		appMessages.getSuccessList().add("Operação realizada com sucesso.");
		model.addAttribute(appMessages);
		return "redirect:/jogo/listar";
	}

	private void onCreate(Jogo jogo) {
		//cria os times dentro do jogo	
		TimeNoJogo tmjA = new TimeNoJogo();
		tmjA.setJogo(jogo);
		tmjA.setTime(jogo.getTimeA());
		tmjRepo.save(tmjA);
		
		TimeNoJogo tmjB = new TimeNoJogo();
		tmjB.setJogo(jogo);
		tmjB.setTime(jogo.getTimeB());
		tmjRepo.save(tmjB);
	}

	private void onUpdate(Jogo jogo) {
		
		
		//atualiza os times no jogo
		List<TimeNoJogo> tnjList = tmjRepo.findAllByJogo(jogo);
		tnjList.sort(new Comparator<TimeNoJogo>() {
			@Override
			public int compare(TimeNoJogo tnj1, TimeNoJogo tnj2) {
				return tnj1.getId().compareTo(tnj2.getId());
			}
		});
		tnjList.get(0).setTime(jogo.getTimeA());
		tnjList.get(1).setTime(jogo.getTimeB());
		tmjRepo.saveAll(tnjList);
		
		
		//atualisa os palpites
		List<Palpite> palpites = palpiteRepo.findAllByJogoAndTipo(jogo,Palpite.RESULTADO);
		palpites.forEach(p -> {
			List<BolaoCompeticao>  bolaoCompeticao = bolaoCompeticaoRepo.findAllByBolaoAndCompeticao(jogo.getFase().getBolao(), jogo.getFase().getCompeticao());
			p.setLimiteAposta(jogo.getLimiteAposta());
			p.setBolaoCompeticao(bolaoCompeticao.get(0));
			p.setTimeA(jogo.getTimeA());
			p.setTimeB(jogo.getTimeB());
			palpiteRepo.save(p);
		});
		
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
	public String upExecucao(
			@PathVariable("bolao") String bolao, 
			@PathVariable("id") Integer id, 
			Integer idTimeA, 
			Integer golsTimeA,
			Integer idTimeB, 
			Integer golsTimeB, 
			String execucao,
			String vencedor) {
		
		Jogo jogo = jogoRepo.findById(id).get();
		jogo.setExecucao(execucao);
		jogoRepo.save(jogo);
		
		tmjService.updateGols(golsTimeA == null ? 0 : golsTimeA, vencedor.equals("timeA") ? true : false, idTimeA);
		tmjService.updateGols(golsTimeB == null ? 0 : golsTimeB, vencedor.equals("timeB") ? true : false, idTimeB);
		
		//rankingService.processar(bolao);
		rankingService.processarESalvarByBolaoId(autenticacao.getBolao().getId());
		return "redirect:/"+bolao+"/";
	}
	

}
