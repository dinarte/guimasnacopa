package br.com.guimasnacopa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDTO;
import br.com.guimasnacopa.apifootbol.service.CampeonatoApiService;
import br.com.guimasnacopa.domain.Competicao;
import br.com.guimasnacopa.repository.CompeticaoRepository;

@Service
public class CarregarCampeonatosDaApiService {
	
	@Autowired
	CampeonatoApiService campeonatoApiService;
	
	@Autowired
	CompeticaoRepository competicaoRepository;
	
	
	public List<CampeonatoDTO> listAllByApi() {
		return campeonatoApiService.getAll();
	}
	
	
	public void carregarCompeticao(Long campeonatoId) {
		
		if (competicaoRepository.findOneByIdApi(campeonatoId).isEmpty()) {
		
			CampeonatoDTO dto = campeonatoApiService.getById(campeonatoId);
				
			Competicao competicao = new Competicao();
			competicao.setIdApi(dto.getCampeonatoId());
			competicao.setLogoUrl(dto.getLogo());
			competicao.setNome(dto.getNome());
			competicao.setNomeAmigavel(dto.getNomePopular());
			competicao.setSessao(Long.valueOf(dto.getEdicaoAtual().getTemporada()));
			competicao.setStatus(dto.getStatus());
				
			competicaoRepository.save(competicao);
		}
	}

}
