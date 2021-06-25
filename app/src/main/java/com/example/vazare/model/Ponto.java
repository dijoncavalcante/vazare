package com.example.vazare.model;

import java.util.Calendar;

public class Ponto {
    private long id;
    private String entrada;
    private String saidaAlmoco;
    private String entradaAlmoco;
    private String saida;
    private Calendar dataDoRegistro;
    private boolean bancoDeHoras;
    private String duracao;

    public Ponto() {
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getSaidaAlmoco() {
        return saidaAlmoco;
    }

    public void setSaidaAlmoco(String saidaAlmoco) {
        this.saidaAlmoco = saidaAlmoco;
    }

    public String getEntradaAlmoco() {
        return entradaAlmoco;
    }

    public void setEntradaAlmoco(String entradaAlmoco) {
        this.entradaAlmoco = entradaAlmoco;
    }

    public String getSaida() {
        return saida;
    }

    public void setSaida(String saida) {
        this.saida = saida;
    }

    public Calendar getDataDoRegistro() {
        return dataDoRegistro;
    }

    public void setDataDoRegistro(Calendar dataDoRegistro) {
        this.dataDoRegistro = dataDoRegistro;
    }

    public boolean isBancoDeHoras() {
        return bancoDeHoras;
    }

    public void setBancoDeHoras(boolean bancoDeHoras) {
        this.bancoDeHoras = bancoDeHoras;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

}
