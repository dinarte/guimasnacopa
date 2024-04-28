package br.com.guimasnacopa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.repository.BolaoRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.repository.UserRepository;

@Transactional
@Service
public class SingUpService {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	BolaoRepository bolaoRepo;
	
	@Autowired
	ParticipanteRepository participanteRepo;
	
	@Autowired
	PalpiteService palpiteService;

	
	/**
	 * Cria um usuario, o participante e os palpites 
	 * @param usuario
	 */
	public void criarUsuarioEParticipante(Usuario usuario, String linkBolao) {	
		usuario.setAdmin(false);
		userRepo.save(usuario);
		
		Bolao bolao = bolaoRepo.findOneByPermalink(linkBolao);
		
		Participante p = new Participante();
		p.setBolao(bolao);
		p.setUsuario(usuario);
		p.setPontuacao(0.0);
		p.setClassificacao(1);
		participanteRepo.save(p);
		
		//palpiteService.criarPalpites(p);
		
	}

	
}