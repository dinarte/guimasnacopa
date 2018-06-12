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
	Autenticacao autenticacao;
	
	@Autowired
	PalpiteRepository palpiteRepo;
	
	
	
	@Autowired 
	ParticipanteRepository participanteRepo;
	
	
	
	@GetMapping("/palpite/{participante}/consultar")
	public String consultarPalpitesDoParticipante(@PathVariable("participante") Participante participante, Model model){
		List<Palpite> palpites = palpiteRepo.findAllByParticipante(participante);
		List<Palpite> meusPalpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
		palpites.forEach(p ->{
			meusPalpites.forEach(meuPalpite ->{
				if (meuPalpite.getJogo() != null && p.getJogo() != null) {
					if (meuPalpite.getJogo().getId().equals(p.getJogo().getId())) {
						p.setPalpiteComparado(meuPalpite);
					}
				} else if ((meuPalpite.isAcertarTimes() && p.isAcertarTimes())
						|| (meuPalpite.isAcertarCampeao() && p.isAcertarCampeao()) ) {
					p.setPalpiteComparado(meuPalpite);
				}
				
			});
		});
		
		model.addAttribute(autenticacao);
		model.addAttribute("participanteConsultado",palpites.get(0).getParticipante());
		model.addAttribute("palpites",palpites);
		return "pages/palpite_participante";
	}
	
	
	
	
}
