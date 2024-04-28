
package br.com.guimasnacopa.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = Schemas.API_FOOTBALL)
public class Goals {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
    
	private Long home;
    private Long away;
 

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHome() {
        return home;
    }

    public void setHome(Long home) {
        this.home = home;
    }

    public Long getAway() {
        return away;
    }

    public void setAway(Long away) {
        this.away = away;
    }

}
