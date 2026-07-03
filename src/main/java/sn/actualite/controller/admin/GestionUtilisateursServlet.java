package sn.actualite.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.actualite.model.Role;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.CategorieService;
import sn.actualite.service.UtilisateurService;

import java.io.IOException;

/**
 * Back-office : gestion des comptes utilisateurs (éditeurs/administrateurs).
 * Réservé aux administrateurs (voir util/AuthentificationFilter).
 */
@WebServlet(name = "GestionUtilisateursServlet", urlPatterns = {"/admin/utilisateurs"})
public class GestionUtilisateursServlet extends HttpServlet {

    private final UtilisateurService utilisateurService = new UtilisateurService();
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

        response.sendRedirect(request.getContextPath() + "/admin/utilisateurs");
    }

    private void creer(HttpServletRequest request) {
        String login = requis(request.getParameter("login"), "Le login est obligatoire.");
        String motDePasse = requis(request.getParameter("motDePasse"), "Le mot de passe est obligatoire.");
        Role role = lireRole(request);

        if (utilisateurService.trouverParLogin(login) != null) {
            throw new IllegalArgumentException("Ce login existe déjà.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(login);
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setRole(role);
        utilisateurService.creer(utilisateur);
    }

    private void modifier(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Utilisateur utilisateur = utilisateurService.trouverParId(id);
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur introuvable (id=" + id + ")");
        }

        String login = requis(request.getParameter("login"), "Le login est obligatoire.");
        String motDePasse = request.getParameter("motDePasse"); // facultatif : vide = on garde l'ancien
        Role role = lireRole(request);

        empecherAutoDegradation(request, utilisateur, role);

        utilisateur.setLogin(login);
        if (motDePasse != null && !motDePasse.isBlank()) {
            utilisateur.setMotDePasse(motDePasse);
        }
        utilisateur.setRole(role);
        utilisateurService.modifier(utilisateur);

        // Si l'admin vient de se modifier lui-même, on rafraîchit sa session.
        HttpSession session = request.getSession(false);
        Utilisateur connecte = session == null ? null : (Utilisateur) session.getAttribute("utilisateur");
        if (connecte != null && connecte.getId() == utilisateur.getId()) {
            session.setAttribute("utilisateur", utilisateur);
        }
    }

    private void supprimer(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));

        HttpSession session = request.getSession(false);
        Utilisateur connecte = session == null ? null : (Utilisateur) session.getAttribute("utilisateur");
        if (connecte != null && connecte.getId() == id) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer votre propre compte.");
        }

        utilisateurService.supprimer(id);
    }

    /** Empêche un administrateur de se rétrograder lui-même (il perdrait l'accès à cette page). */
    private void empecherAutoDegradation(HttpServletRequest request, Utilisateur utilisateur, Role nouveauRole) {
        HttpSession session = request.getSession(false);
        Utilisateur connecte = session == null ? null : (Utilisateur) session.getAttribute("utilisateur");
        boolean estSoiMeme = connecte != null && connecte.getId() == utilisateur.getId();
        if (estSoiMeme && utilisateur.getRole() == Role.ADMINISTRATEUR && nouveauRole != Role.ADMINISTRATEUR) {
            throw new IllegalStateException("Vous ne pouvez pas retirer vos propres droits administrateur.");
        }
    }

    private Role lireRole(HttpServletRequest request) {
        String role = requis(request.getParameter("role"), "Le rôle est obligatoire.");
        try {
            Role r = Role.valueOf(role);
            if (r == Role.VISITEUR) {
                throw new IllegalArgumentException("Un compte ne peut pas avoir le rôle VISITEUR.");
            }
            return r;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rôle invalide.");
        }
    }

    private String requis(String valeur, String messageErreur) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(messageErreur);
        }
        return valeur.trim();
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response, String erreur)
            throws ServletException, IOException {
        request.setAttribute("utilisateurs", utilisateurService.listerTous());
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.setAttribute("erreur", erreur);
        request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs.jsp").forward(request, response);
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {

        Utilisateur utilisateur = null;
        if ("modifier".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            utilisateur = utilisateurService.trouverParId(id);
            if (utilisateur == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
                return;
            }
        }

        request.setAttribute("action", action);
        request.setAttribute("utilisateurEnEdition", utilisateur);
        request.setAttribute("categories", categorieService.listerToutes());
        request.setAttribute("categorieActive", null);
        request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs.jsp").forward(request, response);
    }
}
