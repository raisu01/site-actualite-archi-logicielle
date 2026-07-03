package sn.actualite.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.actualite.model.Article;
import sn.actualite.model.Categorie;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.ArticleService;
import sn.actualite.service.CategorieService;

import java.io.IOException;

/**
 * Back-office : gestion des articles (lister, ajouter, modifier, supprimer).
 * Accessible aux éditeurs et administrateurs (voir util/AuthentificationFilter).
 */
@WebServlet(name = "GestionArticlesServlet", urlPatterns = {"/admin/articles"})
public class GestionArticlesServlet extends HttpServlet {

    private final ArticleService articleService = new ArticleService();
    private final CategorieService categorieService = new CategorieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("modifier".equals(action) || "creer".equals(action)) {
            afficherFormulaire(request, response, action);
            return;
        }

        afficherListe(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action == null ? "" : action) {
                case "creer" -> creer(request);
                case "modifier" -> modifier(request);
                case "supprimer" -> supprimer(request);
                default -> { /* action inconnue : on ne fait rien, on réaffiche la liste */ }
            }
        } catch (Exception e) {
            afficherListe(request, response, "Erreur : " + e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/articles");
    }

    private void creer(HttpServletRequest request) {
        Article article = lireFormulaire(request, new Article());

        HttpSession session = request.getSession(false);
        Utilisateur auteur = (Utilisateur) session.getAttribute("utilisateur");
        article.setAuteur(auteur);

        articleService.creer(article);
    }

    private void modifier(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Article article = articleService.trouverParId(id);
        if (article == null) {
            throw new IllegalArgumentException("Article introuvable (id=" + id + ")");
        }
        lireFormulaire(request, article);
        articleService.modifier(article);
    }

    private void supprimer(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        articleService.supprimer(id);
    }

    /** Remplit un Article à partir des champs du formulaire (titre, contenu, catégorie). */
    private Article lireFormulaire(HttpServletRequest request, Article article) {
        String titre = request.getParameter("titre");
        String contenu = request.getParameter("contenu");
        int categorieId = Integer.parseInt(request.getParameter("categorieId"));

        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("Le contenu est obligatoire.");
        }

        Categorie categorie = categorieService.trouverParId(categorieId);
        if (categorie == null) {
            throw new IllegalArgumentException("Catégorie invalide.");
        }

        article.setTitre(titre.trim());
        article.setContenu(contenu.trim());
        article.setCategorie(categorie);
        return article;
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response, String erreur)
            throws ServletException, IOException {
        request.setAttribute("articles", articleService.listerTous());
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.setAttribute("erreur", erreur);
        request.getRequestDispatcher("/WEB-INF/views/admin/articles.jsp").forward(request, response);
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {

        Article article = null;
        if ("modifier".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            article = articleService.trouverParId(id);
            if (article == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article introuvable");
                return;
            }
        }

        request.setAttribute("action", action);
        request.setAttribute("article", article);
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.getRequestDispatcher("/WEB-INF/views/admin/articles.jsp").forward(request, response);
    }
}
