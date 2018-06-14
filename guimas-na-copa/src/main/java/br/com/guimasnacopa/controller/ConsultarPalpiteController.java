package br.com.guimasnacopa.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.PalpiteHelper;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.TimeRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.PalpiteService;

@Controller
@RequestScope
public class ConsultarPalpiteController {

	
	
	
	@Autowired 
	PalpiteRepository palpiteRepo;
	
	@Autowired
	PalpiteHelper palpiteHelper;
	
	@Autowired
	Autenticacao autenticacao;
	
	
	
	@GetMapping("/palpite/{participante}/consultar")
	public String consultarPalpitesDoParticipante(@PathVariable("participante") Participante participante, Model model){
		palpiteHelper.processarConsultaDePalpite(participante, model, palpiteRepo.findAllByParticipante(participante));
		model.addAttribute("colunasCards",3);
		model.addAttribute("meuPalpite",false);
		return "pages/palpite_participante";
	}
	
	@GetMapping("/palpite/{participante}/consultar/tab")
	public String consultarPalpitesDoParticipanteEmTabela(@PathVariable("participante") Participante participante, Model model){
		palpiteHelper.processarConsultaDePalpite(participante, model, palpiteRepo.findAllByParticipante(participante));
		model.addAttribute("colunasCards",3);
		model.addAttribute("meuPalpite",false);
		return "pages/palpite_participante_tabela";
	}

	@GetMapping("/palpite/consultar")
	public String consultarMeusPalites(Model m) {
		//seta o quadro com os prpoximos jogos
		palpiteHelper.processarConsultaDePalpiteRelacionandoApenasComResultadosDosJogos(autenticacao.getParticipante()
							, m
							, palpiteRepo.findAllByParticipante(autenticacao.getParticipante()));
		m.addAttribute("colunasCards",4);
		m.addAttribute("meuPalpite",true);
		return "pages/palpite_participante";
	
	}
}	