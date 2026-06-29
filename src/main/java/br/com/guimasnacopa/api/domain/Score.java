
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
public class Score {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	Long id;

	@ManyToOne(cascade = CascadeType.ALL)
    private Goals halftime;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private Goals fulltime;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private Goals extratime;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private Goals penalty;
   
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Goals getHalftime() {
		return halftime;
	}

	public void setHalftime(Goals halftime) {
		this.halftime = halftime;
	}

	public Goals getFulltime() {
		return fulltime;
	}

	public void setFulltime(Goals fulltime) {
		this.fulltime = fulltime;
	}

	public Goals getExtratime() {
		return extratime;
	}

	public void setExtratime(Goals extratime) {
		this.extratime = extratime;
	}

	public Goals getPenalty() {
		return penalty;
	}

	public void setPenalty(Goals penalty) {
		this.penalty = penalty;
	}


}
