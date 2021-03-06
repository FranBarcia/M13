package com.gamificacioc.webservices;

import com.gamificacioc.model.Curs;
import java.sql.*;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import org.apache.commons.lang.RandomStringUtils;
import com.gamificacioc.model.*;
import java.util.ArrayList;
import java.util.List;

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
    private Integer idUsuari;
    
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
    
    /**
     * Métode que donat un tipus d'usuari, retorna la llista de tots els usuaris que comparteixen tipus
     * Retorna una llista amb tots els usuaris trobats.
     * @param tipusUsuari Tipus d'usuari a consultar
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return listUsers
     */
    @WebMethod(operationName="consultarUsuaris")
    @WebResult(name="llistarUsuaris")
    public List<Usuari> consultarUsuaris(@WebParam(name="tipusUsuari") String tipusUsuari, @WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        List<Usuari> usuaris = new ArrayList<>(); 
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                query = conexio.prepareStatement("Select * from usuaris where tipus  = '"+tipusUsuari+"'");
                
                ResultSet rs = query.executeQuery();
                
                while (rs.next()) {
                    Usuari userTmp = new Usuari();
                    userTmp.setUsuari(rs.getString("usuari"));
                    userTmp.setNom(rs.getString("nom"));
                    userTmp.setCognom(rs.getString("cognom"));
                    userTmp.setEmail(rs.getString("email"));
                    usuaris.add(userTmp);
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            usuaris = null;
        }
        
        return usuaris;
    }
    
    /**
     * Métode que donat un authId d'un alumne concret
     * Retorna tota la informació d'aquest alumne
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return listUsers
     */
    @WebMethod(operationName="consultarAlumne")
    @WebResult(name="alumne")
    public Alumne consultarAlumne(@WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        Alumne alumne = new Alumne();
        List<Curs> cursos = new ArrayList<>(); 
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                query = conexio.prepareStatement("Select "
                        + "usuari, nomCurs, descripcio, puntuacio, coneixement, actitud, magia, magiaMaxima "
                        + "from ((usuaris u join connexions con on u.idUsuari = con.idUsuari) "
                        + "join alumnes a on u.idUsuari = a.idAlumne) "
                        + "join cursos c on c.idCurs = a.idCurs "
                        + "where con.authId = '"+authId+"'");
                
                ResultSet rs = query.executeQuery();
                
                while (rs.next()) {
                    Curs cursTmp = new Curs();
                    cursTmp.setNomCurs(rs.getString("nomCurs"));
                    cursTmp.setDescripcio(rs.getString("descripcio"));
                    cursos.add(cursTmp);
                }
                alumne.setLlistaCursos(cursos);
                
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            alumne = null;
        }
        
        return alumne;
    }
    
    /**
     * Métode que donat un authId i un nom d'usuari, l'elimina de la base de dades.
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @param usuari Usuari el qual es vol eliminar de la base de dades
     * @return response
     */
    @WebMethod(operationName="eliminarUsuari")
    @WebResult(name="result")
    public String eliminarUsuari(@WebParam(name="authId") String authId, @WebParam(name="usuari") String usuari) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String response = "";
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                query = conexio.prepareStatement("Delete from usuaris where usuari = '"+usuari+"'");
                
                if (query.executeUpdate() == 0) {
                    response = "User "+usuari+" not exists";
                } else {
                    response = "User "+usuari+" deleted";
                }
            }
            catch (ClassNotFoundException | SQLException ex) {
                response = "Not existing user";
            }
        }
        return response;
    }

    /**
     * Métode que donat un authId i una contrasenya nova, modifica la contrasenya del usuari autentificat i la canvia per la nova.
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @param password Nova contrasenya que l'usuari vol canviar
     * @return response
     */
    @WebMethod(operationName="canviarContrasenya")
    @WebResult(name="result")
    public String canviarContrasenya(@WebParam(name="authId") String authId, @WebParam(name="password") String password) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String response = "";
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                query = conexio.prepareStatement("Select u.idUsuari from usuaris u join connexions c on u.idUsuari = c.idUsuari where authId like '"+authId+"'");
                ResultSet rs = query.executeQuery();
                rs.next();

                if (rs.getInt("idUsuari") != 0) {
                    idUsuari = rs.getInt("idUsuari");
                    query = conexio.prepareStatement("update usuaris set contrasenya = '"+password+"' where idUsuari = '"+idUsuari+"'");
                    query.executeUpdate();
                    response = "Password changed OK";
                } 
            }
            catch (ClassNotFoundException | SQLException e) {
                response = "Password not changed";
            }
        }
        return response;
    }

    /**
     * Métode que donats diversos paràmetres crea un curs nou, amb el paràmetre 
     * authId es comprova que l'usuari que executa l'alta d'un nou usuari és valid.
     * Retorna un missatge amb el resultat de la creació del usuari.
     * @param nom Nom del curs que volem crear
     * @param descripcio Descripció del curs que volem crear
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return result
     */
    @WebMethod(operationName="altaCurs")
    @WebResult(name="result")
    public String altaCurs(@WebParam(name="nom") String nom, @WebParam(name="descripcio") String descripcio, 
                           @WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String result = "";
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                query = conexio.prepareStatement("Select idCurs, nomCurs from cursos where nomCurs = '"+nom+"'");

                ResultSet rs = query.executeQuery();
                rs.next();

                if (rs.getInt("idCurs") != 0) {
                    result = "Existing course";
                }
            }            
            catch(ClassNotFoundException | SQLException e) {
                try {
                    query = conexio.prepareStatement("Insert into cursos (nomCurs, descripcio) VALUES (?, ?)");
                    query.setString(1, nom);
                    query.setString(2, descripcio);
                    query.executeUpdate();
                    result = "Course created";
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
     * Métode que retorna la llista de tots els cursos.
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @param tipusUsuari tipus del usuari del qual volem consultar els cursos, "Alumne" o "Profe" o buit per tots els cursos.
     * @return listCourses
     */
    @WebMethod(operationName="consultarCursos")
    @WebResult(name="llistarCursos")
    public List<Curs> consultarCursos(@WebParam(name="authId") String authId, @WebParam(name="tipusUsuari") String tipusUsuari) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        List<Curs> cursos = new ArrayList<>();
        ResultSet rs;
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                
                switch (tipusUsuari) {
                    case "Profe":
                        query = conexio.prepareStatement("Select "
                        + "usuari, nomCurs, descripcio "
                        + "from ((usuaris u join connexions con on u.idUsuari = con.idUsuari) "
                        + "join profes p on u.idUsuari = p.idProfe) "
                        + "join cursos c on c.idCurs = p.idCurs "
                        + "where con.authId = '"+authId+"'");
                        
                        rs = query.executeQuery();
                
                        while (rs.next()) {
                            Curs cursTmp = new Curs();
                            cursTmp.setNomCurs(rs.getString("nomCurs"));
                            cursTmp.setDescripcio(rs.getString("descripcio"));
                            cursos.add(cursTmp);
                        }
                        
                        break;
                    case "Alumne":
                        query = conexio.prepareStatement("Select "
                        + "usuari, nomCurs, descripcio, puntuacio, coneixement, actitud, magia, magiaMaxima "
                        + "from ((usuaris u join connexions con on u.idUsuari = con.idUsuari) "
                        + "join alumnes a on u.idUsuari = a.idAlumne) "
                        + "join cursos c on c.idCurs = a.idCurs "
                        + "where con.authId = '"+authId+"'");

                        rs = query.executeQuery();
                
                        while (rs.next()) {
                            Curs cursTmp = new Curs();
                            cursTmp.setNomCurs(rs.getString("nomCurs"));
                            cursTmp.setDescripcio(rs.getString("descripcio"));
                            cursTmp.setConeixement(rs.getInt("coneixement"));
                            cursTmp.setPuntuacio(rs.getInt("puntuacio"));
                            cursTmp.setMagia(rs.getInt("magia"));
                            cursTmp.setMagiaMaxima(rs.getInt("magiaMaxima"));
                            cursos.add(cursTmp);
                        }

                        break;
                    default:
                        query = conexio.prepareStatement("Select * from cursos");
                        
                        rs = query.executeQuery();
                
                        while (rs.next()) {
                            Curs cursTmp = new Curs();
                            cursTmp.setNomCurs(rs.getString("nomCurs"));
                            cursTmp.setDescripcio(rs.getString("descripcio"));
                            cursos.add(cursTmp);
                        }
                        
                        break;
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            cursos = null;
        }
        
        return cursos;
    }

    /**
     * Métode que donats un usuari i un curs, assigna el curs al usuari, ja sigui 'Profe' o 'Alumne'
     * Retorna un missatge amb el resultat de la creació del usuari.
     * @param idUsuari Id numèric del usuari al que volem assignar un curs
     * @param idCurs Id numèric del curs que volem assignar
     * @param tipusUsuari 'Profe' o 'Alumne', és el tipus que s'ha de passar per tal de fer l'assignació
     * @param authId Codi generat en un inici de sessió correcte que autoritza al client a fer operacions contra el WebService
     * @return result
     */
    @WebMethod(operationName="assignarCurs")
    @WebResult(name="result")
    public String assignarCurs(@WebParam(name="idUsuari") Integer idUsuari, @WebParam(name="idCurs") Integer idCurs, 
                           @WebParam(name="tipusUsuari") String tipusUsuari ,@WebParam(name="authId") String authId) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        PreparedStatement query;
        String result = "";
        
        if (comprovarAuthId(authId) == true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
                
                switch (tipusUsuari) {
                    case "Profe":
                        query = conexio.prepareStatement("Select c.idCurs, nomCurs from "
                                + "cursos c join profes p on c.idCurs = p.idCurs"
                                + "where c.idCurs = '"+idCurs+"'");
                        break;
                    case "Alumne":
                        query = conexio.prepareStatement("Select c.idCurs, nomCurs from "
                                + "cursos c join alumnes a on c.idCurs = a.idCurs"
                                + "where c.idCurs = '"+idCurs+"'");
                        break;
                    default:
                        query = conexio.prepareStatement("Select idCurs, nomCurs from cursos");
                    }
                
                ResultSet rs = query.executeQuery();
                rs.next();

                if (rs.getInt("idCurs") != 0) {
                    result = "Existing assignation";
                }
            }            
            catch(ClassNotFoundException | SQLException e) {
                try {
                    query = conexio.prepareStatement("Insert into cursos (nomCurs, descripcio) VALUES (?, ?)");
                    query.setInt(1, idUsuari);
                    query.setInt(2, idCurs);
                    query.executeUpdate();
                    result = "Course assignated";
                } catch (SQLException ex) {
                    result = "Error in assignation";
                }
            }
        } else {
            result = "AuthId not valid";
        }
        
        return result;
    }

}