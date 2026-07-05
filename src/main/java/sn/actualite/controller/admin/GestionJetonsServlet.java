package sn.actualite.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.actualite.service.AuthService;
import sn.actualite.service.CategorieService;
import sn.actualite.service.UtilisateurService;

import java.io.IOException;

/**
 * Back-office : génération et révocation des jetons d'accès aux services web
 * SOAP (voir util/JetonFilter). Réservé aux administrateurs (voir
 * util/AuthentificationFilter).
 */
@WebServlet(name = "GestionJetonsServlet", urlPatterns = {"/admin/jetons"})
public class GestionJetonsServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final CategorieService categorieService = new CategorieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        afficherListe(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action == null ? "" : action) {
                case "generer" -> generer(request);
                case "revoquer" -> authService.revoquerJeton(lireId(request));
                case "reactiver" -> authService.reactiverJeton(lireId(request));
                case "supprimer" -> authService.supprimerJeton(lireId(request));
                default -> { /* action inconnue : on ne fait rien */ }
            }
        } catch (Exception e) {
            afficherListe(request, response, "Erreur : " + e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/jetons");
    }

    private void generer(HttpServletRequest request) {
        int utilisateurId = lireUtilisateurId(request);
        authService.genererJeton(utilisateurId);
    }

    private int lireId(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("id"));
    }

    private int lireUtilisateurId(HttpServletRequest request) {
        String valeur = request.getParameter("utilisateurId");
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Veuillez choisir un utilisateur.");
        }
        return Integer.parseInt(valeur);
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response, String erreur)
            throws ServletException, IOException {
        request.setAttribute("jetons", authService.listerJetons());
        request.setAttribute("utilisateurs", utilisateurService.listerTous());
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.setAttribute("erreur", erreur);
        request.getRequestDispatcher("/WEB-INF/views/admin/jetons.jsp").forward(request, response);
    }
}
