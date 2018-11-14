package com.gamificacioc.webservices;

import java.sql.*;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import org.apache.commons.lang.RandomStringUtils;
import com.gamificacioc.model.Usuari;

@WebService(serviceName="webservices")
public class WebServices {
    private Connection conexio = null;
    private String nom;
    private String cognom;
    private String email;
    
    @WebMethod(operationName="comprovarLogin")
    @WebResult(name="userAuthentication")
    public Usuari comprovarLogin(@WebParam(name="usuari") String usuari, @WebParam(name="contrasenya") String contrasenya) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        Usuari user = new Usuari();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Select idUsuari, usuari from usuaris where usuari like '"+usuari+"' and contrasenya like '"+contrasenya+"'");
            ResultSet rs = query.executeQuery();
            rs.next();

            if (rs.getInt("idUsuari") != 0) {
                if (sessioJaIniciada(rs.getInt("idUsuari")) == false) {
                    user.setAuthId(RandomStringUtils.randomAlphanumeric(10));
                    user.setUsuari(usuari);
                    query = conexio.prepareStatement("Insert into conexions (idUsuari, authId) VALUES (?, ?)");
                    query.setInt(1, rs.getInt("idUsuari"));
                    query.setString(2, user.getAuthId());
                    query.executeUpdate();
                } else {
                    user.setAuthId("User already connected");
                    user.setUsuari(usuari);
                }
            } 
        }
        catch (ClassNotFoundException | SQLException e) {
            user.setAuthId("INVALID");
            user.setUsuari(usuari);
        }
        
        return user;
    }

    @WebMethod(operationName="tipusUsuari")
    @WebResult(name="tipusUsuari")
    public String tipusUsuari(@WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String response = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Select u.idUsuari, tipus from usuaris u join conexions c on u.idUsuari = c.idUsuari where authId like '"+authId+"'");
            ResultSet rs = query.executeQuery();
            rs.next();

            if (rs.getInt("idUsuari") != 0) {
                if (sessioJaIniciada(rs.getInt("idUsuari")) == true) {
                    response = rs.getString("tipus");
                }
            } 
        }
        catch (ClassNotFoundException | SQLException e) {
            response = "User not connected";
        }
        
        return response;
    }

    @WebMethod(operationName="tancarSessio")
//    @WebResult(name="userLogout")
    public void tancarSessio(@WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        //String response = "";
        //Usuari user = new Usuari();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Delete from conexions where authId like '"+authId+"'");
            query.executeUpdate(); 
        }
        catch (ClassNotFoundException | SQLException e) {
            //response = "INVALID";
        }
        
        //return response;
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

    private Boolean sessioJaIniciada(Integer idUsuari) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        Boolean connectat;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Select idUsuari from conexions where idUsuari = '"+idUsuari+"'");
            
            ResultSet rs = query.executeQuery();
            rs.next();
            
            connectat = rs.getInt("idUsuari") == idUsuari;
        }
        catch (ClassNotFoundException | SQLException e) {
            connectat = false;
        }
        
        return connectat;
    }
}
