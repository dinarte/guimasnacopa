package br.com.guimasnacopa.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

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
	public String editarPalpite(Model model){
		List<Palpite> palpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
		if (palpites == null || palpites.size() == 0)
			palpites = palpiteService.criarPalpites(autenticacao.getParticipante());
		model.addAttribute("times", timeRepositpry.findAllByBolao(autenticacao.getBolao()));
		model.addAttribute(autenticacao);
		model.addAttribute("palpites",palpites);
		return "pages/palpite";
		
	}
	
	@PostMapping("/palpite/save")
	public String editarPalpite(HttpServletRequest request, Model model, AppMessages appMessagens) {
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
