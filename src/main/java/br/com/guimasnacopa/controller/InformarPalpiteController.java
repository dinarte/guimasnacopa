package br.com.guimasnacopa.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.componentes.GroupNode;
import br.com.guimasnacopa.componentes.ObjectsGroup;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.messages.AppMessages;
import br.com.guimasnacopa.repository.PalpiteRepository;
import br.com.guimasnacopa.repository.TimeRepository;
import br.com.guimasnacopa.security.Autenticacao;
import br.com.guimasnacopa.service.PalpiteService;

@Controller
@RequestScope
public class InformarPalpiteController {

	@Autowired
	Autenticacao autenticacao;
	
	@Autowired
	PalpiteRepository palpiteRepo;
	
	@Autowired
	PalpiteService palpiteService;
	
	@Autowired
	TimeRepository timeRepositpry;
	
	@GetMapping("/palpite/editar")
	public String editarPalpite(Model model) throws LoginException{
		autenticacao.checkAthorization();
		
		palpiteService.criarPalpitesCasoNecessario(autenticacao.getParticipante());
		
		List<Palpite> palpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
	
		Map<Object, List<Object>> timesMap = agruparTimesEPegarMapa(palpites);
		
		
		
		List<GroupNode> palpitesEmAbertoNodes = filtarEAgruparPalpites(palpites, Palpite::isApostaAberta);
		List<GroupNode> palpitesFechadosNodes = filtarEAgruparPalpites(palpites, Palpite::isApostaFechada);
		 
		model.addAttribute("timesMap",timesMap);
		model.addAttribute(autenticacao);
		model.addAttribute("palpitesEmAbertoNodes", palpitesEmAbertoNodes);
		model.addAttribute("palpitesFechadosNodes", palpitesFechadosNodes);
		return "pages/palpite";
		
	}




	private Map<Object, List<Object>> agruparTimesEPegarMapa(List<Palpite> palpites) {
		List<GroupNode> timesGroup = ObjectsGroup 
				  .from(palpites
						  .stream()
						  .filter(Palpite::isResultado)
						  .collect(Collectors.toList())
						 ) 
				  .groupBy(( p -> ((Palpite) p).getBolaoCompeticao().getCompeticao().getId())) 
				  .groupBy(( p -> ((Palpite) p).getTimeA())) 
				  .getNodes();
		
		Map<Object, List<Object>> timesMap = timesGroup.stream().collect(Collectors.toMap(GroupNode::getNode, GroupNode::getChildrenAsLastLevel));
		return timesMap;
	}




