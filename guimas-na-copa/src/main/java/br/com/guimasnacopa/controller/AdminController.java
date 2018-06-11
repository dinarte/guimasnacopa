package br.com.guimasnacopa.controller;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import br.com.guimasnacopa.security.Autenticacao;

/**
 * 
 * @author dinarte
 *
 */
public class AdminController {

	@Autowired
	Autenticacao autenticacao;
	
	@PostConstruct
	public void checkAdmin(Model model) throws LoginException {
		
	}
	
	
}
