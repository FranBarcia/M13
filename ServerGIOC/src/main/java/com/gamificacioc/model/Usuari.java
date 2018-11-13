package com.gamificacioc.model;

import java.util.List;

/**
 *
 * @author fbarcia
 */
public class Usuari {
    private int idUsuari;
    private String usuari;
    private String contrasenya;
    private String nom;
    private String cognom;
    private String email;
    private String adresa;
    private List<Usuari> llistaUsuaris;
    
    public Usuari(){
       super();
    }

    public Usuari(int idUsuari, String usuari, String contrasenya, String nom, String cognom, String email, String adresa) {
        this.idUsuari = idUsuari;
        this.usuari = usuari;
        this.contrasenya = contrasenya;
        this.nom = nom;
        this.cognom = cognom;
        this.email = email;
        this.adresa = adresa;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public int getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(int idUsuari) {
        this.idUsuari = idUsuari;
    }

    public String getUsuari() {
        return usuari;
    }

    public void setUsuari(String usuari) {
        this.usuari = usuari;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognom() {
        return cognom;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Usuari> getLlistaUsuaris() {
        return llistaUsuaris;
    }

    public void setLlistaUsuaris(List<Usuari> llistaUsuaris) {
        this.llistaUsuaris = llistaUsuaris;
    }
}