package br.com.guimasnacopa.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	private String email;
	
	private String pass;
	
	private String urlFoto;
	
	@Transient
	private String passConfirm;
	
	private String loginStrategy;
	
	@Column(length = 1500)
	private String authPayLoad;
	
	private LocalDate ultimoLogin;
	
	private Boolean admin;
	
	
	public Usuario() {}
	
	public Usuario(Integer id) {
	 this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	

	public String getLoginStrategy() {
		return loginStrategy;
	}

	public void setLoginStrategy(String loginStrategy) {
		this.loginStrategy = loginStrategy;
	}


	public String getAuthPayLoad() {
		return authPayLoad;
	}

	public void setAuthPayLoad(String authPeyLoad) {
		this.authPayLoad = authPeyLoad;
	}

	public LocalDate getUltimoLogin() {
		return ultimoLogin;
	}

	public void setUltimoLogin(LocalDate ultimoLogin) {
		this.ultimoLogin = ultimoLogin;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public String getPassConfirm() {
		return passConfirm;
	}

	public void setPassConfirm(String passConfirm) {
		this.passConfirm = passConfirm;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}
	
	
	
}
