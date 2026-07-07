package br.com.guimasnacopa.controller;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.ProcessarAviaoService;
import br.com.guimasnacopa.service.ProcessarAviaoService.DetalheProcessamentoAviao;
import br.com.guimasnacopa.service.ProcessarAviaoService.ResultadoProcessamentoAviao;

@Controller
@RequestScope
public class ProcessarAviaoController {

	@Autowired
	private Autenticacao autenticacao;

	@Autowired
	private AppMessages appMessages;

	@Autowired
	private ProcessarAviaoService processarAviaoService;

	@GetMapping("/aviao/listar")
	public String listar(Model model) throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();
		model.addAttribute("processamentos", processarAviaoService.listarProcessamentos(autenticacao.getBolao()));
		model.addAttribute("criterioSelecionado", processarAviaoService.obterUltimoCriterioSelecionado(autenticacao.getBolao()));
		model.addAttribute("criterioPrimeiroLugar", ProcessarAviaoService.CRITERIO_PRIMEIRO_LUGAR);
		model.addAttribute("criterioTerceiroLugar", ProcessarAviaoService.CRITERIO_TERCEIRO_LUGAR);
		return "/aviao/listar";
	}

	@PostMapping("/aviao/processar")
	public String processar(@RequestParam("criterio") Integer criterio, Model model)
			throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();

		ResultadoProcessamentoAviao resultado = processarAviaoService.processar(autenticacao.getBolao(), criterio);
		model.addAttribute("resultado", resultado);
		appMessages.getSuccessList().add("Processamento do aviao concluido.");
		return "/aviao/resultado";
	}

	@GetMapping("/aviao/{aviaoId}/resultado")
	public String consultar(@PathVariable("aviaoId") Integer aviaoId, Model model)
			throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();

		DetalheProcessamentoAviao detalhe = processarAviaoService.detalharProcessamento(aviaoId, autenticacao.getBolao());
		model.addAttribute("detalhe", detalhe);
		model.addAttribute("descricaoCriterio", processarAviaoService.getDescricaoCriterio(detalhe.getAviao().getCriterio()));
		return "/aviao/consulta";
	}

	@PostMapping("/aviao/{aviaoId}/remover")
	public String remover(@PathVariable("aviaoId") Integer aviaoId, Model model)
			throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();

		processarAviaoService.removerProcessamento(aviaoId, autenticacao.getBolao());
		appMessages.getSuccessList().add("Processamento removido com sucesso.");
		return "redirect:/aviao/listar";
	}
}