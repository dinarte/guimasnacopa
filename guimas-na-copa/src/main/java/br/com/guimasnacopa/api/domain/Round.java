package br.com.guimasnacopa.api.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = Schemas.API_FOOTBALL)
public class Round {

	@Id
	private Long id;
	
	private String name;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private League league;
	
	
	public Round() {
		super();
	}

	
	public Round(String name, League league) {
		super();
		this.league = league;
		this.name = name;
		this.id = gerarNumeroUnico(name + " | " + league.getId());
		
	}

	
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}
	
	



	public League getLeague() {
		return league;
	}


	public void setLeague(League league) {
		this.league = league;
	}


	private long gerarNumeroUnico(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes());
            // Convertendo o hash em um número Long
            long numero = 0;
            for (int i = 0; i < Math.min(hash.length, 8); i++) {
                numero += (long) (hash[i] & 0xFF) << (8 * i);
            }
            return numero;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0; // ou outra ação apropriada em caso de erro
        }
    }
}
