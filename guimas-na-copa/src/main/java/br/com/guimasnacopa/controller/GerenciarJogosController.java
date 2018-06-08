package br.com.guimasnacopa.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class GerenciarJogosController {

	@Autowired 
	JogoRepository jogoRepo;
	
	@Autowired
	Autenticacao autenticacao;
	
	@GetMapping("/jogos/gerenciar")
	public String gerenciar(Model m) throws LoginException {
		autenticacao.checkAdminAthorization();
		m.addAttribute("jogos",jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao()));
		m.addAttribute(autenticacao);
		return "/pages/gerenciar_jogos";
	}
	
	@PostMapping("/jogos/save")
	@Transactional
	public String salvar(HttpServletRequest reuqest, Model m) throws LoginException {
		autenticacao.checkAdminAthorization();
		
		
		System.out.println( reuqest.getParameterNames() );
		
		List<Jogo> jogos = jogoRepo.findAllByFase_BolaoOrderByFaseGrupoData(autenticacao.getBolao());
		jogos.forEach(j -> {
			String value = reuqest.getParameter(j.getId().toString());
			if (value != null && value != "") {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date date = null;
				try {
					date = format.parse(value.replace("T", " "));
				} catch (ParseException e) {
					e.printStackTrace();
				}		
				System.out.println(">>>>>>>>>>>>>>>>>" + value);
				jogoRepo.updateData(date, j.getId());
			}	
		});
		
		
		//jogoRepo.updateData(jogo.getData(), jogo.getId());
		return gerenciar(m);
	}
	
	
}
