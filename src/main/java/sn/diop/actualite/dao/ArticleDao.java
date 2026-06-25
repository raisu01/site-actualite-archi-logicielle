package sn.diop.actualite.dao;

import sn.diop.actualite.model.Article;
import sn.diop.actualite.model.Categorie;
import sn.diop.actualite.model.Role;
import sn.diop.actualite.model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ArticleDao {

    private static final String SELECT_BASE =
            "SELECT a.id, a.titre, a.contenu, a.date_publication, " +
            "       c.id AS cat_id, c.nom AS cat_nom, " +
            "       u.id AS aut_id, u.login AS aut_login, u.role AS aut_role " +
            "FROM article a " +
            "JOIN categorie c ON a.categorie_id = c.id " +
            "JOIN utilisateur u ON a.auteur_id = u.id ";

    public List<Article> lister(int limite, int decalage) {
        List<Article> articles = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY a.date_publication DESC LIMIT ? OFFSET ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ps.setInt(2, decalage);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(construire(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du listage des articles", e);
        }
        return articles;
    }

    public List<Article> listerTous() {
        List<Article> articles = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY a.date_publication DESC";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                articles.add(construire(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du listage des articles", e);
        }
        return articles;
    }

    public List<Article> listerParCategorie(int categorieId) {
        List<Article> articles = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.id = ? ORDER BY a.date_publication DESC";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, categorieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(construire(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du listage des articles par categorie", e);
        }
        return articles;
    }

    public Article trouverParId(int id) {
        String sql = SELECT_BASE + "WHERE a.id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construire(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de l'article " + id, e);
        }
        return null;
    }

    public int compter() {
        String sql = "SELECT COUNT(*) FROM article";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du comptage des articles", e);
        }
        return 0;
    }

    public int creer(Article article) {
        String sql = "INSERT INTO article (titre, contenu, categorie_id, auteur_id) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, article.getTitre());
            ps.setString(2, article.getContenu());
            ps.setInt(3, article.getCategorie().getId());
            ps.setInt(4, article.getAuteur().getId());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (cles.next()) {
                    int id = cles.getInt(1);
                    article.setId(id);
                    return id;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la creation de l'article", e);
        }
        return -1;
    }

    public void modifier(Article article) {
        String sql = "UPDATE article SET titre = ?, contenu = ?, categorie_id = ? WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, article.getTitre());
            ps.setString(2, article.getContenu());
            ps.setInt(3, article.getCategorie().getId());
            ps.setInt(4, article.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la modification de l'article", e);
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM article WHERE id = ?";
        try (Connection cn = ConnexionBD.obtenir();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'article", e);
        }
    }

    private Article construire(ResultSet rs) throws Exception {
        Categorie categorie = new Categorie(rs.getInt("cat_id"), rs.getString("cat_nom"));

        Utilisateur auteur = new Utilisateur();
        auteur.setId(rs.getInt("aut_id"));
        auteur.setLogin(rs.getString("aut_login"));
        auteur.setRole(Role.valueOf(rs.getString("aut_role")));

        Article article = new Article();
        article.setId(rs.getInt("id"));
        article.setTitre(rs.getString("titre"));
        article.setContenu(rs.getString("contenu"));
        article.setDatePublication(rs.getTimestamp("date_publication").toLocalDateTime());
        article.setCategorie(categorie);
        article.setAuteur(auteur);
        return article;
    }
}
