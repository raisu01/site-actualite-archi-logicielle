package sn.actualite.dao;

import sn.actualite.model.Jeton;
import sn.actualite.model.Role;
import sn.actualite.model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JetonDao {

    private static final String SELECT_BASE =
            "SELECT j.id, j.valeur, j.date_creation, j.actif, " +
            "       u.id AS u_id, u.login AS u_login, u.role AS u_role " +
            "FROM jeton j " +
            "JOIN utilisateur u ON j.utilisateur_id = u.id ";

    public List<Jeton> listerTous() {
        List<Jeton> jetons = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY j.date_creation DESC";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jetons.add(construire(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du listage des jetons", e);
        }
        return jetons;
    }

    public Jeton trouverParValeur(String valeur) {
        String sql = SELECT_BASE + "WHERE j.valeur = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, valeur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construire(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche du jeton", e);
        }
        return null;
    }

    public int creer(Jeton jeton) {
        String sql = "INSERT INTO jeton (valeur, utilisateur_id, actif) VALUES (?, ?, ?)";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, jeton.getValeur());
            ps.setInt(2, jeton.getUtilisateur().getId());
            ps.setBoolean(3, jeton.isActif());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (cles.next()) {
                    int id = cles.getInt(1);
                    jeton.setId(id);
                    return id;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la creation du jeton", e);
        }
        return -1;
    }

    public void definirActif(int id, boolean actif) {
        String sql = "UPDATE jeton SET actif = ? WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, actif);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise a jour du jeton", e);
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM jeton WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du jeton", e);
        }
    }

    private Jeton construire(ResultSet rs) throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("u_id"));
        utilisateur.setLogin(rs.getString("u_login"));
        utilisateur.setRole(Role.valueOf(rs.getString("u_role")));

        Jeton jeton = new Jeton();
        jeton.setId(rs.getInt("id"));
        jeton.setValeur(rs.getString("valeur"));
        jeton.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        jeton.setActif(rs.getBoolean("actif"));
        jeton.setUtilisateur(utilisateur);
        return jeton;
    }
}
