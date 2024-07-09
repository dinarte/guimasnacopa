package br.com.guimasnacopa.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.BolaoCompeticao;
import br.com.guimasnacopa.domain.Competicao;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.Palpite;
import br.com.guimasnacopa.domain.PalpiteForProcessingVo;
import br.com.guimasnacopa.domain.Participante;

@Repository
public interface PalpiteRepository  extends CrudRepository<Palpite, Integer>{
	
	
	
	public Set<Palpite> findAllByBolaoCompeticao_bolaoAndTipoOrderByBolaoCompeticao(Bolao bolao, String tipo);
	

	@Query("select p from Palpite p "
			+ "left join p.jogo "
			+ "left join p.jogo.fase "
			+ "left join p.timeA "
			+ "left join p.timeB "
			+ "where p.participante = :participante "
			+ "order by p.jogo.fase.competicao, p.jogo.grupo, p.jogo.fase.nome ASC")
	public List<Palpite> findAllByParticipante(@Param("participante") Participante participante);
	
	@Query("select p from Palpite p "
			+ "where p.jogo.data >= :data "
			+ "and participante =:participante "
			+ "order by data asc")
	public List<Palpite> findByParticipanteOrderByData( @Param("participante") Participante participante, 
			@Param("data") LocalDateTime data, Pageable page);
	
	@Query("select p from Palpite p "
			+ "left join p.jogo j "
			+ "where p.participante =:participante order by j.fase desc")
	public List<Palpite> findAllByParticipanteOrderByGrupoAndLimiteAposta( @Param("participante") Participante p);

	@Query("from Palpite p "
			+ "inner join fetch p.participante "
			+ "inner join fetch p.jogo j "
			+ "inner join fetch p.participante.usuario "
			+ "inner join fetch j.fase f "
			+ "inner join fetch f.bolao "
			+ "where p.participante.pg is true and p.jogo = :jogo "
			+ "order by p.participante.classificacao")
	public List<Palpite> findAllByJogo(@Param("jogo") Jogo jogo);
	
	
	@Query("from Palpite p "
			+ "inner join fetch p.participante "
			+ "inner join fetch p.jogo j "
			+ "inner join fetch p.participante.usuario "
			+ "inner join fetch j.fase f "
			+ "inner join fetch f.bolao "
			+ "where p.participante.pg is true and p.jogo = :jogo "
			+ "and p.tipo = :tipo "
			+ "order by p.participante.classificacao")
	public List<Palpite> findAllByJogoAndTipo(@Param("jogo") Jogo jogo, @Param("tipo") String tipo);
	
	
	@Modifying(clearAutomatically = true)
	@Query("update Palpite set pontuacaoAtingida = :pontuacao, regraPontuacao =:regraPontuacao where id = :id")
	public void updatePontuacao(@Param("pontuacao") Double pontuacao, @Param("regraPontuacao") String regraPontuacao, @Param("id") Integer id);
	
	
	
	@Query(value="select  p.gols_timea || ' x ' || p.gols_timeb as palpite,  count(*) as qtd\r\n" + 
			"from palpite p\r\n" + 
			"join time as timeA on timeA.id = p.timea_id\r\n" + 
			"join time as timeB on timeB.id = p.timeb_id\r\n" + 
			"join time_no_jogo tmjA on tmjA.jogo_id = p.jogo_id and tmjA.time_id = timeA.id\r\n" + 
			"join time_no_jogo tmjB on tmjB.jogo_id = p.jogo_id and tmjB.time_id = timeB.id\r\n" + 
			"join jogo j on j.id = p.jogo_id\r\n" + 
			"join participante pa on pa.id = p.participante_id and pa.pg is true\r\n" + 
			"join usuario u on u.id = pa.usuario_id \r\n" + 
			"where p.jogo_id = :jogoId  "+ 
			"group by p.gols_timea || ' x ' || p.gols_timeb\r\n" + 
			"order by 2 desc", nativeQuery = true)
	public List<Map<String,Object>> findResumoByJogo(@Param("jogoId") Integer jogoId);
	
