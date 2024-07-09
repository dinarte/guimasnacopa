package br.com.guimasnacopa.repository;


import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Usuario;

@Repository
public interface ParticipanteRepository  extends CrudRepository<Participante, Integer>{

	public Participante findOneByBolaoAndUsuario(Bolao b, Usuario u);
	
	public List<Participante> findAllByBolaoOrderByAdminDescUsuario_nameAsc(Bolao b);
	
	public Long countByBolao(Bolao b);
	
	@Query("select count(*) from Participante where pg is true and bolao =:bolao")
	public Long countPgByBolao(@Param("bolao") Bolao b);
	
	public List<Participante> findAllByBolaoAndPgOrderByPontuacaoDesc(Bolao b, Boolean pg);
	
	public List<Participante> findAllByBolaoOrderByClassificacaoAscExibirClassificacaoNoRankingDesc(Bolao b);
	
	public List<Participante> findTop10ByBolaoAndPgOrderByClassificacaoAscExibirClassificacaoNoRankingDesc(Bolao b, Boolean pg);
	
	@Modifying(clearAutomatically = true)
	@Query(value = " update participante set pontuacao = t.pts "
					+"		from ( "
					+"		    select participante_id, sum(coalesce(pontuacao_atingida,0)) pts "
					+"		    from palpite "
					+"		    group by participante_id "
					+"		    order by 2 desc "
					+"		) as t "
					+" where t.participante_id = participante.id "
			, nativeQuery = true)
	public void updatePontuacao();

	
	@Modifying(clearAutomatically = true)
	@Query(value = " update participante set classificacao = classi_calc " 
					+"	from "
					+"	( "
					+"	    select pontuacao as pts, row_number() OVER (PARTITION by 0) as classi_calc "
					+"	    from( "
					+"	    select distinct pontuacao " 
					+"	    from participante "
					+"	    order by pontuacao desc "
					+"	    ) as t    "
					+"	 ) as t2 "
					+"	 where pontuacao = t2.pts "
			, nativeQuery = true)
	public void updateClassificacao();

	
	@Modifying(clearAutomatically = true)
	@Query(value = ""
			+ " update participante set aproveitamento =  aprov "
			+ "from (  "
			+ "select id, " + 
			"pontuacao, " + 
			"(pontuacao * 100) / ((select count(*) from jogo " + 
			"						join fase on fase.id = jogo.fase_id " + 
			"			            where fase.bolao_id = p.bolao_id  " + 
			"                       and not exists (select * " + 
			"                                           from time_no_jogo " + 
			"                                          where jogo_id = jogo.id " + 
			"                                            and gols is null)) * 18) aprov " + 
			"from  participante p ) t "
			+ "where participante.id = t.id"
			, nativeQuery = true)
	public void updateAproveitamento();	
	
	
	@Query(value="select to_char(jogo.data, 'yyyy-mm-dd') as dia, coalesce(sum(pontuacao_atingida),0) as pontuacao \r\n" + 
			"from palpite pal\r\n" + 
			"join participante par on (par.id = pal.participante_id)\r\n" + 
			"join usuario u on u.id = par.usuario_id\r\n" + 
			"join jogo on jogo.id = pal.jogo_id\r\n" + 
			"where  par.id = :participanteId \r\n" + 
			"and to_char(jogo.data, 'yymmdd') <= to_char(now(), 'yymmdd')\r\n" + 
			"group by to_char(jogo.data, 'yyyy-mm-dd')\r\n" + 
			"order by 1" , nativeQuery = true)
	public List<Map<String, Object>> findAproveitameto(@Param("participanteId") Integer participanteId);
	
	
	@Modifying(clearAutomatically = true)
	@Query(value = "update Participante "
							+ " set pontuacao = :pontuacao, aproveitamento = :aproveitamento, classificacao = :classificacao, exibirClassificacaoNoRanking = :exibirClassificacaoNoRanking "
							+ " where id = :participanteId")
	public void updateRaking( 
			@Param("participanteId") Integer participanteId, 
			@Param("pontuacao") Double pontuacao, 
			@Param("aproveitamento") Integer aproveitamento, 
			@Param("classificacao") Integer classificacao, 
			@Param("exibirClassificacaoNoRanking") Boolean exibirClassificacaoNoRanking);
	
	
	
}
