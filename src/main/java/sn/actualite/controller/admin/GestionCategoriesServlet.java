package sn.actualite.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.actualite.model.Categorie;
import sn.actualite.service.CategorieService;

import java.io.IOException;

/**
 * Back-office : gestion des catégories (lister, ajouter, modifier, supprimer).
 * Accessible aux éditeurs et administrateurs (voir util/AuthentificationFilter).
 */
@WebServlet(name = "GestionCategoriesServlet", urlPatterns = {"/admin/categories"})
public class GestionCategoriesServlet extends HttpServlet {

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
                default -> { /* action inconnue : on ne fait rien */ }
            }
        } catch (Exception e) {
            afficherListe(request, response, "Erreur : " + e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private void creer(HttpServletRequest request) {
        Categorie categorie = new Categorie();
        lireFormulaire(request, categorie);
        categorieService.creer(categorie);
    }

    private void modifier(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Categorie categorie = categorieService.trouverParId(id);
        if (categorie == null) {
            throw new IllegalArgumentException("Catégorie introuvable (id=" + id + ")");
        }
        lireFormulaire(request, categorie);
        categorieService.modifier(categorie);
    }

    private void supprimer(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            categorieService.supprimer(id);
        } catch (RuntimeException e) {
            // La contrainte de clé étrangère (articles rattachés) empêche la suppression.
            throw new IllegalStateException(
                    "Impossible de supprimer cette catégorie : des articles y sont encore rattachés.");
        }
    }

    private void lireFormulaire(HttpServletRequest request, Categorie categorie) {
        String nom = request.getParameter("nom");
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de la catégorie est obligatoire.");
        }
        categorie.setNom(nom.trim());
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response, String erreur)
            throws ServletException, IOException {
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.setAttribute("erreur", erreur);
        request.getRequestDispatcher("/WEB-INF/views/admin/categories.jsp").forward(request, response);
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {

        Categorie categorie = null;
        if ("modifier".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            categorie = categorieService.trouverParId(id);
            if (categorie == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Catégorie introuvable");
                return;
            }
        }

        request.setAttribute("action", action);
        request.setAttribute("categorieEnEdition", categorie);
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.getRequestDispatcher("/WEB-INF/views/admin/categories.jsp").forward(request, response);
    }
}