	@Query(value="select case \r\n" + 
			"		when (gols_timea = gols_timeb) then 'Empate'\r\n" + 
			"      	when (gols_timea > gols_timeb) then  timeA.nome\r\n" + 
			"       else  timeB.nome \r\n" + 
			"       end as palpite\r\n" + 
			"       , count(*) as qtd\r\n" + 
			"from palpite\r\n" + 
			"join participante on participante.id = participante_id\r\n" + 
			"join time as timeA on timeA.id = timea_id\r\n" + 
			"join time as timeB on timeB.id = timeb_id\r\n" + 
			"where jogo_id = :jogoId \r\n" + 
			"and participante.pg\r\n" + 
			"group by palpite", nativeQuery = true)
	public List<Map<String,Object>> findResumoVencedorByJogo(@Param("jogoId") Integer jogoId);
	

	public void deleteAllByParticipante_id(Integer id);


	public List<Palpite> findAllByBolaoCompeticaoAndTipoOrderByParticipante_classificacaoAscParticipante_usuario_nameAscBolaoCompeticaoAsc(
			BolaoCompeticao bolaoCompeticao,
			String acertarTimes);
	
	@Query(	nativeQuery = true,
			value="select  p.id, \r\n"
					+ "		j.id as jogoId, \r\n"
					+ "		p.participante_id as participanteId, \r\n"
					+ "		u.name,\r\n"
					+ "		u.email, \r\n"
					+ "		u.url_foto as urlFoto,\r\n"
					+ "		tipo as tipo, \r\n"
					+ "		timea_id as timeAId, \r\n"
					+ "		timeb_id as timeBId, \r\n"
					+ "		gols_timea as golsTimeA, \r\n"
					+ "		gols_timeb as golsTimeB, \r\n"
					+ "		tnja.gols as golsJogoTimeA, \r\n"
					+ "		tnjb.gols as golsJogoTimeB,\r\n"
					+ "		tnja.vencedor as vencedorJogoTimeA, \r\n"
					+ "		tnjb.vencedor as vencedorJogoTimeB,\r\n"
					+ "		f.pontuacao_acertar_vencedor as pontuacaoAcertarVencedor,\r\n"
					+ "		f.acertar_empate as pontuacaoAcertarEmpate, \r\n"
					+ "		f.pontuacao_acertar_placar as pontuacaoAcertarPlacar,\r\n"
					+ "		f.acertar_qtd_gols_um_dos_times as pontuacaoAcertarQtdGolsTime, \r\n"
					+ "		qtd_gols_considera_placar_alto as qtdGolsConsideraPlacarAlto, "
					+ "		pontuacao_acertar_placar_alto as pontuacaoAcertarPlacarAlto, "
					+ "		maximo_pontuacao_possivel_palpite as maximoPontuacaoPossivelPalpite, \r\n"
					+ "		acertar_times pontuacaoAcertarTimes,\r\n"
					+ "		acertar_um_time pontuacaoAcertarUmTime, \r\n"
					+ "		(select tnj_aa.time_id from time_no_jogo tnj_aa where tnj_aa.jogo_id = j.id order by tnj_aa.id asc limit 1) as timeAJogoId, \r\n"
					+ "		(select tnj_bb.time_id from time_no_jogo tnj_bb where tnj_bb.jogo_id = j.id order by tnj_bb.id desc limit 1) as timeBJogoId \r\n"
					+ "from palpite p\r\n"
					+ "join participante pa on pa.id = p.participante_id \r\n"
					+ "join usuario u on u.id = pa.usuario_id \r\n"
					+ "left join jogo j on j.id = p.jogo_id \r\n"
					+ "left join time_no_jogo tnja on tnja.jogo_id = j.id and tnja.time_id = p.timea_id \r\n"
					+ "left join time_no_jogo tnjb on tnjb.jogo_id = j.id and tnjb.time_id = p.timeb_id \r\n"
					+ "left join time ta on ta.id = tnja.time_id  \r\n"
					+ "left join time tb on tb.id = tnjb.time_id\r\n"
					+ "left join fase f on f.id = j.fase_id \r\n"
					+ "join bolao_competicao bc on bc.id = p.bolao_competicao_id \r\n"
					+ "where pa.pg is true\r\n"
					+ "and bc.bolao_id = :bolaoId")
	public List<PalpiteForProcessingVo> findAllForProssingByBolaoId(@Param("bolaoId") Integer bolaoId);


	public List<Palpite> findAllByBolaoCompeticao_bolaoAndBolaoCompeticao_competicaoAndTipoIn(Bolao bolao, Competicao competicao,
			String[] tipos);
	
}
