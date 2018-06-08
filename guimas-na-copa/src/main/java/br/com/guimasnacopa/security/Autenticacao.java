package br.com.guimasnacopa.security;

import javax.security.auth.login.LoginException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Usuario;

@Component
@Scope("session")
public class Autenticacao{

	private Usuario usuario = new Usuario();
	
	private Bolao bolao;
	
	private Participante participante;
	
	private  boolean autenticado;

	public Usuario getUsuario() {
		return usuario;
	}
	
	public void checkAdminAthorization() throws LoginException {
		if (!isAutenticado())
			throw new LoginException("Você precisa estar autenticado para acessar esta operação");
		if (getUsuario().getAdmin()!= true)
			throw new LoginException("Você precisa ser admin para acessar esta operação");
	}
	

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isAutenticado() {
		return autenticado;
	}

	public void setAutenticado(boolean autenticado) {
		this.autenticado = autenticado;
	}

	public Bolao getBolao() {
		return bolao;
	}

	public void setBolao(Bolao bolao) {
		this.bolao = bolao;
	}

	public Participante getParticipante() {
		return participante;
	}

	public void setParticipante(Participante participante) {
		this.participante = participante;
	}
	
	
	
	
}
