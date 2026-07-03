package br.com.guimasnacopa.controller;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.MongoDbConnectionTestService;
import br.com.guimasnacopa.service.MongoDbConnectionTestService.MongoConnectionTestResult;

@Controller
@RequestScope
public class MongoDbConnectionTestController {

	@Autowired
	private Autenticacao autenticacao;

	@Autowired
	private AppMessages appMessages;

	@Autowired
	private MongoDbConnectionTestService mongoDbConnectionTestService;

	@GetMapping("/mongodb/testar-conexao")
	public String exibirTela(Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		model.addAttribute("uri", "");
		return "/mongodb/testar-conexao";
	}

	@PostMapping("/mongodb/testar-conexao")
	public String testarConexao(@RequestParam("uri") String uri, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);

		MongoConnectionTestResult resultadoTeste = mongoDbConnectionTestService.testarConexao(uri);
		model.addAttribute("resultadoTeste", resultadoTeste);
		model.addAttribute("uri", uri);

		if (resultadoTeste.isSucesso()) {
			appMessages.getSuccessList().add(resultadoTeste.getMensagem());
		} else {
			appMessages.getErrorList().add("Erro ao conectar no MongoDB.");
		}

		return "/mongodb/testar-conexao";
	}
}