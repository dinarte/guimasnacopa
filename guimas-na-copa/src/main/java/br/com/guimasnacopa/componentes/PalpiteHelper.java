package br.com.guimasnacopa.componentes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Component
@RequestScope
public class PalpiteHelper {


	@Autowired
	Autenticacao autenticacao;
	
	@Autowired
	PalpiteRepository palpiteRepo;
	
	@Autowired
	TimeNoJogoRepository timeNoJogoRepo;
	
	
	public void processarConsultaDePalpite(Participante participante, Model model, List<Palpite> palpites) {
		relacionarComPalpiteComparado(palpites);
		relacionarComResultadosDosJogos(palpites);
		model.addAttribute("palpiteComComparacao", true);
		model.addAttribute("palpiteComResultado", true);
		preparModel(model, palpites);
	}
	
	
	public void processarConsultaDePalpiteRelacionandoApenasComPalpiteComparado(Participante participante, Model model, List<Palpite> palpites) {
		relacionarComPalpiteComparado(palpites);
		model.addAttribute("palpiteComComparacao", true);
		preparModel(model, palpites);
	}
	
	
	public void processarConsultaDePalpiteRelacionandoApenasComResultadosDosJogos(Participante participante, Model model, List<Palpite> palpites) {
		relacionarComResultadosDosJogos(palpites);
		model.addAttribute("palpiteComResultado", true);
		preparModel(model, palpites);
	}


	private void preparModel(Model model, List<Palpite> palpites) {
		model.addAttribute(autenticacao);
		if (palpites != null && palpites.size() > 0)
			model.addAttribute("participanteConsultado",palpites.get(0).getParticipante());
		model.addAttribute("palpites",palpites);
	}


	private void relacionarComPalpiteComparado(List<Palpite> palpites) {
		List<Palpite> meusPalpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
		palpites.forEach(p ->{
			meusPalpites.forEach(meuPalpite ->{
				if (meuPalpite.isResultado()) {
					if (meuPalpite.getJogo().getId().equals(p.getJogo().getId())) {
						p.setPalpiteComparado(meuPalpite);
					}
				} else if (meuPalpite.getBolaoCompeticao().getId().equals(p.getBolaoCompeticao().getId())) {
					if (meuPalpite.isAcertarTimes() && p.isAcertarTimes() || (meuPalpite.isAcertarCampeao() && p.isAcertarCampeao()) ) {
						p.setPalpiteComparado(meuPalpite);
					}	
				}
				
			});
		});
	}
	
	private void relacionarComResultadosDosJogos(List<Palpite> palpites) {
		
			palpites.forEach(palpite ->{
				if (palpite.isResultado()) {
					palpite.popularGolsDoJogo();
				}	
			});
	
	
	}
}
