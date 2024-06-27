package br.com.guimasnacopa.api.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.guimasnacopa.api.domain.FixturesLeague;
import br.com.guimasnacopa.api.domain.League;
import br.com.guimasnacopa.api.domain.Round;
import br.com.guimasnacopa.api.domain.Team;
import br.com.guimasnacopa.domain.Bolao;
import br.com.guimasnacopa.domain.Competicao;
import br.com.guimasnacopa.domain.Fase;
import br.com.guimasnacopa.domain.Jogo;
import br.com.guimasnacopa.domain.LogMigracaoApi;
import br.com.guimasnacopa.domain.Time;
import br.com.guimasnacopa.domain.TimeNoJogo;

public class Mapper {
	
	public static class MappingData {
		
		private Bolao bolao;
		private Object source;
		private Object destination;
		private List<LogMigracaoApi> logMigracaoApi;
	
		
		public Bolao getBolao() {
			return bolao;
		}
		public void setBolao(Bolao bolao) {
			this.bolao = bolao;
		}
		public Object getSource() {
			return source;
		}
		public void setSource(Object obj) {
			this.source = obj;
		}
		public Object getDestination() {
			return destination;
		}
		public void setDestination(Object destination) {
			this.destination = destination;
		}
		
		
		public List<LogMigracaoApi> getLogMigracaoApi() {
			return logMigracaoApi;
		}
		public void setLogMigracaoApi(List<LogMigracaoApi> logMigracaoApi) {
			this.logMigracaoApi = logMigracaoApi;
		}
		public static MappingData getInstance(Bolao bolao, Object source) {
			MappingData data =  new MappingData();
			data.setBolao(bolao);
			data.setSource(source);
			return data;
		}
		
	}	
	
	
	public static Competicao leagueToCompeticao(MappingData data) {
		
		League league = (League) data.getSource();
		
		Competicao competicao = new Competicao();
		competicao.setIdApi(league.getId());
		competicao.setNome(league.getName());
		competicao.setSessao(league.getSeason());
		competicao.setNomeAmigavel(league.getName());
		competicao.setLogoUrl(league.getLogo());
		
		data.setDestination(competicao);
		return competicao;
	}
	
	public static Fase roundToFase(MappingData data) {
		Round round = (Round) data.getSource();
		Bolao bolao = data.getBolao();
		List<LogMigracaoApi> logMigracaoApi = data.getLogMigracaoApi();
		
		Integer idCompeticao = getIdFromLog(round.getLeague().getId(), logMigracaoApi, Competicao.class);
		
		Competicao competicao = new Competicao();
		competicao.setId(idCompeticao);
		
		Fase fase = new Fase();
		fase.setIdApi(round.getId());
		fase.setNome(round.getName());
		fase.setCompeticao(competicao);
		fase.setBolao(bolao);
	
		data.setDestination(fase);
		return fase;
	}

	private static Integer getIdFromLog(Long idApi, List<LogMigracaoApi> logMigracaoApi, Class<?> clazz) {
		Integer idCompeticao = logMigracaoApi.stream().filter(log ->{
			return log.getDestination().equals(clazz.getName()) && log.getIdSource().equals(idApi); 
		})
		.findFirst()
		.get()
		.getIdDestination();
		return idCompeticao;
	}
	
	public static Time teamToTime(MappingData data) {
		Team team = (Team) data.getSource();
		
		Time time = new Time();
		time.setIdApi(team.getId());
		time.setFlag(team.getLogo());
		time.setNome(team.getName());
		
		data.setDestination(time);
		return time;
	}
	
	
	public static Jogo fixturesLeagueToJogo(MappingData data) {
		FixturesLeague fixturesLeague = (FixturesLeague) data.getSource();
		Bolao bolao = data.getBolao();
		List<LogMigracaoApi> logMigracaoApi = data.getLogMigracaoApi();
		
		Integer idFase = getIdFromLog(fixturesLeague.getRound().getId(), logMigracaoApi, Fase.class);
		Fase fase = new Fase();
		fase.setId(idFase);
		
		
		Jogo jogo = new Jogo();
		List<TimeNoJogo> timesNoJogo = new ArrayList<>();
		Integer idHome = getIdFromLog(fixturesLeague.getTeams().getHome().getId(), logMigracaoApi, Time.class);
		Time home = new Time();
		home.setId(idHome);
				
		TimeNoJogo homeTnj = new TimeNoJogo();
		homeTnj.setJogo(jogo);
		homeTnj.setTime(home);
		timesNoJogo.add(homeTnj);
		
		Integer idAway = getIdFromLog(fixturesLeague.getTeams().getAway().getId(), logMigracaoApi, Time.class);
		Time away = new Time();
		away.setId(idAway);
		TimeNoJogo awayTnj = new TimeNoJogo();
		awayTnj.setJogo(jogo);
		awayTnj.setTime(away);
		timesNoJogo.add(awayTnj);
		
		Date dataLocal = fixturesLeague.getFixture().getDate();
		
		LocalDateTime localDateTime = LocalDateTime.ofInstant(
	            dataLocal.toInstant(), 
	            ZoneId.systemDefault()
	        );
		
		LocalDateTime limiteAposta = localDateTime.minusHours(1);
		
		jogo.setIdApi(fixturesLeague.getId());
		jogo.setData(localDateTime);
		jogo.setLimiteAposta(limiteAposta);
		jogo.setFase(fase);
		jogo.setGrupo("A Definir");
		jogo.setLiberarCriacaoPalpites(true);
		jogo.setTimesNoJogo( timesNoJogo );
		
		data.setDestination(jogo);
		return jogo;
	}

}
