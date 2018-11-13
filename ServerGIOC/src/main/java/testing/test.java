package testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author fbarcia
 */
public class test {
    private int id;
    private String nom;
    private String cognom;
    private String email;
    
    public static boolean insertarAlumne(String nom, String cognom, String email) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        Connection conexio = null;
        boolean funciona = false;
        String con;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            Statement s = conexio.createStatement();
            con = "INSERT INTO alumnes (nom, cognom, email) VALUES ('"+nom+"','" + cognom +"','"+ email +"')";
            s.executeUpdate(con);
            funciona = true;
        }
        catch(Exception e) {
            System.out.println("No s'ha completat la operacio...");
            System.out.println("Excepcio: "+e.getMessage());
        }
        return funciona;
    }

    public boolean buscarAlumnePerNom(String nom) {
        String conexioBD = "jdbc:mysql://localhost:3306/gamific_db?serverTimezone=UTC";
        Connection conexio = null;
        boolean funciona = false;
        String con;
        ResultSet rs;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexio = DriverManager.getConnection(conexioBD, "user_db", "gamificacioc_dbP@ss");
            Statement s = conexio.createStatement();
            con = "SELECT * FROM alumnes where nom = '" + nom + "'" ;
            rs = s.executeQuery (con);

            while (rs.next()) {
                nom = rs.getString("nom");
                cognom = rs.getString("cognom");
                email = rs.getString("email");
                funciona=true;
                mostrarNom();
                mostrarCognom();
                mostrarEmail();
                break;
            }
        }
        catch(Exception e) {
            System.out.println("No s'ha completat la operacio...");
        }
        return funciona;
    }

    public String mostrarNom() {
        String nom;
        nom = "";
        nom = this.nom;
        return nom;
    }

    public String mostrarCognom() {
        String cognom;
        cognom = "";
        cognom = this.cognom;
        return cognom;
    }

    public String mostrarEmail() {
        String email;
        email = "";
        email = this.email;
        return email;
    }
    
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        insertarAlumne("Fran", "Barcia", "fbarcia@gmail.com");
    }
}
