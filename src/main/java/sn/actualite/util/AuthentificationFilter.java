package sn.actualite.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.actualite.model.Role;
import sn.actualite.model.Utilisateur;

import java.io.IOException;

/**
 * Protège tout le back-office web (/admin/*) : accès réservé aux éditeurs et
 * administrateurs connectés. La gestion des utilisateurs (/admin/utilisateurs*)
 * est en plus réservée aux seuls administrateurs.
 *
 * À ne pas confondre avec util/JetonFilter (Membre 3), qui protège l'accès
 * aux services web SOAP/REST via un jeton, pas via une session HTTP.
 */
@WebFilter(urlPatterns = {"/admin/*"})
public class AuthentificationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = session == null ? null : (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        boolean sectionUtilisateurs = request.getRequestURI().contains("/admin/utilisateurs")
                || request.getRequestURI().contains("/admin/jetons");

        if (sectionUtilisateurs && utilisateur.getRole() != Role.ADMINISTRATEUR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès réservé aux administrateurs.");
            return;
        }

        // Éditeur ou administrateur, sur une section autorisée : on laisse passer.
        chain.doFilter(request, response);
    }
}
