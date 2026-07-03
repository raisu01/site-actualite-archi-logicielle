package sn.actualite.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.actualite.model.Article;
import sn.actualite.service.ArticleService;
import sn.actualite.service.CategorieService;

import java.io.IOException;

/**
 * Consultation détaillée d'un article (clic sur le titre depuis l'accueil,
 * la page catégorie, ou le bandeau EN DIRECT).
 */
@WebServlet(name = "ArticleServlet", urlPatterns = {"/article"})
public class ArticleServlet extends HttpServlet {

    private final ArticleService articleService = new ArticleService();
    private final CategorieService categorieService = new CategorieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = lireIdDemande(request);
        if (id == -1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant d'article invalide");
            return;
        }

        Article article = articleService.trouverParId(id);
        if (article == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article introuvable");
            return;
        }

        // Données pour l'en-tête (nav catégories + onglet actif = catégorie de l'article)
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", article.getCategorie().getId());
        request.setAttribute("articlesTicker", articleService.listerDerniers(5));

        request.setAttribute("article", article);

        request.getRequestDispatcher("/WEB-INF/views/article-detail.jsp").forward(request, response);
    }

    private int lireIdDemande(HttpServletRequest request) {
        String parametreId = request.getParameter("id");
        if (parametreId == null) {
            return -1;
        }
        try {
            return Integer.parseInt(parametreId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