	private List<GroupNode> filtarEAgruparPalpites(List<Palpite> palpites, Predicate<? super Palpite> predicate) {
		Comparator<Palpite> comparadorOrdenacao = Comparator
				.comparing((Palpite p) -> p.getBolaoCompeticao().getCompeticao().getNome(), String.CASE_INSENSITIVE_ORDER)
				.thenComparingInt(this::obterOrdemFase)
				.thenComparing(this::obterDescricaoDaFase, String.CASE_INSENSITIVE_ORDER)
				.thenComparingInt(this::obterOrdemGrupo)
				.thenComparing(Palpite::getDescricaoGrupo, String.CASE_INSENSITIVE_ORDER)
				.thenComparing(
						this::obterDataOrdenacao,
						Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(Palpite::getId, Comparator.nullsLast(Comparator.naturalOrder()));

		List<Palpite> palpitesOrdenados = palpites.stream()
				.filter(predicate)
				.sorted(comparadorOrdenacao)
				.collect(Collectors.toList());

		Map<String, LocalDateTime> ordemGrupoPorData = construirOrdemGrupoPorData(palpitesOrdenados);

		List<GroupNode> palpitesEmAbertoNodes = ObjectsGroup
				 .from(palpitesOrdenados) 
				 .groupBy(( p -> ((Palpite) p).getBolaoCompeticao().getCompeticao())) 
				 .groupBy(( p -> obterChaveDaFase((Palpite) p)))
				 .groupBy(( p -> obterChaveDoGrupo((Palpite) p, ordemGrupoPorData)))
				 .getNodes();
		return palpitesEmAbertoNodes;
	}

	private Map<String, LocalDateTime> construirOrdemGrupoPorData(List<Palpite> palpitesOrdenados) {
		Map<String, LocalDateTime> ordemGrupoPorData = new HashMap<>();
		Comparator<LocalDateTime> comparatorData = Comparator.nullsLast(LocalDateTime::compareTo);

		palpitesOrdenados.forEach(palpite -> {
			String chave = montarChaveGrupoOrdenacao(palpite);
			LocalDateTime data = obterDataOrdenacao(palpite);
			LocalDateTime dataAtual = ordemGrupoPorData.get(chave);

			if (dataAtual == null || comparatorData.compare(data, dataAtual) < 0) {
				ordemGrupoPorData.put(chave, data);
			}
		});

		return ordemGrupoPorData;
	}

	private GrupoGroupKey obterChaveDoGrupo(Palpite palpite, Map<String, LocalDateTime> ordemGrupoPorData) {
		String nomeGrupo = palpite.getDescricaoGrupo();
		if (nomeGrupo == null || nomeGrupo.trim().isEmpty()) {
			nomeGrupo = "SEM GRUPO";
		}

		LocalDateTime dataPrimeiroJogoDoGrupo = ordemGrupoPorData.get(montarChaveGrupoOrdenacao(palpite));
		return new GrupoGroupKey(dataPrimeiroJogoDoGrupo, nomeGrupo);
	}

	private String montarChaveGrupoOrdenacao(Palpite palpite) {
		Integer competicaoId = palpite.getBolaoCompeticao() != null && palpite.getBolaoCompeticao().getCompeticao() != null
				? palpite.getBolaoCompeticao().getCompeticao().getId()
				: null;

		return String.valueOf(competicaoId)
				+ "|" + obterOrdemFase(palpite)
				+ "|" + obterDescricaoDaFase(palpite)
				+ "|" + palpite.getDescricaoGrupo();
	}

	private LocalDateTime obterDataOrdenacao(Palpite palpite) {
		return palpite.getJogo() != null ? palpite.getJogo().getData() : palpite.getLimiteAposta();
	}

	private FaseGroupKey obterChaveDaFase(Palpite palpite) {
		return new FaseGroupKey(obterOrdemFase(palpite), obterDescricaoDaFase(palpite));
	}

	private String obterDescricaoDaFase(Palpite palpite) {
		if (palpite.isResultado() && palpite.getJogo() != null && palpite.getJogo().getFase() != null) {
			return palpite.getJogo().getFase().getNome();
		}

		if (palpite.isAcertarTimes() || palpite.isAcertarCampeao()) {
			return "BONUS";
		}

		return "SEM FASE";
	}

	private int obterOrdemFase(Palpite palpite) {
		if (palpite.isResultado()
				&& palpite.getJogo() != null
				&& palpite.getJogo().getFase() != null
				&& palpite.getJogo().getFase().getOrdinal() != null) {
			return palpite.getJogo().getFase().getOrdinal();
		}

		if (palpite.isAcertarTimes() || palpite.isAcertarCampeao()) {
			return Integer.MAX_VALUE - 1;
		}

		return Integer.MAX_VALUE;
	}

	private int obterOrdemGrupo(Palpite palpite) {
		String grupo = palpite.getDescricaoGrupo();
		if (grupo == null || grupo.trim().isEmpty()) {
			return Integer.MAX_VALUE - 2;
		}

		String grupoNormalizado = grupo.trim().toUpperCase();
		if (grupoNormalizado.startsWith("GRUPO ") && grupoNormalizado.length() >= 7) {
			char sufixo = grupoNormalizado.charAt(6);
			if (sufixo >= 'A' && sufixo <= 'Z') {
				return sufixo - 'A';
			}
		}

		if (grupoNormalizado.contains("BONUS")) {
			return Integer.MAX_VALUE - 1;
		}

		return Integer.MAX_VALUE;
	}

	private static class FaseGroupKey implements Comparable<FaseGroupKey> {
		private final Integer ordem;
		private final String nome;

		private FaseGroupKey(Integer ordem, String nome) {
			this.ordem = ordem;
			this.nome = nome;
		}

		@Override
		public int compareTo(FaseGroupKey other) {
			Comparator<Integer> comparatorOrdem = Comparator.nullsLast(Integer::compareTo);
			int compareOrdem = comparatorOrdem.compare(this.ordem, other.ordem);
			if (compareOrdem != 0) {
				return compareOrdem;
			}

			Comparator<String> comparatorNome = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
			return comparatorNome.compare(this.nome, other.nome);
		}

		@Override
		public String toString() {
			return nome;
		}

		@Override
		public int hashCode() {
			return Objects.hash(nome, ordem);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			FaseGroupKey other = (FaseGroupKey) obj;
			return Objects.equals(nome, other.nome) && Objects.equals(ordem, other.ordem);
		}
	}

	private static class GrupoGroupKey implements Comparable<GrupoGroupKey> {
		private final LocalDateTime dataPrimeiroJogo;
		private final String nome;

		private GrupoGroupKey(LocalDateTime dataPrimeiroJogo, String nome) {
			this.dataPrimeiroJogo = dataPrimeiroJogo;
			this.nome = nome;
		}

		@Override
		public int compareTo(GrupoGroupKey other) {
			Comparator<LocalDateTime> comparatorData = Comparator.nullsLast(LocalDateTime::compareTo);
			int compareData = comparatorData.compare(this.dataPrimeiroJogo, other.dataPrimeiroJogo);
			if (compareData != 0) {
				return compareData;
			}

			Comparator<String> comparatorNome = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
			return comparatorNome.compare(this.nome, other.nome);
		}

		@Override
		public String toString() {
			return nome;
		}

		@Override
		public int hashCode() {
			return Objects.hash(dataPrimeiroJogo, nome);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			GrupoGroupKey other = (GrupoGroupKey) obj;
			return Objects.equals(dataPrimeiroJogo, other.dataPrimeiroJogo)
					&& Objects.equals(nome, other.nome);
		}
	}
	

	
	
	@PostMapping("/palpite/save")
	public String editarPalpite(HttpServletRequest request, Model model, AppMessages appMessagens) throws LoginException {
		List<Palpite> palpites = palpiteRepo.findAllByParticipante(autenticacao.getParticipante());
		palpites.forEach(p -> {
			String valuesA = request.getParameter(p.getKeyGolsTimeA());
			String valuesB = request.getParameter(p.getKeyGolsTimeB());
			
			if (p.isResultado()) {
				if (valuesA != null && valuesA != "") 
					p.setGolsTimeA(Integer.parseInt(valuesA));
				if (valuesB != null && valuesB != "") 
					p.setGolsTimeB(Integer.parseInt(valuesB));
			}else {
				
				if (valuesA != null && valuesA != "") {
					Time t = new Time();
					t.setId(Integer.parseInt(valuesA));
					p.setTimeA(t);
				}	
				if (valuesB != null && valuesB != "") {
					Time t = new Time();
					t.setId(Integer.parseInt(valuesB));
					p.setTimeB(t);
				} 
			}
				
			palpiteRepo.save(p);
		});
		appMessagens.getSuccessList().add("Seus palpites foram salvos com sucesso.");
		model.addAttribute("appMessagens",appMessagens);
		return editarPalpite(model);
	}
	

	
	
}
