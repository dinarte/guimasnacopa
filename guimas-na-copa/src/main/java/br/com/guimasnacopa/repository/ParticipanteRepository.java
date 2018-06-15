package br.com.guimasnacopa.repository;


import java.util.List;

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
	
	public List<Participante> findAllByBolaoOrderByUsuario_nameAsc(Bolao b);
	
	public Long countByBolao(Bolao b);
	
	@Query("select count(*) from Participante where pg is true and bolao =:bolao")
	public Long countPgByBolao(@Param("bolao") Bolao b);
	
	public List<Participante> findAllByBolaoOrderByClassificacaoAsc(Bolao b);
	
	public List<Participante> findTop10ByBolaoOrderByClassificacaoAsc(Bolao b);
	
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
	
	
	
}
