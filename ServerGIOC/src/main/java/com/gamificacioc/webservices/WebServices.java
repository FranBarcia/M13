package com.gamificacioc.webservices;

import java.sql.*;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import org.apache.commons.lang.RandomStringUtils;
import com.gamificacioc.model.Usuari;

/**
 *
 * @author Fran Barcia
 */
@WebService(serviceName="webservices")
public class WebServices {
    private Connection conexio = null;
    private String nom;
    private String cognom;
    private String email;
    private String authId;
    
    /**
     * Métode que donats dos paràmetres, usuari i contrasenya, comprova si existeixen a la BD
     * i, en cas de que la combinació sigui correcta, inicia la sessió.
     * Retorna un codi de sessió únic.
     * @param usuari Usuari utilitzat per iniciar sessió
     * @param contrasenya Contrasenya del usuari
     * @return authId
     */
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
                authId = sessioJaIniciada(rs.getInt("idUsuari"));
                if (!authId.equals("Invalid")) {
                    tancarSessio(authId);
                }
                user.setAuthId(RandomStringUtils.randomAlphanumeric(10));
                user.setUsuari(usuari);
                query = conexio.prepareStatement("Insert into connexions (idUsuari, authId) VALUES (?, ?)");
                query.setInt(1, rs.getInt("idUsuari"));
                query.setString(2, user.getAuthId());
                query.executeUpdate();
            } 
        }
        catch (ClassNotFoundException | SQLException e) {
            user.setAuthId("Invalid");
            user.setUsuari(usuari);
        }
        
        return user;
    }

    /**
     * Métode que donat un authId retorna el tipus d'usuari al qual pertany aquesta sessió
     * "Alumne, Profe, Pare o Admin"
     * Retorna el tipus d'usuari.
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return response
     */
    @WebMethod(operationName="tipusUsuari")
    @WebResult(name="tipusUsuari")
    public String tipusUsuari(@WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String response = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Select u.idUsuari, tipus from usuaris u join connexions c on u.idUsuari = c.idUsuari where authId like '"+authId+"'");
            ResultSet rs = query.executeQuery();
            rs.next();

            if (rs.getInt("idUsuari") != 0) {
                authId = sessioJaIniciada(rs.getInt("idUsuari"));
                if (!authId.equals("Invalid")) {
                    response = rs.getString("tipus");
                }
            } 
        }
        catch (ClassNotFoundException | SQLException e) {
            response = "User not connected";
        }
        
        return response;
    }

    /**
     * Métode que donat un authId l'elimina de la BD per tal de tancar la sessió
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     */
    @WebMethod(operationName="tancarSessio")
    public void tancarSessio(@WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Delete from connexions where authId like '"+authId+"'");
            query.executeUpdate(); 
        }
        catch (ClassNotFoundException | SQLException e) {}
    }

    /**
     * Métode que donats diversos paràmetres crea un usuari nou, amb el paràmetre 
     * authId es comprova que l'usuari que executa l'alta d'un nou usuari és valid.
     * Retorna un missatge amb el resultat de la creació del usuari.
     * @param nom Nom el usuari que volem crear
     * @param cognom Cognom del usuari que volem crear
     * @param email Email del usuari, no es pot repetir
     * @param tipus Tipus d'usuari que volem crear
     * @param contrasenya Contrasenya que desitja el usuari
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return result
     */
    @WebMethod(operationName="altaUsuari")
    @WebResult(name="result")
    public String altaUsuari(@WebParam(name="nom") String nom, @WebParam(name="cognom") String cognom, 
                              @WebParam(name="email") String email, @WebParam(name="tipus") String tipus, 
                              @WebParam(name="contrasenya") String contrasenya, @WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String result = "";
        String usuari = nom.substring(0, 1).concat(String.join("", cognom.split(" "))).toLowerCase();
        Integer idUsuari;
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                query = conexio.prepareStatement("Select idUsuari, usuari, email from usuaris where usuari = '"+usuari+"' or email = '"+email+"'");

                ResultSet rs = query.executeQuery();
                rs.next();

                if (rs.getInt("idUsuari") != 0) {
                    result = "Existing user";
                }
            }            
            catch(ClassNotFoundException | SQLException e) {
                try {
                    query = conexio.prepareStatement("Insert into usuaris (usuari, contrasenya, nom, cognom, email, tipus) VALUES (?, ?, ?, ?, ?, ?)");
                    query.setString(1, usuari);
                    query.setString(2, contrasenya);
                    query.setString(3, nom);
                    query.setString(4, cognom);
                    query.setString(5, email);
                    query.setString(6, tipus);
                    query.executeUpdate();
                    result = "User created";
                    
                    query = conexio.prepareStatement("Select idUsuari, usuari from usuaris where usuari = '"+usuari+"'");
                    ResultSet rs = query.executeQuery();
                    rs.next();
                    idUsuari = rs.getInt("idUsuari");
                    
                    switch (tipus) {
                        case "Alumne":
                            query = conexio.prepareStatement("Insert into alumnes (idAlumne) VALUES (?)");
                            break;
                        case "Pare":
                            query = conexio.prepareStatement("Insert into pares (idPare) VALUES (?)");
                            break;
                        case "Profe":
                            query = conexio.prepareStatement("Insert into profes(idProfe) VALUES (?)");
                            break;
                    }
                    query.setInt(1, idUsuari);
                    query.executeUpdate();
                } catch (SQLException ex) {
                    result = "Error in creation";
                }
            }
        } else {
            result = "AuthId not valid";
        }
        
        return result;
    }

    /**
     * Métode que donat un idUsuari comprova si ja te una sessió iniciada i retorna l'authId
     * @param idUsuari ID del usuari del qual volem comprovar si la sessió existeix o no
     * @return authId
     */
    private String sessioJaIniciada(Integer idUsuari) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Select idUsuari, authId from connexions where idUsuari = '"+idUsuari+"'");
            
            ResultSet rs = query.executeQuery();
            rs.next();
            
            if (rs.getInt("idUsuari") == idUsuari) {
                authId = rs.getString("authId");
            }
        }
        catch (ClassNotFoundException | SQLException e) {
            authId = "Invalid";
        }
        
        return authId;
    }

    /**
     * Métode que donat un authId comprova si existeix a la BD, retorna un boleà 
     * en cas que existeixi o no l'authId a la BD
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return result
     */
    private Boolean comprovarAuthId(String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        Boolean result = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            query = conexio.prepareStatement("Select idUsuari, authId from connexions where authId = '"+authId+"'");
            
            ResultSet rs = query.executeQuery();
            rs.next();
            
            if (rs.getString("authId").equals(authId)) {
                result = true;
            }
        }
        catch (ClassNotFoundException | SQLException e) {
            result = false;
        }
        
        return result;
    }
}