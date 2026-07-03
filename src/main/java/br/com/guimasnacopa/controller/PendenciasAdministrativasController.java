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
import br.com.guimasnacopa.service.PendenciasAdministrativasService;
import br.com.guimasnacopa.service.ProcessaFlatRankingService;

@Controller
@RequestScope
public class PendenciasAdministrativasController {

	@Autowired
	private Autenticacao autenticacao;

	@Autowired
	private AppMessages appMessages;

	@Autowired
	private PendenciasAdministrativasService pendenciasAdministrativasService;

	@Autowired
	private ProcessaFlatRankingService processaFlatRankingService;

	@GetMapping("/admin/pendencias")
	public String listarPendencias(Model model) throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();

		model.addAttribute("pendencias", pendenciasAdministrativasService.getPendencias(autenticacao.getBolao()));
		return "/admin/pendencias";
	}

	@PostMapping("/admin/pendencias/jogo/{jogoId}/encerrar")
	public String encerrarJogo(@PathVariable("jogoId") Integer jogoId, Model model)
			throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();

		pendenciasAdministrativasService.encerrarJogo(jogoId, autenticacao.getBolao());
		processaFlatRankingService.processarESalvarByBolaoId(autenticacao.getBolao().getId());
		appMessages.getSuccessList().add("Jogo encerrado com sucesso.");
		return "redirect:/admin/pendencias";
	}

	@PostMapping("/admin/pendencias/jogo/{jogoId}/vencedor")
	public String definirVencedor(
			@PathVariable("jogoId") Integer jogoId,
			@RequestParam("timeNoJogoId") Integer timeNoJogoId,
			Model model) throws LoginException, BolaoNaoSelecionadoException {
		autenticacao.checkAdminAthorization(model);
		autenticacao.checkBolaoNaoSelecionado();

		pendenciasAdministrativasService.definirVencedor(jogoId, timeNoJogoId, autenticacao.getBolao());
		processaFlatRankingService.processarESalvarByBolaoId(autenticacao.getBolao().getId());
		appMessages.getSuccessList().add("Vencedor do mata-mata definido com sucesso.");
		return "redirect:/admin/pendencias";
	}
}