package br.com.guimasnacopa.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDTO;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Competicao;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.CompeticaoRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.ApiService;
import br.com.guimasnacopa.service.BolaoService;
import br.com.guimasnacopa.service.CarregarCampeonatosDaApiService;

@Controller
@RequestScope
public class GerenciarBolaoController{

	private static final String ATUALIZACAO_API_PREVIEW_PREFIX = "ATUALIZACAO_API_PREVIEW_";

	@Autowired
	BolaoRepository bolaoRepo;
	
	@Autowired 
	BolaoService bolaoService;
	
	@Autowired 
	Autenticacao autenticacao;
	
	@Autowired
	ApiService apiService;
	
	@Autowired
	CarregarCampeonatosDaApiService carregarCampeonatosDaApiService;
	
	@Autowired
	CompeticaoRepository competicaoRepository;
	
	@Autowired
	AppMessages appMessages;
	
	@GetMapping("/bolao/listar")
	public String listar(Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		List<Bolao> bolaoList = (List<Bolao>) bolaoRepo.findAll();
		model.addAttribute("bolaoList", bolaoList);
		return "/bolao/listar";
	}
	
	@GetMapping("/bolao/{id}/editar")
	public String editar(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = bolaoRepo.findById(id).get();
		model.addAttribute(bolao);
		return "/bolao/form";
	}
	
	
	@GetMapping("/bolao/novo")
	public String novo(Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = new Bolao();
		model.addAttribute(bolao);
		model.addAttribute("competicaoList", competicaoRepository.findByStatusNot(Competicao.STATUS_FINALIZADO));
		return "/bolao/form";
	}
	
	@PostMapping("/bolao/salvar")
	public String salvar(Bolao bolao, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		bolaoService.criarBolao(bolao);
		model.addAttribute(bolao);
		appMessages.getSuccessList().add("Operação realizada com sucesso.");
		return listar(model);
	}
	
	
	@GetMapping("/bolao/{id}/remover")
	public String remover(@PathVariable("id") Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = bolaoRepo.findById(id).get();
		bolaoRepo.delete(bolao);
		model.addAttribute(bolao);
		appMessages.getSuccessList().add("Registro removido com sucesso.");
		return listar(model);
	}

	@GetMapping("/bolao/{id}/atualizar-jogos-api")
	public String atualizarJogosDaApi(@PathVariable("id") Integer id, Model model, HttpSession session) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		BolaoService.AtualizacaoApiPreview preview = bolaoService.gerarPreviewAtualizacaoApi(id);
		String token = UUID.randomUUID().toString();
		session.setAttribute(ATUALIZACAO_API_PREVIEW_PREFIX + token, preview);

		model.addAttribute("preview", preview);
		model.addAttribute("token", token);
		return "/bolao/conferencia-atualizacao-api";
	}

	@PostMapping("/bolao/{id}/atualizar-jogos-api/confirmar")
	public String confirmarAtualizacaoJogosDaApi(@PathVariable("id") Integer id,
			@RequestParam("token") String token,
			Model model,
			HttpSession session) throws LoginException {
		autenticacao.checkAdminAthorization(model);

		Object previewObj = session.getAttribute(ATUALIZACAO_API_PREVIEW_PREFIX + token);
		if (!(previewObj instanceof BolaoService.AtualizacaoApiPreview)) {
			appMessages.getErrorList().add("Não foi possível confirmar a atualização. Gere uma nova conferência.");
			return listar(model);
		}

		BolaoService.AtualizacaoApiPreview preview = (BolaoService.AtualizacaoApiPreview) previewObj;
		if (!id.equals(preview.getBolaoId())) {
			appMessages.getErrorList().add("Os dados da conferência não correspondem ao bolão selecionado.");
			return listar(model);
		}

		bolaoService.persistirAtualizacaoApi(preview);
		session.removeAttribute(ATUALIZACAO_API_PREVIEW_PREFIX + token);
		appMessages.getSuccessList().add("Fases e jogos foram atualizados com sucesso.");
		return listar(model);
	}
	
	
	@GetMapping("/bolao/erro")
	public String erro(Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		return "/bolao/erro";
	}
	
	@GetMapping("/competicao/listar-api")
	public String habilitarCompeticaoListar(Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		List<CampeonatoDTO> campeonatoList = carregarCampeonatosDaApiService.listAllByApi();
		model.addAttribute("campeonatoList", campeonatoList);
		return "/bolao/listar-campeonatos-api";
	}
	
	
	@GetMapping("/competicao/habilitar-api/{campeonatoId}")
	public String habilitarCompeticao(@PathVariable("campeonatoId") Long campeonatoId, Model model) throws LoginException{
		autenticacao.checkAdminAthorization(model);
		carregarCampeonatosDaApiService.carregarCompeticao(campeonatoId);
		appMessages.getSuccessList().add("A competição foi disponibilizada com sucesso: Agora os usuários já podem criar bolões para ");
		return listar(model);
	}
	
	@Deprecated
	@GetMapping("/bolao/{id}/associar-competicoes")
	public String associarCompeticoes(Integer id, Model model) throws LoginException {
		autenticacao.checkAdminAthorization(model);
		Bolao bolao = bolaoRepo.findById(id).get();
		apiService.gerarDadosDoBolao(bolao);
		model.addAttribute(bolao);
		appMessages.getSuccessList().add("Operação realizada com sucesso: O Bolão foi associado as competiçoes disponíveis");
		return listar(model);
	}
	
	
}
