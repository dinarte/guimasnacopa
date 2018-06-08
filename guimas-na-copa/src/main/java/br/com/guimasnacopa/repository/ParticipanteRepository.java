package br.com.guimasnacopa.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Usuario;

@Repository
public interface ParticipanteRepository  extends CrudRepository<Participante, Integer>{

	public Participante findOneByBolaoAndUsuario(Bolao b, Usuario u);
	
	public List<Participante> findAllByBolaoOrderByUsuario_nameAsc(Bolao b);
	
	public Long countByBolao(Bolao b);
	
	public List<Participante> findAllByBolaoOrderByClassificacaoDesc(Bolao b);
	
	public List<Participante> findTop10ByBolaoOrderByClassificacaoDesc(Bolao b);
}
