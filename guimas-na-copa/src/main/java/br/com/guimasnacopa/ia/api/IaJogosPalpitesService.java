package br.com.guimasnacopa.ia.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IaJogosPalpitesService {

	@Autowired
	IaRepository iaRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public IaBolaoJogosPalpitesDto getJogosComResultadosEPalpites(Integer bolaoId) {
		List<IaBolaoJogosPalpitesFlat> itens = iaRepository.getJogosComResultadosEPalpites(bolaoId);

		if (itens == null || itens.isEmpty()) {
			return null;
		}

		IaBolaoJogosPalpitesDto bolaoDto = new IaBolaoJogosPalpitesDto();
		Map<Integer, IaBolaoJogosPalpitesDto.IaBolaoCompeticaoDto> competicaoMap = new LinkedHashMap<>();
		Map<Integer, Map<Integer, IaBolaoJogosPalpitesDto.IaFaseDto>> faseMapPorCompeticao = new LinkedHashMap<>();
		Map<Integer, Map<Integer, IaBolaoJogosPalpitesDto.IaJogoDto>> jogoMapPorFase = new LinkedHashMap<>();

		for (IaBolaoJogosPalpitesFlat item : itens) {
			if (bolaoDto.getIdBolao() == null) {
				bolaoDto.setIdBolao(item.getIdBolao());
				bolaoDto.setNomeBolao(item.getNomeBolao());
				bolaoDto.setSlugBolao(item.getSlugBolao());
			}

			// --- competicao ---
			IaBolaoJogosPalpitesDto.IaBolaoCompeticaoDto competicaoWrapper = competicaoMap.get(item.getIdCompeticao());
			if (competicaoWrapper == null) {
				competicaoWrapper = new IaBolaoJogosPalpitesDto.IaBolaoCompeticaoDto();
				IaBolaoJogosPalpitesDto.IaCompeticaoDto competicaoDto = new IaBolaoJogosPalpitesDto.IaCompeticaoDto();
				competicaoDto.setIdCompeticao(item.getIdCompeticao());
				competicaoDto.setNomeCompeticao(item.getNomeCompeticao());
				competicaoWrapper.setCompeticao(competicaoDto);

				competicaoMap.put(item.getIdCompeticao(), competicaoWrapper);
				bolaoDto.getBolaoCompeticoes().add(competicaoWrapper);
				faseMapPorCompeticao.put(item.getIdCompeticao(), new LinkedHashMap<>());
			}

			// --- fase ---
			Map<Integer, IaBolaoJogosPalpitesDto.IaFaseDto> fasesMap = faseMapPorCompeticao.get(item.getIdCompeticao());
			IaBolaoJogosPalpitesDto.IaFaseDto faseDto = fasesMap.get(item.getIdFase());
			if (faseDto == null) {
				faseDto = new IaBolaoJogosPalpitesDto.IaFaseDto();
				faseDto.setIdFase(item.getIdFase());
				faseDto.setNome(item.getFaseNome());
				faseDto.setFaseFinal(item.getFaseFinal());
				faseDto.setValoresRegrasPontuacao(buildValoresRegrasPontuacao(item));

				fasesMap.put(item.getIdFase(), faseDto);
				competicaoWrapper.getCompeticao().getFases().add(faseDto);
				jogoMapPorFase.put(item.getIdFase(), new LinkedHashMap<>());
			}

			// --- jogo ---
			Map<Integer, IaBolaoJogosPalpitesDto.IaJogoDto> jogosMap = jogoMapPorFase.get(item.getIdFase());
			IaBolaoJogosPalpitesDto.IaJogoDto jogoDto = jogosMap.get(item.getIdJogo());
			if (jogoDto == null) {
				jogoDto = new IaBolaoJogosPalpitesDto.IaJogoDto();
				jogoDto.setIdJogo(item.getIdJogo());
				jogoDto.setGrupo(item.getGrupo());
				jogoDto.setRodada(item.getRodada());
				jogoDto.setExecucao(item.getExecucao());
				jogoDto.setIdTimeA(item.getIdTimeA());
				jogoDto.setTimeA(item.getTimeA());
				jogoDto.setFlagTimeA(item.getFlagTimeA());
				jogoDto.setEmojiTimeA(item.getEmojiTimeA());
				jogoDto.setGolsTimeA(item.getGolsTimeA());
				jogoDto.setIdTimeB(item.getIdTimeB());
				jogoDto.setTimeB(item.getTimeB());
				jogoDto.setGolsTimeB(item.getGolsTimeB());
				jogoDto.setFlagTimeB(item.getFlagTimeB());
				jogoDto.setEmojiTimeB(item.getEmojiTimeB());

				faseDto.getJogos().add(jogoDto);
				jogosMap.put(item.getIdJogo(), jogoDto);
			}

			// --- palpite ---
			if (item.getIdPalpite() != null) {
				IaBolaoJogosPalpitesDto.IaPalpiteDto palpiteDto = new IaBolaoJogosPalpitesDto.IaPalpiteDto();
				palpiteDto.setIdPalpite(item.getIdPalpite());
				palpiteDto.setIdParticipante(item.getIdParticipante());
				palpiteDto.setNomeParticipante(item.getNomeParticipante());
				palpiteDto.setPontuacaoParticipante(item.getPontuacaoParticipante());
				palpiteDto.setClassificacaoParticipante(item.getClassificacaoParticipante());
				palpiteDto.setLimiteAposta(item.getLimiteAposta());
				palpiteDto.setGolsTimeA(item.getGolsPalpiteTimeA());
				palpiteDto.setGolsTimeB(item.getGolsPalpiteTimeB());
				palpiteDto.setPontuacaoAtingida(item.getPontuacaoAtingida());
				palpiteDto.setRegraPontuacao(parseRegraPontuacao(item.getRegraPontuacao()));
				jogoDto.getPalpites().add(palpiteDto);
			}
		}

		return bolaoDto;
	}

	private IaBolaoJogosPalpitesDto.IaValoresRegrasPontuacaoDto buildValoresRegrasPontuacao(IaBolaoJogosPalpitesFlat item) {
		IaBolaoJogosPalpitesDto.IaValoresRegrasPontuacaoDto dto = new IaBolaoJogosPalpitesDto.IaValoresRegrasPontuacaoDto();

		dto.setMensagens(new IaBolaoJogosPalpitesDto.IaMensagensDto());

		IaBolaoJogosPalpitesDto.IaRegraFinalDto finalDto = new IaBolaoJogosPalpitesDto.IaRegraFinalDto();
		finalDto.setAcertouCampeao(item.getFaseAcertarUmTime());
		finalDto.setBonusPorAcertarOsDoisFinalistas(item.getFaseAcertarTimes());
		finalDto.setAcertouFinalistaA(item.getFaseAcertarTimes());
		finalDto.setAcertouFinalistaB(item.getFaseAcertarTimes());
		dto.setFinalRegras(finalDto);

		IaBolaoJogosPalpitesDto.IaRegraBonusDto bonusDto = new IaBolaoJogosPalpitesDto.IaRegraBonusDto();
		bonusDto.setBonusPlacarAltoDoTimeA(item.getFasePontuacaoAcertarPlacarAlto());
		bonusDto.setBonusPlacarAltoDoTimeB(item.getFasePontuacaoAcertarPlacarAlto());
		dto.setBonus(bonusDto);

		IaBolaoJogosPalpitesDto.IaRegraResultadoDto resultadoDto = new IaBolaoJogosPalpitesDto.IaRegraResultadoDto();
		resultadoDto.setAcertouVencedor(item.getFasePontuacaoAcertarVencedor());
		resultadoDto.setAcertouEmpate(item.getFaseAcertarEmpate());
		resultadoDto.setPremioDePlacarExato(item.getFasePontuacaoAcertarPlacar());
		resultadoDto.setAcertouGolsDoTimeA(item.getFaseAcertarQtdGolsUmDosTimes());
		resultadoDto.setAcertouGolsDoTimeB(item.getFaseAcertarQtdGolsUmDosTimes());
		resultadoDto.setPontuacaoMaximaPermitida(item.getFaseMaximoPontuacaoPossivelPalpite());
		dto.setResultado(resultadoDto);

		return dto;
	}

	private Map<String, Object> parseRegraPontuacao(String regraPontuacao) {
		Map<String, Object> regra = new LinkedHashMap<>();

		if (regraPontuacao == null || regraPontuacao.trim().isEmpty()) {
			return regra;
		}

		try {
			if (regraPontuacao.trim().startsWith("{")) {
				return objectMapper.readValue(regraPontuacao, new TypeReference<Map<String, Object>>() {
				});
			}
		} catch (Exception e) {
			// Se nao for JSON valido, retorna no formato chave-valor com a mensagem crua.
		}

		regra.put("descricao", regraPontuacao);
		return regra;
	}
}