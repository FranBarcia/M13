package com.gamificacioc.model;

/**
 *
 * @author fbarcia
 */
public class Curs {
    private int idCurs;
    private String nomCurs;
    private String descripcio;
    private Integer puntuacio;
    private Integer coneixement;
    private Integer magia;
    private Integer magiaMaxima;

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

    public String getNomCurs() {
        return nomCurs;
    }

    public void setNomCurs(String nomCurs) {
        this.nomCurs = nomCurs;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
    
    public Curs(){
       super();
    }

    public Curs(int idCurs, String nomCurs, String descripcio) {
        this.nomCurs = nomCurs;
        this.descripcio = descripcio;
    }

}