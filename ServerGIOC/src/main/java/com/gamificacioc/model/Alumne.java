package com.gamificacioc.model;

import java.util.List;

/**
 *
 * @author franbarcia
 */
public class Alumne extends Usuari {
    private Integer puntuacio;
    private Integer coneixement;
    private Integer actitud;
    private Integer magia;
    private Integer magiaMaxima;
    private List<Curs> llistaCursos;

    public List<Curs> getLlistaCursos() {
        return llistaCursos;
    }

    public void setLlistaCursos(List<Curs> llistaCursos) {
        this.llistaCursos = llistaCursos;
    }

    public Integer getPuntuacio() {
        return puntuacio;
    }

    public void setPuntuacio(Integer puntuacio) {
        this.puntuacio = puntuacio;
    }

    public Integer getConeixement() {
        return coneixement;
    }

    public void setConeixement(Integer coneixement) {
        this.coneixement = coneixement;
    }

    public Integer getActitud() {
        return actitud;
    }

    public void setActitud(Integer actitud) {
        this.actitud = actitud;
    }

    public Integer getMagia() {
        return magia;
    }

    public void setMagia(Integer magia) {
        this.magia = magia;
    }

    public Integer getMagiaMaxima() {
        return magiaMaxima;
    }

    public void setMagiaMaxima(Integer magiaMaxima) {
        this.magiaMaxima = magiaMaxima;
    }
    
    
}
