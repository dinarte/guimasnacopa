package br.com.guimasnacopa.componentes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Component
@RequestScope
public class ParticipanteHelper {

	@Autowired 
	private BolaoHelper bolaoHelper;
	
	@Autowired
	private ParticipanteRepository participanteRepo;
	
	@Autowired 
	PalpiteRepository palpiteRepo;
	
	@Autowired
	Autenticacao autenticacao;
	
	public void prepareAllParticipantes(String linkBolao, Model model) throws AppException {
		Bolao bolao = prepareModel(linkBolao, model);
		List<Participante> participantes = participanteRepo.findAllByBolaoOrderByUsuario_nameAsc((bolao));

		participantes.forEach(p -> {
			popularPorcentagemDePreenchimentoDoParticipante(p);
		});
		model.addAttribute("participantes",participantes);
	}

	public void popularPorcentagemDePreenchimentoDoParticipante(Participante p) {
		List<Palpite> palpitesDoParaticipante = palpiteRepo.findAllByParticipante(p);
		palpitesDoParaticipante.forEach(palpite -> {
			p.palpitesParaInformar ++;
			if (palpite.isResultado()) {
				p.palpitesParaInformar ++;
				if (palpite.getGolsTimeA() != null)
					p.palpitesInformados ++;
				if (palpite.getGolsTimeB() != null)
					p.palpitesInformados ++;
			}else if (palpite.isAcertarTimes()) {
				p.palpitesParaInformar ++;
				if (palpite.getTimeA() != null)
					p.palpitesInformados ++;
				if (palpite.getTimeB() != null)
					p.palpitesInformados ++;
			}else{ // acertarCampeao
				if (palpite.getTimeA() != null)
					p.palpitesInformados ++;
			}
			
			Double porcentagem = (Double.valueOf(p.palpitesInformados) * 100) / p.palpitesParaInformar;
			p.setPorcentagemPalpites(porcentagem);
		});
	}
	
	public void prepareRankingParticipantes(String linkBolao, Model model) throws AppException {
		Bolao bolao = prepareModel(linkBolao, model);
		model.addAttribute("participantes", participanteRepo
				.findAllByBolaoOrderByClassificacaoDesc(bolao)
				.stream()
				.filter(p -> p.getPg())
				.collect(Collectors.toList()));
	}

	private Bolao prepareModel(String linkBolao, Model model) throws AppException {
		Bolao bolao = bolaoHelper.getBolaoByPermaLink(linkBolao);
		model.addAttribute(autenticacao);
		model.addAttribute(bolao);
		return bolao;
	}
}
