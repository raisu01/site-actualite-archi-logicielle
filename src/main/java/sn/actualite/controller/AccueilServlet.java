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
import java.util.ArrayList;
import java.util.List;

/**
 * Page d'accueil : bandeau "EN DIRECT", article à la une + articles en avant
 * (page 1 seulement), puis grille "Derniers articles" avec pagination.
 */
@WebServlet(name = "AccueilServlet", urlPatterns = {"/accueil"})
public class AccueilServlet extends HttpServlet {

    /** Nombre d'articles en avant à la une (en plus de l'article principal), page 1 seulement. */
    private static final int NB_EN_AVANT = 2;

    private final ArticleService articleService = new ArticleService();
    private final CategorieService categorieService = new CategorieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = lirePageDemandee(request);
        int totalPages = articleService.compterPages();
        if (page > totalPages) {
            page = totalPages;
        }

        List<Article> articlesPage = articleService.listerPage(page);

        // Sur la page 1 : le 1er article devient "à la une", les 2 suivants sont mis en avant,
        // le reste alimente la grille "Derniers articles".
        Article articleUne = null;
        List<Article> articlesEnAvant = new ArrayList<>();
        List<Article> articlesGrille;

        if (page == 1 && !articlesPage.isEmpty()) {
            articleUne = articlesPage.get(0);
            int finEnAvant = Math.min(1 + NB_EN_AVANT, articlesPage.size());
            articlesEnAvant = articlesPage.subList(1, finEnAvant);
            articlesGrille = articlesPage.subList(finEnAvant, articlesPage.size());
        } else {
            articlesGrille = articlesPage;
        }

        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null); // "Toutes" est active sur l'accueil
        request.setAttribute("articlesTicker", articleService.listerDerniers(5));

        request.setAttribute("articleUne", articleUne);
        request.setAttribute("articlesEnAvant", articlesEnAvant);
        request.setAttribute("articlesGrille", articlesGrille);

        request.setAttribute("pageCourante", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("aPagePrecedente", page > 1);
        request.setAttribute("aPageSuivante", page < totalPages);
        request.setAttribute("numerosPages", numerosDePage(totalPages));

        request.getRequestDispatcher("/WEB-INF/views/accueil.jsp").forward(request, response);
    }

    private int lirePageDemandee(HttpServletRequest request) {
        String parametrePage = request.getParameter("page");
        if (parametrePage == null) {
            return 1;
        }
        try {
            int page = Integer.parseInt(parametrePage);
            return Math.max(page, 1);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /** Liste simple [1, 2, ..., totalPages] pour afficher les boutons numérotés de pagination. */
    private List<Integer> numerosDePage(int totalPages) {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            numeros.add(i);
        }
        return numeros;
    }
}
