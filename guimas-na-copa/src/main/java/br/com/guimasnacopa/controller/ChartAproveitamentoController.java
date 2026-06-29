package br.com.guimasnacopa.controller;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

import br.com.guimasnacopa.domain.Participante;
import br.com.guimasnacopa.morrischart.MorrisChartOptions;
import br.com.guimasnacopa.repository.ParticipanteRepository;
import br.com.guimasnacopa.security.Autenticacao;

@Controller
@RequestScope
public class ChartAproveitamentoController {

	@Autowired
	ParticipanteRepository participanteRepo;
	
	@Autowired
	Autenticacao autenticacao;
	
	
	@ResponseBody()
	@RequestMapping(value = "/chart/desempenho/{participanteId}", method = RequestMethod.GET, produces = "application/json")
	public MorrisChartOptions desempenho(@PathVariable("participanteId") Integer participanteId) {
		MorrisChartOptions desempenho = new MorrisChartOptions();
		
		desempenho.setData(participanteRepo
				.findAproveitameto(participanteId));
			
		desempenho.setXkey("dia");
		desempenho.getYkeys().add("pontuacao");
		desempenho.getLabels().add("Pontuação");
		//desempenho.setDateFormat("function (x) { return new Date(x).toString(); }");
		return desempenho;
	}
	
	
	
}
