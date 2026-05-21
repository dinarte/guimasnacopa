package br.com.guimasnacopa.apifootbol.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesApiDTO.TimeApiDTO;



public class CampeonatoDetalhesToJogosConverter {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<JogoApiDTO> converter(
            CampeonatoDetalhesApiDTO campeonatoDetalhes,
            String fase
    ) {

        List<JogoApiDTO> jogos = new ArrayList<>();
        Set<Long> partidasJaAdicionadas = new HashSet<>();

        if (campeonatoDetalhes == null || fase == null) {
            return jogos;
        }

        JsonNode root = mapper.valueToTree(campeonatoDetalhes);

        JsonNode faseNode = root
                .path("partidas")
                .path(fase);

        if (!faseNode.isObject()) {
            return jogos;
        }

        processarFase(
                jogos,
                partidasJaAdicionadas,
                faseNode,
                fase
        );

        return jogos;
    }

    private void processarFase(
            List<JogoApiDTO> jogos,
            Set<Long> partidasJaAdicionadas,
            JsonNode faseNode,
            String fase
    ) {

        Iterator<Map.Entry<String, JsonNode>> grupos = faseNode.fields();

        while (grupos.hasNext()) {

            Map.Entry<String, JsonNode> grupoEntry = grupos.next();

            String grupo = grupoEntry.getKey();
            JsonNode grupoNode = grupoEntry.getValue();

            if (!isGrupoValido(grupo, grupoNode)) {
                continue;
            }

            processarRodadasDoGrupo(
                    jogos,
                    partidasJaAdicionadas,
                    grupoNode,
                    fase,
                    grupo
            );
        }
    }

    private void processarRodadasDoGrupo(
            List<JogoApiDTO> jogos,
            Set<Long> partidasJaAdicionadas,
            JsonNode grupoNode,
            String fase,
            String grupo
    ) {

        Iterator<Map.Entry<String, JsonNode>> rodadas = grupoNode.fields();

        while (rodadas.hasNext()) {

            Map.Entry<String, JsonNode> rodadaEntry = rodadas.next();

            String rodada = rodadaEntry.getKey();
            JsonNode jogosNode = rodadaEntry.getValue();

            if (!isRodadaValida(rodada, jogosNode)) {
                continue;
            }

            adicionarJogos(
                    jogos,
                    partidasJaAdicionadas,
                    jogosNode,
                    fase,
                    rodada,
                    grupo
            );
        }
    }

    private void adicionarJogos(
            List<JogoApiDTO> jogos,
            Set<Long> partidasJaAdicionadas,
            JsonNode jogosNode,
            String fase,
            String rodada,
            String grupo
    ) {

        for (JsonNode jogoNode : jogosNode) {

            if (!jogoNode.hasNonNull("partida_id")) {
                continue;
            }

            Long partidaId = jogoNode.get("partida_id").asLong();

            if (partidasJaAdicionadas.contains(partidaId)) {
                continue;
            }

            JogoApiDTO jogo = mapper.convertValue(
                    jogoNode,
                    JogoApiDTO.class
            );

            jogo.setFase(fase);
            jogo.setRodada(rodada);
            jogo.setGrupo(grupo);

            jogos.add(jogo);

            partidasJaAdicionadas.add(partidaId);
        }
    }

    private boolean isGrupoValido(
            String grupo,
            JsonNode grupoNode
    ) {

        return grupo != null
                && grupo.matches("grupo-[a-z]")
                && grupoNode != null
                && grupoNode.isObject();
    }

    private boolean isRodadaValida(
            String rodada,
            JsonNode jogosNode
    ) {

        return rodada != null
                && rodada.matches("\\d+a-rodada")
                && jogosNode != null
                && jogosNode.isArray();
    }
    
    public List<TimeApiDTO> extrairTimes(List<JogoApiDTO> jogos) {

        Map<Long, TimeApiDTO> times = new LinkedHashMap<>();

        for (JogoApiDTO jogo : jogos) {

            if (jogo.getTimeMandante() != null) {

                times.putIfAbsent(
                        jogo.getTimeMandante().getTimeId(),
                        jogo.getTimeMandante()
                );
            }

            if (jogo.getTimeVisitante() != null) {

                times.putIfAbsent(
                        jogo.getTimeVisitante().getTimeId(),
                        jogo.getTimeVisitante()
                );
            }
        }

        return times.values()
                .stream()
                .collect(Collectors.toList());
    }
}