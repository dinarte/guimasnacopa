

package br.com.guimasnacopa.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.PalpiteHelper;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.PalpiteResumoVo;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class ConsultarPalpiteController {

	@Autowired 
	PalpiteRepository palpiteRepo;
	
	@Autowired
	PalpiteHelper palpiteHelper;
	
	@Autowired
	Autenticacao autenticacao;
	
	@Autowired 
	AppMessages  appMessages;
	
	@Autowired
	HomeController home;
	
	
	@GetMapping("/palpite/{participante}/consultar")
	public String consultarPalpitesDoParticipante(@PathVariable("participante") Participante participante, Model model) throws LoginException{
		autenticacao.checkAthorization();
		palpiteHelper.processarConsultaDePalpite(participante, model, palpiteRepo.findAllByParticipante(participante));
		model.addAttribute("colunasCards",4);
		model.addAttribute("meuPalpite",false);
		return "pages/palpite_participante";
	}
	
	@GetMapping("/palpite/{participante}/consultar/tab")
	public String consultarPalpitesDoParticipanteEmTabela(@PathVariable("participante") Participante participante, Model model) throws LoginException{
		autenticacao.checkAthorization();
		palpiteHelper.processarConsultaDePalpite(participante, model, palpiteRepo.findAllByParticipante(participante));
		model.addAttribute("meuPalpite",false);
		return "pages/palpite_participante_tabela";
	}

	@GetMapping("/palpite/consultar")
	public String consultarMeusPalites(Model m) throws LoginException {
		autenticacao.checkAthorization();
		//seta o quadro com os prpoximos jogos
		palpiteHelper.processarConsultaDePalpiteRelacionandoApenasComResultadosDosJogos(autenticacao.getParticipante()
							, m
							, palpiteRepo.findAllByParticipante(autenticacao.getParticipante()));
		m.addAttribute("colunasCards",4);
		m.addAttribute("meuPalpite",true);
		return "pages/palpite_participante";
	
	}
	
	@GetMapping("/palpite/{palpite}/detalhar")
	public String detalharPalpite(@PathVariable("palpite") Palpite palpite, Model m) throws LoginException, br.com.guimasnacopa.exception.LoginException, AppException {
		autenticacao.checkAthorization();
		
		palpite = palpiteRepo.findById(palpite.getId()).get();
		
		if (palpite.isApostaAberta()){
			appMessages.getWarningList().add("Calma atacente, você está impedido!!! Este jogo ainda está com o período de palpites aberto e nesse período não é permitido acessas os detalhes dos palpites, volte aqui depois!");
			m.addAttribute(appMessages);
			return home.home(autenticacao.getBolao().getPermalink(), m);
		}
		
		List<Palpite> palpites = palpiteRepo.findAllByJogo(palpite.getJogo());
		List<Map<String,Object>> resumos = palpiteRepo.findResumoByJogo(palpite.getJogo().getId());
		List<Map<String,Object>> resultados = palpiteRepo.findResumoVencedorByJogo(palpite.getJogo().getId());
		String text = formatarResumoParaWhatsApp(palpites, resumos);		
		m.addAttribute("textWapp",text);
		palpite.popularGolsDoJogo();
		m.addAttribute("palpiteReferencia",palpite);
		m.addAttribute("resumos",resumos);
		m.addAttribute("resultados",resultados);
		m.addAttribute("palpites", palpites);
		m.addAttribute(autenticacao);
		return "pages/palpite_detalhe";
	}

	private String formatarResumoParaWhatsApp(List<Palpite> palpites, List<Map<String, Object>> resumos) {
		String text = "whatsapp://send?text="+
		"--------------------------------------------------\n"+
		palpites.get(0).getJogo().getFase().getBolao().getNome() + "\n" +
		"--------------------------------------------------\n"+
		"Resumo de: \n"+
		"*"+palpites.get(0).getJogo().getTimesDescricao()+"*\n"+
		"---------------------------------------------------\n"+
		"	*palpite*			*qtd*		\n";
		for(Map<String, Object> map : resumos) {
			text = text + "	  " + map.get("palpite") +"			"+map.get("qtd")  + "\n";
		}
		text = text + 
		"---------------------------------------------------\n";
		return text;
	}
}	