package com.gamificacioc.webservices;

import java.sql.*;
import java.util.Arrays;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import org.apache.commons.lang.RandomStringUtils;

@WebService(serviceName="webservices")
public class WebServices {
    private int idUsuari;
    private String usuari;
    private String contrasenya;
    private String nom;
    private String cognom;
    private String email;
    private String adresa;
    
    public WebServices() {
    }

    @WebMethod(operationName="comprovarLogin")
    //@WebResult(name="authId")
    @WebResult(name="arrayStrings")
    public String[] comprovarLogin(@WebParam(name="usuari") String usuari, @WebParam(name="contrasenya") String contrasenya) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        Connection conexio = null;
        String[] arrayStrings = null;
        String authId = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            Statement sql = conexio.createStatement();
            String con = (new StringBuilder()).append("Select idUsuari, usuari from usuaris where usuari like '").append(usuari).append("' and contrasenya like '").append(contrasenya).append("'").toString();
            
            for(ResultSet rs = sql.executeQuery(con); rs.next();) {
                if (rs.getInt("idUsuari") != 0) {
                    authId = RandomStringUtils.randomAlphanumeric(10);
                } else {
                    authId = "NULL";
                }
            }
            arrayStrings[0] = "Hola";
            arrayStrings[1] = " mundo!";
        }
        catch(Exception e) {
            System.out.println("No s'ha completat la operacio...");
        }
        //return authId;
        return arrayStrings;
    }

    @WebMethod(operationName="insertarAlumne")
    public boolean insertarAlumne(@WebParam(name="nom") String nom, @WebParam(name="cognom") String cognom, @WebParam(name="email") String email) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        Connection conexio = null;
        boolean funciona = false;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            Statement sql = conexio.createStatement();
            String con = (new StringBuilder()).append("INSERT INTO alumnes (nom, cognom, email) VALUES ('").append(nom).append("', '").append(cognom).append("', '").append(email).append("')").toString();
            sql.executeUpdate(con);
            funciona = true;
        }
        catch(Exception e) {
            System.out.println("No s'ha completat la operacio...");
        }
        
        return funciona;
    }

    @WebMethod(operationName="buscarAlumnePerNom")
    public boolean buscarAlumnePerNom(@WebParam(name="nom") String nom) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        Connection conexio = null;
        boolean funciona = false;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            Statement sql = conexio.createStatement();
            String con = (new StringBuilder()).append("SELECT * FROM alumnes where nom = '").append(nom).append("'").toString();
            ResultSet rs = sql.executeQuery(con);
            
            if(rs.next()) {
                nom = rs.getString("nom");
                cognom = rs.getString("cognom");
                email = rs.getString("email");
                funciona = true;
                mostrarNom();
                mostrarCognom();
                mostrarEmail();
            }
        }
        catch(Exception e) {
            System.out.println("No s'ha completat la operacio...");
        }
        
        return funciona;
    }

    @WebMethod(operationName="mostrarNom")
    public String mostrarNom() {
        String localNom = "";
        localNom = nom;
        return localNom;
    }

    @WebMethod(operationName="mostrarCognom")
    public String mostrarCognom() {
        String localCognom = "";
        localCognom = cognom;
        return localCognom;
    }

    @WebMethod(operationName="mostrarEmail")
    public String mostrarEmail() {
        String localEmail = "";
        localEmail = email;
        return localEmail;
    }
}
