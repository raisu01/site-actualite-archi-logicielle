package sn.diop.actualite.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/actualite?useSSL=false&serverTimezone=UTC";
    private static final String UTILISATEUR = "root";
    private static final String MOT_DE_PASSE = "";

    public static Connection obtenir() {
        try {
            return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de se connecter a la base de donnees", e);
        }
    }
}
