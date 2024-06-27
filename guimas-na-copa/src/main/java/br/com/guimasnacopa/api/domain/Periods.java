
package br.com.guimasnacopa.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = Schemas.API_FOOTBALL)
public class Periods {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	Long id;

    private Long first;
    private Long second;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFirst() {
		return first;
	}
	public void setFirst(Long first) {
		this.first = first;
	}
	public Long getSecond() {
		return second;
	}
	public void setSecond(Long second) {
		this.second = second;
	}



}
