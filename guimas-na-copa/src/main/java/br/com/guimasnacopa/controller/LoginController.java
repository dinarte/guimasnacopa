package br.com.guimasnacopa.controller;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;

import br.com.guimasnacopa.domain.Autorizacao;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;
import br.com.guimasnacopa.exception.ValidacaoException;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.AutorizacaoRespository;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.UserRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.BolaoService;


@Controller
@RequestScope
public class LoginController {

	@Autowired
	private Autenticacao autenticacao;
	
	@Autowired
	private UserRepository uRepo;
	
	@Autowired
	private BolaoRepository bolaoRepo;
	
	@Autowired BolaoService bolaoService;
	
	@Autowired
	private NovoUsuarioController novoUsuarioController;
	
	
	@Autowired
	private ParticipanteRepository participanteRepo;

	@Autowired
	AppMessages appMessages;
	
	@Value("${guimasnacopa.config.bolaoAtivo}")
	String bolaoAtivo;
	
	@Autowired
	HttpServletRequest request;

	@Autowired
	HttpServletResponse response;
	
	@Autowired
	Environment env;
	
	@Autowired
	ResourceLoader versionResource;
	
	@Autowired
	AutorizacaoRespository autorizacaoRespo;
	

	@RequestMapping(value = "/heartbeat", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public void heartbeat(@CookieValue( name = "guimasbet-session-id") String autIdOpt) {
			autorizacaoRespo.findById(autIdOpt).flatMap(autorizacao -> {
			Integer idUser = autorizacao.getUsuario().getId();
			Autenticacao.getUserheartbeats().put(idUser, System.currentTimeMillis());
			return Optional.empty();
		});
        
    }
		
	@PostMapping("/login")
	public String login( Usuario usuario, Model model) throws LoginException {
		autenticacao.setUsuario(uRepo.findOneByEmailSenha(usuario.getEmail(), usuario.getPass()));
		if ( autenticacao.getUsuario() != null) {
			autenticacao.setAutenticado(true);
			autenticacao.setBolao(bolaoRepo.findOneByPermalink(bolaoAtivo));
			
			String idAutorizacao = gerarToken(autenticacao.getUsuario().getId());
			Autorizacao autorizacao = new Autorizacao();
			autorizacao.setId(idAutorizacao);
			autorizacao.setUsuario(autenticacao.getUsuario());
			autorizacao.setDateTime(LocalDateTime.now());
			autorizacao.setDispositivo(request.getHeader("User-Agent"));
			autorizacao.setIp(request.getRemoteAddr());
			response.addCookie(getCookie(idAutorizacao));
			autorizacaoRespo.save(autorizacao);
			autenticacao.setAutorizacao(autorizacao);
			model.addAttribute(autenticacao);
			
			return redirectAccordingByKindOfUser();
		}
		else {
			throw new LoginException("Que é isso jogador? errou seus dados! Tente novamente...");
		}	
	}
	
	
	@ResponseBody()
	@RequestMapping(value = "google-oauth-test", method = {RequestMethod.POST}, produces = "application/json")
	public Map<String, String[]> simularLoginNoGoogle(HttpServletRequest request, HttpServletResponse response) {
		return request.getParameterMap(); 
	}
	
	
	@ResponseBody()
	@RequestMapping(value = "google-oauth-set-cookie/{value}", method = {RequestMethod.GET}, produces = "application/json")
	public HttpStatus setCookie(HttpServletResponse response, @PathVariable String value) {
		response.addCookie(new Cookie("g_csrf_token", value));
		return HttpStatus.OK; 
	}
	
	

	@PostMapping(value = "google-oauth")
	public String loginGoogleAuth(
						String credential, 
						@CookieValue(name = "g_csrf_token") String gCsrfCookie,
						@RequestParam(value = "g_csrf_token") String gCsrfToken,
						Model model) throws LoginException, ValidacaoException {
		
		
		if (gCsrfCookie == null)
			throw new LoginException("Falha no login do Gogle: No CSRF token in Cookie.");
			
		if (gCsrfToken == null)
			throw new LoginException("Falha no login do Gogle: No CSRF token in Post Body.");
		
		//if (!gCsrfCookie.equals(gCsrfToken)) 
			//throw new LoginException("Falha no login do Gogle: Failed to verify double submit cookie.");
		
		String domain = request.getServerName();
		model.addAttribute("domain", domain);
		
		String googleOHauthClientId =  env.getProperty("google."+request.getServerName()+".oauth.client_id");
	
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
				.Builder(new NetHttpTransport(), new GsonFactory())
			    .setAudience(Collections.singletonList(googleOHauthClientId))
			    .build();

		GoogleIdToken idToken;
		try {
			idToken = verifier.verify(credential);
		} catch (GeneralSecurityException | IOException e) {
			throw new LoginException(e.getMessage());
		}
		
		if (idToken == null) {
			throw new LoginException("Invalid ID token.");
		}
		
		Payload payload = idToken.getPayload();		
		String email = payload.getEmail();
		String name = (String) payload.get("name");
		String urlFoto = (String) payload.get("picture");

		Usuario u = uRepo.findByEmail(email);
				
		if (u == null) {
			u = new Usuario();
			u.setName(name);
			u.setEmail(email);
			u.setAdmin(false);
			u.setPass(payload.getSubject());
			u.setPassConfirm(payload.getSubject());
			u.setLoginStrategy("br.com.guimasbet.auth.strategy.external.GoogleOAuthStrategy");
			
			novoUsuarioController.singUp(u, model);
		}
		
		u.setUrlFoto(urlFoto);
		u.setUltimoLogin(LocalDate.now());
		u.setAuthPayLoad(new Gson().toJson( payload ));
		uRepo.save(u);
		
		return login(u, model);
	}

	
	
	public String login(String linkBolao, Model model) throws BolaoNaoSelecionadoException {
		return login(bolaoAtivo, model, null);
	}
	
	public String login(Model model) throws BolaoNaoSelecionadoException {
		return login(bolaoAtivo, model, null);
	}
	
	@GetMapping("/login")
	public String login(Model model, @CookieValue( name = "guimasbet-session-id") String autId) throws BolaoNaoSelecionadoException {
		return login(bolaoAtivo, model, autId);
	}
	
	@GetMapping("/{linkBolao}/login/")
	public String login(@PathVariable("linkBolao") String linkBolao, Model model, @CookieValue( name = "guimasbet-session-id") String autIdOpt) throws BolaoNaoSelecionadoException {
		
		String autId = getAutId(autIdOpt);
		
		if (getAutorizacaFromCookie(autId).isPresent()) {
			model.addAttribute(autenticacao);
			return redirectAccordingByKindOfUser();
		} else {
		
			Bolao bolao = bolaoService.getBolaoByPermaLink(linkBolao);
			model.addAttribute(bolao);
			model.addAttribute(appMessages);
			model.addAttribute("usuario", new Usuario());
			
			String domain = request.getServerName();
			model.addAttribute("domain", domain);
			
			String googleOHauthClientId =  env.getProperty("google."+request.getServerName()+".oauth.client_id");
			String googleOHauthLoginUri = env.getProperty("google."+request.getServerName()+".oauth.login_uri");
			
			model.addAttribute("googleOHauthClientId", googleOHauthClientId);
			model.addAttribute("googleOHauthLoginUri", googleOHauthLoginUri);
			
			model.addAttribute("appVersion", getVersion());
			
			//seta o card de total de participantes
			Long totalParticipaantesAtivos = participanteRepo.countPgByBolao(bolao);
			Double totalValor = totalParticipaantesAtivos * bolao.getValor();
			
			//seta o card do valor do premio
			if (bolao.getTaxaAdministrativa() != null)
				model.addAttribute("premioEstimado", totalValor - ((totalValor * bolao.getTaxaAdministrativa()) / 100) );
			
			return "pages/login";
			
		}
	}


	private String getAutId(String autIdOpt) {
		String autId =  Optional.ofNullable(autIdOpt).orElse( 
				
				Arrays
						.stream(request.getCookies())
						.filter(cookie -> cookie.getName().equals("guimasbet-session-id") )
						.map(Cookie::getValue)
						.findFirst()
						.orElse(null)
			);
		return autId;
	}
	
	
	@GetMapping("/logout")
	public String logout(Model model) throws BolaoNaoSelecionadoException {
		
		if (autenticacao.isAutenticado()) {
			String msg = autenticacao.getUsuario().getName().split(" ")[0] + ", "
					+ "Foi muinto bom ver vc por aqui e esperamos vê-lo novamente em breve...";
			appMessages.getSuccessList().add(msg);
			model.addAttribute(appMessages);
			autenticacao.setUsuario(null);
			autenticacao.setAutenticado(false);
			autenticacao.getAutorizacao().setDataTimeDisconnected(LocalDateTime.now());
			autorizacaoRespo.save(autenticacao.getAutorizacao());
			autenticacao.setAutorizacao(null);
		}	
		return login(bolaoAtivo, model);
	}
	
	
	private String getVersion()  {
        Properties props = new Properties();
        try {
			props.load(versionResource.getResource("classpath:version.properties").getInputStream());
		} catch (IOException e) {
			System.err.println("Unable to load version.properties");
			e.printStackTrace();
		}
        return props.getProperty("version");
    }
	
	
	private String gerarToken(Integer userId) {
        String randomId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String rawToken = userId.toString() + randomId + timestamp;
        return rawToken;
    }
	
	private Cookie getCookie(String value) {
	        Cookie cookie = new Cookie("guimasbet-session-id", value);
	        cookie.setMaxAge(200 * 24 * 60 * 60); // 7 dias de validade
	        cookie.setHttpOnly(true); // Apenas acessível via HTTP (não acessível por JavaScript)
	       //cookie.setSecure(true); // Apenas enviado via HTTPS
	       //cookie.setPath("/"); // Caminho onde o cookie é válido
	        return cookie;
   }
	
   private Optional<Autorizacao> getAutorizacaFromCookie(String autId) {
	
		
		Optional.ofNullable(autId).ifPresent(autoId ->{
			Optional<Autorizacao> autorizacaoOpt = autorizacaoRespo.findById(autId).filter(Autorizacao::isConnected); 
			autorizacaoOpt.ifPresent (autorizacao -> {
				autenticacao.setUsuario(autorizacao.getUsuario());
				autenticacao.setAutenticado(true);
				autenticacao.setBolao(bolaoRepo.findOneByPermalink(bolaoAtivo));
				autenticacao.setAutorizacao(autorizacao);
				
			});
		});
		
		return Optional.ofNullable(autenticacao.getAutorizacao());
	}
	
	private String redirectAccordingByKindOfUser() {
		if (autenticacao.getUsuario().getAdmin() != true)
			return "redirect:/" + bolaoAtivo; 
		else
			return "redirect:/bolao/listar";
	}
	
}
