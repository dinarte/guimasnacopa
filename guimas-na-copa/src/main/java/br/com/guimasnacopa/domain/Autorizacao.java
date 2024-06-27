package br.com.guimasnacopa.domain;

import java.beans.Transient;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Autorizacao {

	@Id
	private String id;
	
	@ManyToOne
	private Usuario usuario;
	private LocalDateTime dateTime;
	private String dispositivo;
	private String ip;
	private LocalDateTime dataTimeDisconnected;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public String getDispositivo() {
		return dispositivo;
	}
	public void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public LocalDateTime getDataTimeDisconnected() {
		return dataTimeDisconnected;
	}
	public void setDataTimeDisconnected(LocalDateTime dataTimeDisconnected) {
		this.dataTimeDisconnected = dataTimeDisconnected;
	}
	
	@Transient
	public boolean isConnected() {
		return getDataTimeDisconnected() == null;
	}
	
}
