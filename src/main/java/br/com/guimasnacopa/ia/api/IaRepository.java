package br.com.guimasnacopa.ia.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;

@Repository
public interface IaRepository extends JpaRepository<Bolao, Integer> {
	
	@Query("select p.bolao.id as bolaoId, "
			+ "p.bolao.nome as bolaoNome, "
			+ "p.bolao.permalink as bolaoSlug, "
			+ "p.id as participanteId, "
			+ "p.usuario.name as participanteNome, "
			+ "p.classificacao as classificacao, "
			+ "p.pontuacao as pontuacao, "
			+ "p.aproveitamento as aproveitamento "
			+ "from Participante p where p.bolao.id = :bolaoId "
			+ "order by p.classificacao")
	List<IaRanking> getRanking(@Param("bolaoId") Integer bolaoId);
	
	@Query("select id as id, nome as nome, permalink as slug from Bolao")
	List<IaBolao> listAllBolao();

	@Query(value = "select x.data as data, "
			+ "       x.participanteId as participanteId, "
			+ "       x.participanteNome as participanteNome, "
			+ "       x.pontuacao as pontuacao, "
			+ "       sum(x.pontuacao) over (partition by x.participanteId order by x.data rows between unbounded preceding and current row) as pontuacaoAcumulada "
			+ "  from ( "
			+ "        select to_char(j.data, 'YYYY-MM-DD') as data, "
			+ "               p.participante_id as participanteId, "
			+ "               u.name as participanteNome, "
			+ "               sum(coalesce(p.pontuacao_atingida, 0)) as pontuacao "
			+ "          from palpite p "
			+ "          join jogo j on j.id = p.jogo_id "
			+ "          join participante part on part.id = p.participante_id "
			+ "          join usuario u on u.id = part.usuario_id "
			+ "          join bolao_competicao bc on bc.id = p.bolao_competicao_id "
			+ "         where bc.bolao_id = :bolaoId "
			+ "           and p.pontuacao_atingida is not null "
			+ "         group by to_char(j.data, 'YYYY-MM-DD'), p.participante_id, u.name "
			+ "       ) x "
			+ " order by x.participanteNome, x.data",
		nativeQuery = true)
	List<IaPontuacaoPorDia> getPontuacaoPorDia(@Param("bolaoId") Integer bolaoId);

	@Query(value = "select b.id as idBolao, "
			+ "       b.nome as nomeBolao, "
			+ "       b.permalink as slugBolao, "
			+ "       c.id as idCompeticao, "
			+ "       c.nome as nomeCompeticao, "
			+ "       f.id as idFase, "
			+ "       f.nome as faseNome, "
			+ "       f.fase_final as faseFinal, "
			+ "       f.pontuacao_acertar_vencedor as fasePontuacaoAcertarVencedor, "
			+ "       f.acertar_empate as faseAcertarEmpate, "
			+ "       f.pontuacao_acertar_placar as fasePontuacaoAcertarPlacar, "
			+ "       f.acertar_qtd_gols_um_dos_times as faseAcertarQtdGolsUmDosTimes, "
			+ "       f.pontuacao_acertar_placar_alto as fasePontuacaoAcertarPlacarAlto, "
			+ "       f.maximo_pontuacao_possivel_palpite as faseMaximoPontuacaoPossivelPalpite, "
			+ "       f.acertar_um_time as faseAcertarUmTime, "
			+ "       f.acertar_times as faseAcertarTimes, "
			+ "       j.id as idJogo, "
			+ "       j.grupo as grupo, "
			+ "       cast(j.rodada as varchar) as rodada, "
			+ "       j.execucao as execucao, "
			+ "       ta.id as idTimeA, "
			+ "       ta.nome as timeA, "
			+ "       ta.flag as flagTimeA, "
			+ "       ta.emoji as emojiTimeA, "
			+ "       tnja.gols as golsTimeA, "
			+ "       tb.id as idTimeB, "
			+ "       tb.nome as timeB, "
			+ "       tnjb.gols as golsTimeB, "
			+ "       tb.flag as flagTimeB, "
			+ "       tb.emoji as emojiTimeB, "
			+ "       p.id as idPalpite, "
			+ "       pa.id as idParticipante, "
			+ "       u.name as nomeParticipante, "
			+ "       pa.pontuacao as pontuacaoParticipante, "
			+ "       pa.classificacao as classificacaoParticipante, "
			+ "       cast(p.limite_aposta as varchar) as limiteAposta, "
			+ "       p.gols_timea as golsPalpiteTimeA, "
			+ "       p.gols_timeb as golsPalpiteTimeB, "
			+ "       p.pontuacao_atingida as pontuacaoAtingida, "
			+ "       p.regra_pontuacao as regraPontuacao "
			+ "  from bolao b "
			+ "  join fase f on f.bolao_id = b.id "
			+ "  join competicao c on c.id = f.competicao_id "
			+ "  join jogo j on j.fase_id = f.id "
			+ "  join time_no_jogo tnja on tnja.id = (select min(tj.id) from time_no_jogo tj where tj.jogo_id = j.id) "
			+ "  join time_no_jogo tnjb on tnjb.id = (select max(tj.id) from time_no_jogo tj where tj.jogo_id = j.id) "
			+ "  join time ta on ta.id = tnja.time_id "
			+ "  join time tb on tb.id = tnjb.time_id "
			+ "  left join palpite p on p.jogo_id = j.id "
			+ "                     and p.tipo = 'Resultado' "
			+ "                     and exists (select 1 from participante ppg where ppg.id = p.participante_id and ppg.pg is true) "
			+ "  left join participante pa on pa.id = p.participante_id "
			+ "  left join usuario u on u.id = pa.usuario_id "
			+ " where b.id = :bolaoId "
			+ " order by c.nome, f.ordinal, f.id, j.data, j.id, pa.classificacao, u.name ", nativeQuery = true)
	List<IaBolaoJogosPalpitesFlat> getJogosComResultadosEPalpites(@Param("bolaoId") Integer bolaoId);

}
