package com.gamificacioc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.jws.WebParam;
import org.apache.commons.lang.RandomStringUtils;
import com.gamificacioc.model.Usuari;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author fbarcia
 */
public class test {
    private static Connection conexio = null;
    
    public static Usuari comprovarLogin(@WebParam(name="usuari") String usuari, @WebParam(name="contrasenya") String contrasenya) {
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
    
    private static Boolean sessioJaIniciada(Integer idUsuari) {
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

    public static void main(String[] args) {
        Usuari resposta = comprovarLogin("fbarcia", "password");
        System.out.println("1 - Valor retornat de comprovarLogin(): "+resposta.getAuthId()+" - "+resposta.getUsuari());
        
        resposta = comprovarLogin("mmaqueda", "contra");
        System.out.println("2 - Valor retornat de comprovarLogin(): "+resposta.getAuthId()+" - "+resposta.getUsuari());
    }
}
