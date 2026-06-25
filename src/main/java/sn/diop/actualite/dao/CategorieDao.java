package sn.diop.actualite.dao;

import sn.diop.actualite.model.Categorie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategorieDao {

    public List<Categorie> listerToutes() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT id, nom FROM categorie ORDER BY nom";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(construire(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du listage des categories", e);
        }
        return categories;
    }

    public Categorie trouverParId(int id) {
        String sql = "SELECT id, nom FROM categorie WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construire(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de la categorie " + id, e);
        }
        return null;
    }

    public int creer(Categorie categorie) {
        String sql = "INSERT INTO categorie (nom) VALUES (?)";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorie.getNom());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (cles.next()) {
                    int id = cles.getInt(1);
                    categorie.setId(id);
                    return id;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la creation de la categorie", e);
        }
        return -1;
    }

    public void modifier(Categorie categorie) {
        String sql = "UPDATE categorie SET nom = ? WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, categorie.getNom());
            ps.setInt(2, categorie.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la modification de la categorie", e);
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM categorie WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de la categorie", e);
        }
    }

    private Categorie construire(ResultSet rs) throws Exception {
        return new Categorie(rs.getInt("id"), rs.getString("nom"));
    }
}
