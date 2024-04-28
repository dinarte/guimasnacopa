package br.com.guimasnacopa.security;

import java.util.Objects;

import javax.security.auth.login.LoginException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.domain.Usuario;
import br.com.guimasnacopa.exception.BolaoNaoSelecionadoException;

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
		checkAthorization();
		if ( ! (getUsuario().getAdmin() || isAdminDoBolao() ) )				
			throw new LoginException("Você precisa ser admin para acessar esta operação");
			
	}
	
	public void checkBolaoNaoSelecionado() throws BolaoNaoSelecionadoException {
		if (bolao == null) throw new BolaoNaoSelecionadoException("Você precisa selecionar um bolão antes de acessar esta funcionalidade."); 
	}
	
	public void checkAdminAthorization(Model model) throws LoginException {
		checkAdminAthorization();
		model.addAttribute(this);
	}
	
	public void checkAthorization() throws LoginException {
		if (!isAutenticado())
			throw new LoginException("Você precisa estar autenticado para acessar esta operação, "
					+ "você não éfetuou login ou seu tempo de conexão expirou");
	}
	
	public Boolean isAdminDoBolao() {
		return isAutenticado() && usuario.getAdmin() || (Objects.nonNull(participante) && participante.getAdmin());
	}
	
	public Boolean isParticipanteDoBolao() {
		return isAutenticado() && Objects.nonNull(participante);
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
