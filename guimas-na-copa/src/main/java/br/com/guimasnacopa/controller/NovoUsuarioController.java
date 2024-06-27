package br.com.guimasnacopa.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.exception.ValidacaoException;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.UserRepository;
import br.com.guimasnacopa.service.SingUpService;

@Controller
@RequestScope
public class NovoUsuarioController {
	
	@Autowired
	SingUpService singUpService;
	@Autowired
	UserRepository userRepo;
	@Autowired
	AppMessages appMessages;
	@Autowired
	LoginController loginComtroller;
	
	@GetMapping("singup")
	public String singUp() {	
		return "pages/singup";
	}
	
	@Value("${guimasnacopa.config.bolaoAtivo}")
	String bolaoAtivo;
	
	@PostMapping("singup")
	public String singUp(Usuario usuario, Model model) throws ValidacaoException  {	
		List<String> erros = new ArrayList<>();
		if (usuario.getName() == null || usuario.getName().equals(""))
			erros.add("Informe o seu nome por favor.");
		if (usuario.getEmail() == null || usuario.getEmail().equals(""))
			erros.add("Informe seu email por favor.");
		if (usuario.getPass() == null || usuario.getPass().equals(""))
			erros.add("Informe uma senha por favor.");
		if (usuario.getPassConfirm() == null || usuario.getPassConfirm().equals(""))
			erros.add("A confirmação da senha não é igual a senha informada.");
		
		if (usuario.getPass() != null && usuario.getPassConfirm() != null) {
			if (!usuario.getPass().equals(usuario.getPassConfirm())) {
				erros.add("A confirmação da senha não é igual a senha informada.");
			}
			if (usuario.getPass().length() < 6) {
				erros.add("A senha deverá possuir no mínimo 6 caracteres.");
			}
		}
		
		if (erros.size() > 0)
			throw new ValidacaoException(erros);
		
		if (userRepo.findByEmail(usuario.getEmail()) != null) {
			erros.add("Já existe um usuário com o email informado.");
			throw new ValidacaoException(erros);
		}
		
		singUpService.criarUsuarioEParticipante(usuario, bolaoAtivo);
		
		appMessages.getWarningList().add("Você se cadastrou com sucesso! Efetue login.");
		model.addAttribute(appMessages);
		return loginComtroller.login(model);
	}
	
	

}
