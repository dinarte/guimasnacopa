package br.com.guimasnacopa.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PalpitesProcessorsCache {
	
	
	private List<PalpiteProcessor> cachedPalpitesProcessors = new ArrayList<>();
	
	public void set(List<PalpiteProcessor> cachedPalpitesProcessors) {
		this.cachedPalpitesProcessors = cachedPalpitesProcessors; 
	}
	
	public List<PalpiteProcessor> load() {
		return cachedPalpitesProcessors;
	}

	public boolean notIsEmpty() {
		return !cachedPalpitesProcessors.isEmpty();
	} 

}
