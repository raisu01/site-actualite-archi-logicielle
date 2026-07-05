package sn.actualite.soap;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Element;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.AuthService;

import java.io.IOException;

/**
 * Service SOAP d'authentification (login/mot de passe), consommé par
 * l'application client (client/ws/SoapClient#authentifier, Membre 3) pour
 * l'écran de connexion. Volontairement non protégé par jeton : il sert
 * justement à vérifier une identité, avant qu'un jeton ne soit fourni.
 *
 * Contrat : voir {@link SoapMessages}.
 * <pre>
 * POST /actualite/ws/auth
 *   Requete  : &lt;authentifierRequest&gt;&lt;login/&gt;&lt;motDePasse/&gt;&lt;/authentifierRequest&gt;
 *   Reponse  : &lt;authentifierResponse&gt;&lt;succes/&gt;&lt;role/&gt;&lt;message/&gt;&lt;/authentifierResponse&gt;
 * </pre>
 */
@WebServlet("/ws/auth")
public class AuthSoapService extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Element requete = SoapMessages.extraireCorps(request);
            String login = SoapMessages.texte(requete, "login");
            String motDePasse = SoapMessages.texte(requete, "motDePasse");

            Utilisateur utilisateur = authService.connecter(login, motDePasse);

            boolean succes = utilisateur != null;
            String role = succes ? utilisateur.getRole().name() : "";
            String message = succes ? "Connexion reussie." : "Login ou mot de passe incorrect.";

            String reponseXml = "<authentifierResponse xmlns=\"" + SoapMessages.NS + "\">"
                    + "<succes>" + succes + "</succes>"
                    + "<role>" + SoapMessages.echapper(role) + "</role>"
                    + "<message>" + SoapMessages.echapper(message) + "</message>"
                    + "</authentifierResponse>";
            SoapMessages.ecrireReponse(response, reponseXml);
        } catch (Exception e) {
            SoapMessages.ecrireErreur(response, "Erreur lors de l'authentification : " + e.getMessage());
        }
    }
}
