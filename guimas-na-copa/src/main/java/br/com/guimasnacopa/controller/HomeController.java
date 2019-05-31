package br.com.guimasnacopa.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.BolaoHelper;
import br.com.guimasnacopa.componentes.PalpiteHelper;
import br.com.guimasnacopa.componentes.ParticipanteHelper;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.exception.LoginException;
import br.com.guimasnacopa.repository.JogoRepository;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.UserRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.StartUpService;

@Controller
@RequestScope
public class HomeController {
	
	@Autowired 
	private Autenticacao autenticacao;
	
	@Autowired
	private StartUpService startUp;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	ParticipanteRepository participanteRepo;

	@Autowired 
	private BolaoHelper bolaoHelper;
	
	@Autowired
	private ParticipanteHelper participanteHelper;
	
	@Autowired
	private PalpiteHelper palpiteHelper;
	
	@Autowired PalpiteRepository palpiteRepo;
	
	@Autowired JogoRepository jogoRepo;
	
	@RequestMapping("/")
	public String home(Model m) throws AppException {
		criarUsuarioAdminCasoNecessario();
		return redirecionaDeAcordoComAutenticacao(m);
		
	}
	
	@RequestMapping("/{linkBolao}")
	public String home(@PathVariable("linkBolao") String linkBolao, Model m) throws AppException, LoginException {
		if (autenticacao.isAutenticado()) {
			Bolao bolao = bolaoHelper.getBolaoByPermaLink(linkBolao);
			autenticacao.setBolao(bolao);
			populaHomoDoParticipante(m, bolao);
			criarUsuarioAdminCasoNecessario();
			return redirecionaDeAcordoComAutenticacao(m,linkBolao);
		}else {
			return redirecionaDeAcordoComAutenticacao(m);
		}
			
	}
	
	

	private void populaHomoDoParticipante(Model m, Bolao bolao) {
		if (! autenticacao.getUsuario().getAdmin()) {
			//seta o card de participante
			Participante participante = participanteRepo.findOneByBolaoAndUsuario(bolao, autenticacao.getUsuario());
			
			participante = criaParticipanteCasoNecessario(participante);
			
			participanteHelper.popularPorcentagemDePreenchimentoDoParticipante(participante);
			autenticacao.setParticipante(participante);
			m.addAttribute(autenticacao);
			m.addAttribute("qtdParticipantes",participanteRepo.countByBolao(bolao));
			
			//seta o card de total de participantes
			Long totalParticipaantesAtivos = participanteRepo.countPgByBolao(bolao);
			Double totalValor = totalParticipaantesAtivos * bolao.getValor();
			
			//seta o card do valor do premio
			if (bolao.getTaxaAdministrativa() != null)
				m.addAttribute("premioEstimado", totalValor - ((totalValor * bolao.getTaxaAdministrativa()) / 100) );
			
			//seta o quadro de top10 do rannking
			List<Participante> top10 = participanteRepo.findTop10ByBolaoOrderByClassificacaoAscExibirClassificacaoNoRankingDesc(bolao); 
			m.addAttribute("top10",top10.stream().filter(p -> p.getPg()).collect(Collectors.toList()) );
			
			//seta o quadro com os prpoximos jogos
			LocalDateTime  hoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
			palpiteHelper.processarConsultaDePalpiteRelacionandoApenasComResultadosDosJogos(participante, m, 
					palpiteRepo.findByParticipanteOrderByData(participante, hoje, new PageRequest(0, 10)));
			m.addAttribute("colunasCards",6);
			m.addAttribute("meuPalpite",true);
			
			//verifica se existe jogos pendentes de informacao de palpite
			m.addAttribute("jogosPendentes",jogoRepo.countJogosComPalpitesPendentesByBolaoAndParticipante(bolao, 
					autenticacao.getParticipante()));
			
		}	
	}

	private Participante criaParticipanteCasoNecessario(Participante participante) {
		if (participante == null) {
			participante = new Participante();
			participante.setBolao(autenticacao.getBolao());
			participante.setUsuario(autenticacao.getUsuario());
			participanteRepo.save(participante);
		}
		return participante;
	}

	
	@GetMapping("/{linkBolao}/regulamento")
	public String regulamento( @PathVariable("linkBolao") String linkBolao, Model model) throws AppException {
		Bolao bolao = bolaoHelper.getBolaoByPermaLink(linkBolao);
		model.addAttribute(autenticacao);
		model.addAttribute(bolao);
		return "pages/regulamento";
	}


	
	private String redirecionaDeAcordoComAutenticacao(Model m) throws AppException {
		return redirecionaDeAcordoComAutenticacao(m,null);
	}

	private String redirecionaDeAcordoComAutenticacao(Model m, String redirectToBolao) throws AppException {
		if (autenticacao.isAutenticado()) {
			m.addAttribute(autenticacao);
			if (autenticacao.getUsuario().getAdmin() != true)
				return "pages/home";
			else {
				participanteHelper.prepareAllParticipantes(autenticacao.getBolao().getPermalink(), m);
				return "pages/participantes";
			}
			
		}
		else {
			m.addAttribute("usuario", new Usuario());
			return "pages/login";
		}
	}

	private void criarUsuarioAdminCasoNecessario() {
		if (userRepo.count() == 0)	
			startUp.startupSistema();
	}
	
}
