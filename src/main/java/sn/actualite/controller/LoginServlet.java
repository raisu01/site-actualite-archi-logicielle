package sn.actualite.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.AuthService;
import sn.actualite.service.CategorieService;

import java.io.IOException;

/**
 * Authentification éditeur/administrateur pour l'accès au back-office.
 * Les visiteurs simples n'ont pas besoin de compte.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final CategorieService categorieService = new CategorieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Déjà connecté : pas besoin de re-saisir le formulaire.
        if (utilisateurConnecte(request) != null) {
            response.sendRedirect(request.getContextPath() + "/accueil");
            return;
        }

        afficherFormulaire(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String motDePasse = request.getParameter("motDePasse");

        Utilisateur utilisateur = authService.connecter(login, motDePasse);

        if (utilisateur == null) {
            afficherFormulaire(request, response, "Login ou mot de passe incorrect.", login);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("utilisateur", utilisateur);

        response.sendRedirect(request.getContextPath() + "/accueil");
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response,
                                     String erreur, String loginSaisi)
            throws ServletException, IOException {

        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);

        request.setAttribute("erreur", erreur);
        request.setAttribute("loginSaisi", loginSaisi);

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    private Utilisateur utilisateurConnecte(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Utilisateur) session.getAttribute("utilisateur");
    }
}
