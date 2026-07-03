package br.com.guimasnacopa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.TimeNoJogo;
import br.com.guimasnacopa.repository.JogoRepository;

@Service
public class PendenciasAdministrativasService {

	@Autowired
	private JogoRepository jogoRepository;

	public PendenciasAdministrativas getPendencias(Bolao bolao) {
		List<Jogo> jogos = jogoRepository.findAllByBolaoOrderByDataDescCompeticaoFaseGrupo(bolao);
		LocalDateTime limiteInicio = LocalDateTime.now().minusHours(2);

		List<Jogo> jogosEmAndamentoAtrasados = new ArrayList<>();
		List<Jogo> jogosMataMataEmpatadosSemVencedor = new ArrayList<>();

		for (Jogo jogo : jogos) {
			if (isJogoEmAndamentoAtrasado(jogo, limiteInicio)) {
				jogosEmAndamentoAtrasados.add(jogo);
			}

			if (isJogoMataMataEmpatadoSemVencedor(jogo)) {
				jogosMataMataEmpatadosSemVencedor.add(jogo);
			}
		}

		jogosEmAndamentoAtrasados.sort(Comparator.comparing(Jogo::getData, Comparator.nullsLast(LocalDateTime::compareTo)));
		jogosMataMataEmpatadosSemVencedor
				.sort(Comparator.comparing(Jogo::getData, Comparator.nullsLast(LocalDateTime::compareTo)));

		PendenciasAdministrativas pendencias = new PendenciasAdministrativas();
		pendencias.setJogosEmAndamentoAtrasados(jogosEmAndamentoAtrasados);
		pendencias.setJogosMataMataEmpatadosSemVencedor(jogosMataMataEmpatadosSemVencedor);
		return pendencias;
	}

	@Transactional
	public void encerrarJogo(Integer jogoId, Bolao bolao) {
		Jogo jogo = getJogoDoBolao(jogoId, bolao);
		jogo.setExecucao(Jogo.EXECUSSAO_ENCERRADO);
		jogoRepository.save(jogo);
	}

	@Transactional
	public void definirVencedor(Integer jogoId, Integer timeNoJogoId, Bolao bolao) {
		Jogo jogo = getJogoDoBolao(jogoId, bolao);
		if (!isJogoMataMataEmpatadoSemVencedor(jogo)) {
			throw new IllegalArgumentException("Este jogo nao esta apto para definicao manual de vencedor.");
		}

		for (TimeNoJogo timeNoJogo : jogo.getTimesNoJogo()) {
			timeNoJogo.setVencedor(timeNoJogo.getId().equals(timeNoJogoId));
		}

		jogo.setExecucao(Jogo.EXECUSSAO_ENCERRADO);
		jogoRepository.save(jogo);
	}

	private Jogo getJogoDoBolao(Integer jogoId, Bolao bolao) {
		Jogo jogo = jogoRepository.findById(jogoId).orElseThrow();
		if (!jogo.getFase().getBolao().getId().equals(bolao.getId())) {
			throw new IllegalArgumentException("Jogo nao pertence ao bolao selecionado.");
		}
		return jogo;
	}

	private boolean isJogoEmAndamentoAtrasado(Jogo jogo, LocalDateTime limiteInicio) {
		return jogo.isEmAndamento()
				&& jogo.getData() != null
				&& jogo.getData().isBefore(limiteInicio);
	}

	private boolean isJogoMataMataEmpatadoSemVencedor(Jogo jogo) {
		if (jogo.getFase() == null || !"mata-mata".equalsIgnoreCase(jogo.getFase().getTipo())) {
			return false;
		}
		if (jogo.getTimesNoJogo() == null || jogo.getTimesNoJogo().size() < 2) {
			return false;
		}

		Integer golsA = jogo.getTimesNoJogo().get(0).getGols();
		Integer golsB = jogo.getTimesNoJogo().get(1).getGols();
		boolean empate = golsA != null && golsB != null && golsA.equals(golsB);
		boolean semVencedor = jogo.getTimesNoJogo().stream().noneMatch(TimeNoJogo::getVencedor);
		return empate && semVencedor;
	}

	public static class PendenciasAdministrativas {
		private List<Jogo> jogosEmAndamentoAtrasados;
		private List<Jogo> jogosMataMataEmpatadosSemVencedor;

		public List<Jogo> getJogosEmAndamentoAtrasados() {
			return jogosEmAndamentoAtrasados;
		}

		public void setJogosEmAndamentoAtrasados(List<Jogo> jogosEmAndamentoAtrasados) {
			this.jogosEmAndamentoAtrasados = jogosEmAndamentoAtrasados;
		}

		public List<Jogo> getJogosMataMataEmpatadosSemVencedor() {
			return jogosMataMataEmpatadosSemVencedor;
		}

		public void setJogosMataMataEmpatadosSemVencedor(List<Jogo> jogosMataMataEmpatadosSemVencedor) {
			this.jogosMataMataEmpatadosSemVencedor = jogosMataMataEmpatadosSemVencedor;
		}

		public int getTotalPendencias() {
			int a = jogosEmAndamentoAtrasados == null ? 0 : jogosEmAndamentoAtrasados.size();
			int b = jogosMataMataEmpatadosSemVencedor == null ? 0 : jogosMataMataEmpatadosSemVencedor.size();
			return a + b;
		}

		public boolean hasPendencias() {
			return getTotalPendencias() > 0;
		}
	}
}