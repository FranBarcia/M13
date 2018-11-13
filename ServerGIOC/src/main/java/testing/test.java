package testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.jws.WebParam;
import org.apache.commons.lang.RandomStringUtils;
import com.gamificacioc.model.Usuari;

/**
 *
 * @author fbarcia
 */
public class test {
    private static Usuari user = new Usuari();
    private int id;
    private String nom;
    private String cognom;
    private String email;
    
    public static Usuari comprovarLogin(@WebParam(name="usuari") String usuari, @WebParam(name="contrasenya") String contrasenya) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        Connection conexio = null;
        //String[] arrayStrings = new String[2];
        String authId = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            Statement sql = conexio.createStatement();
            String con = (new StringBuilder()).append("Select idUsuari, usuari from usuaris where usuari like '").append(usuari).append("' and contrasenya like '").append(contrasenya).append("'").toString();
            
            for(ResultSet rs = sql.executeQuery(con); rs.next();) {
                System.out.println("ResultSet: "+rs.getString("usuari"));
                if (rs.getInt("idUsuari") != 0) {
                    user.setAuthId(RandomStringUtils.randomAlphanumeric(10));
                    user.setUsuari(rs.getString("usuari"));
                    user.setIdUsuari(rs.getInt("idUsuari"));
                } else {
                    authId = "NULL";
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("No s'ha completat la operacio...");
        }
        //return authId;
        
        //arrayStrings[0] = authId;
        //arrayStrings[1] = usuari;
        
        //return arrayStrings;
        return user;
    }
    
    public static void main(String[] args) {
        Usuari resposta = comprovarLogin("fbarcia", "password");
        System.out.println("Valor retornat de comprovarLogin(): "+resposta.getAuthId()+" - "+resposta.getUsuari()+" - "+resposta.getIdUsuari());
    }
}
