package br.com.guimasnacopa.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.guimasnacopa.domain.Usuario;

@Repository
public interface UserRepository  extends CrudRepository<Usuario, Integer>{

	@Query("from Usuario where email = :email and pass = :pass")
	public Usuario findOneByEmailSenha(@Param("email") String email, @Param("pass") String pass);
	
	public Usuario findByEmail(String email);
	
	
}
