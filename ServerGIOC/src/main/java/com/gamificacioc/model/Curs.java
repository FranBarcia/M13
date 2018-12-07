package com.gamificacioc.model;

import java.util.List;

/**
 *
 * @author fbarcia
 */
public class Curs {
    private int idCurs;
    private String nomCurs;
    private String descripcio;
    private List<Curs> llistaCursos;

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

    public List<Curs> getLlistaCursos() {
        return llistaCursos;
    }

    public void setLlistaCursos(List<Curs> llistaCursos) {
        this.llistaCursos = llistaCursos;
    }
}