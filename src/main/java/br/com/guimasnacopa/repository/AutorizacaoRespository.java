package br.com.guimasnacopa.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.guimasnacopa.domain.Autorizacao;

@ResponseBody
public interface AutorizacaoRespository extends CrudRepository<Autorizacao, String>{

}
