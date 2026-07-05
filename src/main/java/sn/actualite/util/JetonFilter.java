package sn.actualite.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.AuthService;

import java.io.IOException;

/**
 * Protège le service web SOAP de gestion des utilisateurs (CRUD) via un jeton
 * généré par un administrateur (voir controller/admin/GestionJetonsServlet).
 *
 * Convention retenue pour Membre 2 : les services SOAP sont publiés sous le
 * préfixe d'URL "/ws/" ; ce filtre ne protège que "/ws/utilisateurs" (le CRUD
 * utilisateurs, soap/UtilisateurSoapService), pas "/ws/auth"
 * (soap/AuthSoapService, qui vérifie login/mot de passe et ne doit donc pas
 * exiger un jeton, sous peine de bloquer la connexion elle-même). Le jeton est
 * transmis dans l'en-tête HTTP "X-Jeton" de la requête.
 *
 * À ne pas confondre avec util/AuthentificationFilter, qui protège le
 * back-office web (/admin/*) via une session HTTP, pas via un jeton.
 */
@WebFilter(urlPatterns = {"/ws/utilisateurs", "/ws/utilisateurs/*"})
public class JetonFilter implements Filter {

    public static final String EN_TETE_JETON = "X-Jeton";

    private final AuthService authService = new AuthService();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String valeurJeton = request.getHeader(EN_TETE_JETON);
        Utilisateur utilisateur = authService.validerJeton(valeurJeton);

        if (utilisateur == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Jeton manquant, invalide ou revoque.");
            return;
        }

        request.setAttribute("utilisateurJeton", utilisateur);
        chain.doFilter(request, response);
    }
}
