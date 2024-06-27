package br.com.guimasnacopa.controller;

import java.net.URI;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.guimasnacopa.api.domain.FixturesLeague;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.service.ApiService;

@RestController("apiController")
@RequestMapping(path = "/api/v1/")
public class CargaInicialApiController {
	
	@Autowired
	private ApiService api;
	
	@Autowired
	BolaoRepository bolaoRepository;
	
	
	@GetMapping(path = "import/apifootball/{league}/{season}")
	public ResponseEntity<List<FixturesLeague>> carga(@PathVariable String league, @PathVariable String season) {
		List<FixturesLeague> ret =  api.carregarInicial(league, season);
		return ResponseEntity.ok(ret);
	}
	
	@PostMapping(path = "bolao")
	public ResponseEntity<Bolao> criarbolao(@RequestBody Bolao bolao) throws LoginException{
		bolaoRepository.save(bolao);
		api.gerarDadosDoBolao(bolao);
		return ResponseEntity.created(URI.create("/api/bolao/" + bolao.getId())).body(bolao);
		
	}

}
