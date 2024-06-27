
package br.com.guimasnacopa.api.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(schema = Schemas.API_FOOTBALL)
public class FixturesLeague {

	@Id  
	Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
    private Fixture fixture;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private League league;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private Teams teams;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private Goals goals;
	
	@ManyToOne(cascade = CascadeType.ALL)
    private Score score;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Round round;

    
    public Long getId() {
		return id;
	}
    
	public void setId(Long id) {
		this.id = id;
	}


	public Fixture getFixture() {
        return fixture;
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Teams getTeams() {
        return teams;
    }

    public void setTeams(Teams teams) {
        this.teams = teams;
    }

    public Goals getGoals() {
        return goals;
    }

    public void setGoals(Goals goals) {
        this.goals = goals;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

	public Round getRound() {
		return round;
	}

	public void setRound(Round round) {
		this.round = round;
	}

	
}
