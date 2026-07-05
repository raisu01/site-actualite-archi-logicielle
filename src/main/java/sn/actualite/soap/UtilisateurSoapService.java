package sn.actualite.soap;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Element;
import sn.actualite.model.Role;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.UtilisateurService;

import java.io.IOException;
import java.util.List;

/**
 * Service SOAP de gestion des utilisateurs (CRUD), protégé par jeton (voir
 * util/JetonFilter, qui intercepte "/ws/utilisateurs" avant cette servlet et
 * exige l'en-tête HTTP "X-Jeton"). Consommé par l'application client
 * (client/ws/SoapClient, Membre 3).
 *
 * Contrat : voir {@link SoapMessages}. L'opération demandée est déterminée par
 * le nom du premier élément du corps SOAP :
 * <pre>
 * listerUtilisateursRequest    -&gt; listerUtilisateursResponse (0..n utilisateur)
 * creerUtilisateurRequest      -&gt; creerUtilisateurResponse (id)
 * modifierUtilisateurRequest   -&gt; modifierUtilisateurResponse
 * supprimerUtilisateurRequest  -&gt; supprimerUtilisateurResponse
 * </pre>
 */
@WebServlet("/ws/utilisateurs")
public class UtilisateurSoapService extends HttpServlet {

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Element requete = SoapMessages.extraireCorps(request);
            String operation = requete.getLocalName();

            switch (operation) {
                case "listerUtilisateursRequest" -> lister(response);
                case "creerUtilisateurRequest" -> creer(requete, response);
                case "modifierUtilisateurRequest" -> modifier(requete, response);
                case "supprimerUtilisateurRequest" -> supprimer(requete, response);
                default -> SoapMessages.ecrireErreur(response, "Operation inconnue : " + operation);
            }
        } catch (Exception e) {
            SoapMessages.ecrireErreur(response, "Erreur inattendue : " + e.getMessage());
        }
    }

    private void lister(HttpServletResponse response) throws IOException {
        List<Utilisateur> utilisateurs = utilisateurService.listerTous();
        StringBuilder contenu = new StringBuilder("<listerUtilisateursResponse xmlns=\"" + SoapMessages.NS + "\">");
        for (Utilisateur u : utilisateurs) {
            contenu.append("<utilisateur>")
                    .append("<id>").append(u.getId()).append("</id>")
                    .append("<login>").append(SoapMessages.echapper(u.getLogin())).append("</login>")
                    .append("<role>").append(u.getRole().name()).append("</role>")
                    .append("</utilisateur>");
        }
        contenu.append("</listerUtilisateursResponse>");
        SoapMessages.ecrireReponse(response, contenu.toString());
    }

    private void creer(Element requete, HttpServletResponse response) throws IOException {
        String login = SoapMessages.texte(requete, "login");
        String motDePasse = SoapMessages.texte(requete, "motDePasse");
        Role role = lireRole(requete);
        if (role == null) {
            SoapMessages.ecrireErreur(response, "Role invalide (attendu : VISITEUR, EDITEUR ou ADMINISTRATEUR).");
            return;
        }
        if (login == null || login.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            SoapMessages.ecrireErreur(response, "Login et mot de passe sont obligatoires.");
            return;
        }
        if (utilisateurService.trouverParLogin(login.trim()) != null) {
            SoapMessages.ecrireErreur(response, "Ce login existe deja : " + login);
            return;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(login.trim());
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setRole(role);
        int id = utilisateurService.creer(utilisateur);

        String contenu = "<creerUtilisateurResponse xmlns=\"" + SoapMessages.NS + "\">"
                + "<id>" + id + "</id>"
                + "</creerUtilisateurResponse>";
        SoapMessages.ecrireReponse(response, contenu);
    }

    private void modifier(Element requete, HttpServletResponse response) throws IOException {
        String idTexte = SoapMessages.texte(requete, "id");
        if (idTexte == null) {
            SoapMessages.ecrireErreur(response, "Identifiant utilisateur manquant.");
            return;
        }
        int id = Integer.parseInt(idTexte);

        Utilisateur utilisateur = utilisateurService.trouverParId(id);
        if (utilisateur == null) {
            SoapMessages.ecrireErreur(response, "Utilisateur introuvable (id=" + id + ")");
            return;
        }

        Role role = lireRole(requete);
        if (role == null) {
            SoapMessages.ecrireErreur(response, "Role invalide (attendu : VISITEUR, EDITEUR ou ADMINISTRATEUR).");
            return;
        }

        String login = SoapMessages.texte(requete, "login");
        String motDePasse = SoapMessages.texte(requete, "motDePasse");

        utilisateur.setLogin(login != null ? login.trim() : utilisateur.getLogin());
        utilisateur.setRole(role);
        if (motDePasse != null && !motDePasse.isBlank()) {
            utilisateur.setMotDePasse(motDePasse);
        }
        utilisateurService.modifier(utilisateur);

        SoapMessages.ecrireReponse(response, "<modifierUtilisateurResponse xmlns=\"" + SoapMessages.NS + "\"/>");
    }

    private void supprimer(Element requete, HttpServletResponse response) throws IOException {
        String idTexte = SoapMessages.texte(requete, "id");
        if (idTexte == null) {
            SoapMessages.ecrireErreur(response, "Identifiant utilisateur manquant.");
            return;
        }
        utilisateurService.supprimer(Integer.parseInt(idTexte));
        SoapMessages.ecrireReponse(response, "<supprimerUtilisateurResponse xmlns=\"" + SoapMessages.NS + "\"/>");
    }

    private Role lireRole(Element requete) {
        String roleTexte = SoapMessages.texte(requete, "role");
        if (roleTexte == null) {
            return null;
        }
        try {
            return Role.valueOf(roleTexte.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
