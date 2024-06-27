package br.com.guimasnacopa.domain;

public interface PalpiteForProcessingVo {

    Long getId();
    
    Long getJogoId();

    Long getParticipanteId();
    
    String getName();
    
    String getEmail();
    
    String getUrlFoto();

    String getTipo();

    Long getTimeAId();

    Long getTimeBId();

    Long getGolsTimeA();

    Long getGolsTimeB();

    Long getGolsJogoTimeA();

    Long getGolsJogoTimeB();

    Double getPontuacaoAcertarVencedor();

    Double getPontuacaoAcertarEmpate();

    Double getPontuacaoAcertarPlacar();

    Double getPontuacaoAcertarQtdGolsTime();
}