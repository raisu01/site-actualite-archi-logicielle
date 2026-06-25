package sn.actualite.dao;

import sn.actualite.model.Role;
import sn.actualite.model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDao {

    public List<Utilisateur> listerTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT id, login, mot_de_passe, role FROM utilisateur ORDER BY login";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                utilisateurs.add(construire(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du listage des utilisateurs", e);
        }
        return utilisateurs;
    }

    public Utilisateur trouverParId(int id) {
        return trouverPar("id = ?", id);
    }

    public Utilisateur trouverParLogin(String login) {
        String sql = "SELECT id, login, mot_de_passe, role FROM utilisateur WHERE login = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construire(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur " + login, e);
        }
        return null;
    }

    public int creer(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (login, mot_de_passe, role) VALUES (?, ?, ?)";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utilisateur.getLogin());
            ps.setString(2, utilisateur.getMotDePasse());
            ps.setString(3, utilisateur.getRole().name());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (cles.next()) {
                    int id = cles.getInt(1);
                    utilisateur.setId(id);
                    return id;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la creation de l'utilisateur", e);
        }
        return -1;
    }

    public void modifier(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET login = ?, mot_de_passe = ?, role = ? WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, utilisateur.getLogin());
            ps.setString(2, utilisateur.getMotDePasse());
            ps.setString(3, utilisateur.getRole().name());
            ps.setInt(4, utilisateur.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la modification de l'utilisateur", e);
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    private Utilisateur trouverPar(String condition, int valeur) {
        String sql = "SELECT id, login, mot_de_passe, role FROM utilisateur WHERE " + condition;
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, valeur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construire(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur", e);
        }
        return null;
    }

    private Utilisateur construire(ResultSet rs) throws Exception {
        return new Utilisateur(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("mot_de_passe"),
                Role.valueOf(rs.getString("role")));
    }
}
