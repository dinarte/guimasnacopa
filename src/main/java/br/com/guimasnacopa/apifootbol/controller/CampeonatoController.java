package br.com.guimasnacopa.apifootbol.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDTO;
import br.com.guimasnacopa.apifootbol.dto.CampeonatoDetalhesApiDTO;
import br.com.guimasnacopa.apifootbol.dto.FaseApiDTO;
import br.com.guimasnacopa.apifootbol.service.CampeonatoDetalhesApiService;
import br.com.guimasnacopa.apifootbol.service.CampeonatoApiService;
import br.com.guimasnacopa.apifootbol.service.FaseApiService;

@RestController
public class CampeonatoController {

    private final CampeonatoApiService campeonatoService;
    private final CampeonatoDetalhesApiService campeonatoDetalhesService;
    private final FaseApiService faseService;

    public CampeonatoController(CampeonatoApiService campeonatoService, 
    		CampeonatoDetalhesApiService campeonatoDetalhesService,
    		FaseApiService faseService) {
        this.campeonatoService = campeonatoService;
        this.campeonatoDetalhesService = campeonatoDetalhesService;
        this.faseService = faseService;
    }

    @GetMapping("/api-footbol/campeonatos")
    public List<CampeonatoDTO> campeonatos() {
        return campeonatoService.getAll();
    }
    
    @GetMapping("/api-footbol/campeonatos/{id}")
    public CampeonatoDTO campeonatos(@PathVariable("id") Long id) {
        return campeonatoService.getById(id);
    }
    
    @GetMapping("/api-footbol/campeonatos/{slug}/slug")
    public CampeonatoDTO campeonatos(@PathVariable("slug") String slug) {
        return campeonatoService.getBySlug(slug);
    }
    
    @GetMapping("/api-footbol/campeonatos/{id}/detalhes")
    public CampeonatoDetalhesApiDTO detalhes(@PathVariable("id") Long id) {
        return campeonatoDetalhesService.getByCampeonatoId(id);
    }
    
    @GetMapping("/api-footbol/campeonatos/{campeonatoId}/fases")
    public List<FaseApiDTO> fases(@PathVariable("campeonatoId") Long campeonatoId) {
        return faseService.getAllByCampeonatoId(campeonatoId);
    }
    
    @GetMapping("/api-footbol/campeonatos/{campeonatoId}/fases/{faseId}")
    public FaseApiDTO fase(@PathVariable("campeonatoId") Long campeonatoId, 
    		@PathVariable("faseId") Long faseId) {
        return faseService.getById(faseId);
    }
    
    
}