package sn.actualite.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.actualite.model.Article;
import sn.actualite.model.Categorie;
import sn.actualite.service.ArticleService;
import sn.actualite.service.CategorieService;

import java.io.IOException;
import java.util.List;

/**
 * Consultation des articles filtrés par catégorie (clic sur une pilule de nav,
 * ou sur le nom de catégorie depuis un article).
 */
@WebServlet(name = "CategorieServlet", urlPatterns = {"/categorie"})
public class CategorieServlet extends HttpServlet {

    private final ArticleService articleService = new ArticleService();
    private final CategorieService categorieService = new CategorieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = lireIdDemande(request);
        if (id == -1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de catégorie invalide");
            return;
        }

        Categorie categorie = categorieService.trouverParId(id);
        if (categorie == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Catégorie introuvable");
            return;
        }

        List<Article> articles = articleService.listerParCategorie(id);

        // Données pour l'en-tête (nav catégories + onglet actif = catégorie consultée)
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", id);
        request.setAttribute("articlesTicker", articleService.listerDerniers(5));

        request.setAttribute("categorie", categorie);
        request.setAttribute("articles", articles);

        request.getRequestDispatcher("/WEB-INF/views/categorie.jsp").forward(request, response);
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
