package br.com.guimasnacopa.componentes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.exception.AppException;
import br.com.guimasnacopa.exception.ErroException;
import br.com.guimasnacopa.repository.BolaoRepository;

@Component
public class BolaoHelper {
	
	@Autowired
	BolaoRepository bolaoRepo;
	
	public Bolao getBolaoByPermaLink(String linkBolao) throws AppException {
		Bolao bolao = bolaoRepo.findOneByPermalink(linkBolao);
		if (bolao == null)
			throw new ErroException("Praaaa foraaaaaaaaaaaaa!!!! Não  encontramos o bolão especificado.");
		return bolao;
	}
}
