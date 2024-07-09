

package br.com.guimasnacopa.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.PalpiteHelper;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.exception.ValidacaoException;
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
		model.addAttribute("colunasCards",3);
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
		m.addAttribute("colunasCards",3);
		m.addAttribute("meuPalpite",true);
		return "pages/palpite_participante";
	
	}
	
	@GetMapping("/palpite/{palpite}/detalhar")
	public String detalharPalpite(@PathVariable("palpite") Palpite palpite, Model m) throws LoginException, br.com.guimasnacopa.exception.LoginException, AppException {
		autenticacao.checkAthorization();
		palpite = palpiteRepo.findById(palpite.getId()).get();
		if (palpite.isApostaAberta()){
			appMessages.getWarningList().add("Calma atacente, você está impedido!!! Este jogo ainda está com o período de palpites aberto e nesse período não é permitido acessar os detalhes dos palpites, volte aqui depois!");
			m.addAttribute(appMessages);
			return home.home(autenticacao.getBolao().getPermalink(), m);
		}
		if (palpite.isResultado())
			return detalharPalpiteResultado(palpite, m);
		if (palpite.isAcertarTimes())
			return detalharPalpiteAcertarTimes(palpite, m);
		if (palpite.isAcertarCampeao())
			return detalharPalpiteAcertarCampeao(palpite, m);
		else
			throw new ValidacaoException("Operação não disponível para esse tipo de palpite: " + palpite.getTipo());
		
	}
	
	private String detalharPalpiteResultado(Palpite palpite, Model m) throws LoginException, br.com.guimasnacopa.exception.LoginException, AppException {
		List<Palpite> palpites = palpiteRepo.findAllByJogo(palpite.getJogo());
		List<Map<String,Object>> resumos = palpiteRepo.findResumoByJogo(palpite.getJogo().getId());
		List<Map<String,Object>> resultados = palpiteRepo.findResumoVencedorByJogo(palpite.getJogo().getId());
		String text = formatarResumoParaWhatsApp(palpite, palpites, resumos, resultados);		
		m.addAttribute("textWapp",text);
		palpite.popularGolsDoJogo();
		m.addAttribute("palpiteReferencia",palpite);
		m.addAttribute("resumos",resumos);
		m.addAttribute("resultados",resultados);
		m.addAttribute("palpites", palpites);
		m.addAttribute(autenticacao);
		return "pages/palpite_detalhe"; 
	}
	
	
	
	private String detalharPalpiteAcertarTimes(Palpite palpite, Model m) throws LoginException, br.com.guimasnacopa.exception.LoginException, AppException {
		List<Palpite> palpites = palpiteRepo.findAllByBolaoCompeticaoAndTipoOrderByParticipante_classificacaoAscParticipante_usuario_nameAscBolaoCompeticaoAsc(
				palpite.getBolaoCompeticao(), 
				Palpite.ACERTAR_TIMES);
		Map<String, Long> resumos = agruparResumoAcertarTimes(palpites, Palpite::getDescricaoTimesEmOrdemAfalbetica);
		m.addAttribute("palpiteReferencia",palpite);
		m.addAttribute("resumos",resumos);
		m.addAttribute("palpites", palpites);
		m.addAttribute(autenticacao);
		return "pages/palpite_detalhe_acertar_times";
	}

	
	
	private String detalharPalpiteAcertarCampeao(Palpite palpite, Model m) throws LoginException, br.com.guimasnacopa.exception.LoginException, AppException {
		List<Palpite> palpites = palpiteRepo.findAllByBolaoCompeticaoAndTipoOrderByParticipante_classificacaoAscParticipante_usuario_nameAscBolaoCompeticaoAsc(
				palpite.getBolaoCompeticao(), 
				Palpite.ACERTAR_CAMPEAO);	

		Map<String, Long> resumos = agruparResumoAcertarTimes(palpites, Palpite::getDescricaoTimesEmOrdemAfalbetica);
	
		m.addAttribute("palpiteReferencia",palpite);
		m.addAttribute("resumos",resumos);
		m.addAttribute("palpites", palpites);
		m.addAttribute(autenticacao);
		return "pages/palpite_detalhe_acertar_campeao";
	}
	

	private String formatarResumoParaWhatsApp(Palpite palpiteReferencia, List<Palpite> palpites, List<Map<String, Object>> resumos, List<Map<String, Object>> resultados) {
		
		palpiteReferencia.popularGolsDoJogo();
		String timeA = palpiteReferencia.getTimeA().getNome();
		String timeAEmoji = palpiteReferencia.getTimeA().getEmoji();
		String timeAGols = palpiteReferencia.getGolsDoJogoTimaA() == null ? "" : palpiteReferencia.getGolsDoJogoTimaA().toString();
		
		String timeB = palpiteReferencia.getTimeB().getNome();
		String timeBEmoji = palpiteReferencia.getTimeB().getEmoji();
		String timeBGols = palpiteReferencia.getGolsDoJogoTimaB() == null ? "" : palpiteReferencia.getGolsDoJogoTimaB().toString();
		String execucao = palpiteReferencia.getJogo().getExecucao();
		
		String text = ""+
		"                   *PAI LOLÔ INFORMA:*\n"+		
		"--------------------------------------------------\n"+
		"GUIMAS.BET: " + palpiteReferencia.getJogo().getFase().getBolao().getNome() + "\n" +
		"https://guimasbet.com.br\n\n"+
		"       *"+timeA+" "+timeAEmoji+" "+timeAGols+" Vs "+timeBGols+" "+timeBEmoji+" "+ timeB + "*\n"+
		"               (Jogo " + execucao + ")\n\n" +
		"*Detalhamento*: \n"+
		"\n"+
		"*class.*	*ptos.*	*nome*		*palpites*\n";
		for(Palpite palpite : palpites) {
			System.out.println(">>>>>>" +  palpite.getParticipante().getUsuario().getName());
			String classificacao = palpite.getParticipante().getClassificacao().toString();
			String pontuacao = palpite.getParticipante().getPontuacao().toString();
			String nome = getPrimeiroNome(palpite.getParticipante().getUsuario().getName()).trim();
			String placar = palpite.getGolsTimeA() + " x " + palpite.getGolsTimeB();
			text = text + classificacao +"	"+ pontuacao +"	"+ nome +"		"+ placar + "\n";
		}
		text = text + 
		"---------------------------------------------------\n\n" +
				
		"*Resumo por Placar:* \n"+
		"\n"+
		"	*palpite*			*qtd*		\n";
		for(Map<String, Object> map : resumos) {
			text = text + "	  " + map.get("palpite") +"			"+map.get("qtd")  + "\n";
		}
		text = text + 
		"---------------------------------------------------\n\n" +
				
		"*Resumo Geral*: \n"+
		"\n"+
		"	*palpite*			*qtd*		\n";
		for(Map<String, Object> map : resultados) {
			text = text + "	  " + map.get("palpite") +"			"+map.get("qtd")  + "\n";
		}
		text = text + 
		"---------------------------------------------------\n" +
		"https://guimasbet.com.br \n";
		
		
		try {
			return URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return text;
		}
	}
	
	private String getPrimeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) {
            return "";
        }
        int indiceEspaco = nomeCompleto.indexOf(" ");
        if (indiceEspaco == -1) {
            return nomeCompleto; // Caso não haja espaços, a string inteira é o primeiro nome
        }
        return nomeCompleto.substring(0, indiceEspaco);
    }
	
	private Map<String, Long> agruparResumoAcertarTimes(List<Palpite> palpites, Function<Palpite, String> function) {
		Map<String, Long> resumos = palpites.stream()
        .collect(Collectors.groupingBy(
        	function, 
            Collectors.counting())
        ).entrySet()
         .stream()
         .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
         .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new));
		return resumos;
	}
}	