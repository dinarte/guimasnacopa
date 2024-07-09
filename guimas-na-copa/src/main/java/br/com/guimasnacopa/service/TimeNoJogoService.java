package br.com.guimasnacopa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.repository.TimeNoJogoRepository;

@Transactional
@Service
public class TimeNoJogoService {
	
	@Autowired
	private TimeNoJogoRepository timeNojogoRepository;
	
	public void updateGols(Integer gols, boolean vencedor, Integer idTimeNoJogo) {
		timeNojogoRepository.updateGolsAndVencedor(gols, vencedor, idTimeNoJogo);
	}
	
	public void updateGols(TimeNoJogo tmjA, TimeNoJogo tmjB) {
		timeNojogoRepository.updateGols(tmjA.getGols() == null ? 0 : tmjA.getGols(), tmjA.getId());
		timeNojogoRepository.updateGols(tmjB.getGols() == null ? 0 : tmjB.getGols(), tmjB.getId());
	}
}
