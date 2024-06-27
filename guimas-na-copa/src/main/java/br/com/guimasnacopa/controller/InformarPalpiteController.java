package br.com.guimasnacopa.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.GroupNode;
import br.com.guimasnacopa.componentes.ObjectsGroup;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.TimeRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.PalpiteService;

@Controller
@RequestScope
public class InformarPalpiteController {

	@Autowired
	Autenticacao autenticacao;
	
	@Autowired
	PalpiteRepository palpiteRepo;
	
	@Autowired
	PalpiteService palpiteService;
	
	@Autowired
	TimeRepository timeRepositpry;
	
	@GetMapping("/palpite/editar")
	public String editarPalpite(Model model) throws LoginException{
		autenticacao.checkAthorization();
		
		palpiteService.criarPalpitesCasoNecessario(autenticacao.getParticipante());
		
		List<Palpite> palpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
	
		Map<Object, List<Object>> timesMap = agruparTimesEPegarMapa(palpites);
		
		
		
		List<GroupNode> palpitesEmAbertoNodes = filtarEAgruparPalpites(palpites, Palpite::isApostaAberta);
		List<GroupNode> palpitesFechadosNodes = filtarEAgruparPalpites(palpites, Palpite::isApostaFechada);
		 
		model.addAttribute("timesMap",timesMap);
		model.addAttribute(autenticacao);
		model.addAttribute("palpitesEmAbertoNodes", palpitesEmAbertoNodes);
		model.addAttribute("palpitesFechadosNodes", palpitesFechadosNodes);
		return "pages/palpite";
		
	}




	private Map<Object, List<Object>> agruparTimesEPegarMapa(List<Palpite> palpites) {
		List<GroupNode> timesGroup = ObjectsGroup 
				  .from(palpites
						  .stream()
						  .filter(Palpite::isResultado)
						  .collect(Collectors.toList())
						 ) 
				  .groupBy(( p -> ((Palpite) p).getBolaoCompeticao().getCompeticao().getId())) 
				  .groupBy(( p -> ((Palpite) p).getTimeA())) 
				  .getNodes();
		
		Map<Object, List<Object>> timesMap = timesGroup.stream().collect(Collectors.toMap(GroupNode::getNode, GroupNode::getChildrenAsLastLevel));
		return timesMap;
	}




	private List<GroupNode> filtarEAgruparPalpites(List<Palpite> palpites, Predicate<? super Palpite> predicate) {
		List<GroupNode> palpitesEmAbertoNodes = ObjectsGroup
				 .from(palpites.stream().filter(predicate).collect(Collectors.toList())) 
				 .groupBy(( p -> ((Palpite) p).getBolaoCompeticao().getCompeticao())) 
				 .groupBy(( p -> ((Palpite) p).getDescricaoGrupo()))
				 .getNodes();
		return palpitesEmAbertoNodes;
	}
	

	
	
	@PostMapping("/palpite/save")
	public String editarPalpite(HttpServletRequest request, Model model, AppMessages appMessagens) throws LoginException {
		List<Palpite> palpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
		palpites.forEach(p -> {
			String valuesA = request.getParameter(p.getKeyGolsTimeA());
			String valuesB = request.getParameter(p.getKeyGolsTimeB());
			
			if (p.isResultado()) {
				if (valuesA != null && valuesA != "") 
					p.setGolsTimeA(Integer.parseInt(valuesA));
				if (valuesB != null && valuesB != "") 
					p.setGolsTimeB(Integer.parseInt(valuesB));
			}else {
				
				if (valuesA != null && valuesA != "") {
					Time t = new Time();
					t.setId(Integer.parseInt(valuesA));
					p.setTimeA(t);
				}	
				if (valuesB != null && valuesB != "") {
					Time t = new Time();
					t.setId(Integer.parseInt(valuesB));
					p.setTimeB(t);
				} 
			}
				
			palpiteRepo.save(p);
		});
		appMessagens.getSuccessList().add("Seus palpites foram salvos com sucesso.");
		model.addAttribute("appMessagens",appMessagens);
		return editarPalpite(model);
	}
	

	
	
}
