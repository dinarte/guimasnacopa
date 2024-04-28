package br.com.guimasnacopa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;
import br.com.guimasnacopa.repository.BolaoRepository;

@Component
public class BolaoService {
	
	@Autowired
	BolaoRepository bolaoRepo;
	
	public Bolao getBolaoByPermaLink(String linkBolao) throws BolaoNaoSelecionadoException {
		Bolao bolao = bolaoRepo.findOneByPermalink(linkBolao);
		if (bolao == null)
			throw new BolaoNaoSelecionadoException("Praaaa foraaaaaaaaaaaaa!!!! Não  encontramos o bolão especificado.");
		return bolao;
	}
}
