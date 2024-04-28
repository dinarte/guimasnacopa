
package br.com.guimasnacopa.api.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = Schemas.API_FOOTBALL)
public class Teams {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	Long id;
	
	@ManyToOne(cascade = CascadeType.ALL) 
	private Team home;
	
	@ManyToOne(cascade = CascadeType.ALL) 
	private Team away;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }

}
