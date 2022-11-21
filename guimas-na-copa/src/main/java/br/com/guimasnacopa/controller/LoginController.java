package br.com.guimasnacopa.controller;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.UserRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class LoginController {

	@Autowired
	private Autenticacao autenticacao;
	
	@Autowired
	private UserRepository uRepo;
	
	@Autowired
	private BolaoRepository bolaoRepo;

	@Autowired
	AppMessages appMessages;
	
	@Value("${guimasnacopa.config.bolaoAtivo}")
	String bolaoAtivo;
	
		
	@PostMapping("/login")
	public String login( Usuario usuario, Model model) throws LoginException {
		autenticacao.setUsuario(uRepo.findOneByEmailSenha(usuario.getEmail(), usuario.getPass()));
		if ( autenticacao.getUsuario() != null) {
			autenticacao.setAutenticado(true);
			autenticacao.setBolao(bolaoRepo.findOneByPermalink(bolaoAtivo));
			
			model.addAttribute(autenticacao);
			
			if (autenticacao.getUsuario().getAdmin() != true)
				return "redirect:/" + bolaoAtivo; 
			else
				return "redirect:/bolao/listar";
		}
		else {
			throw new LoginException("Que é isso jogador? errou seus dados! Tente novamente...");
		}	
	}
	
	
	@GetMapping("/login")
	public String login() throws LoginException {
		return "pages/login";
	}
	
	@GetMapping("/logout")
	public String logout(Model model) {
		String msg = autenticacao.getUsuario().getName().split(" ")[0] + ", "
				+ "Foi muinto bom ver vc por aqui e esperamos vê-lo novamente em breve...";
		appMessages.getSuccessList().add(msg);
		model.addAttribute(appMessages);
		autenticacao.setUsuario(null);
		autenticacao.setAutenticado(false);
		return "pages/login";
	}
	
}
