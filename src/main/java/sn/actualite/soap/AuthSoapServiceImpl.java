package sn.actualite.soap;

import jakarta.jws.WebService;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.AuthService;

@WebService(endpointInterface = "sn.actualite.soap.AuthSoapService",
        targetNamespace = SoapConstantes.NS, serviceName = "AuthSoapService")
public class AuthSoapServiceImpl implements AuthSoapService {

    private final AuthService authService = new AuthService();

    @Override
    public AuthResultat authentifier(String login, String motDePasse) {
        Utilisateur utilisateur = authService.connecter(login, motDePasse);
        if (utilisateur == null) {
            return new AuthResultat(false, "", "Login ou mot de passe incorrect.");
        }

        AuthResultat resultat = new AuthResultat(true, utilisateur.getRole().name(), "Connexion reussie.");
        // Fournit directement le jeton actif de l'utilisateur (déjà généré au
        // préalable par un administrateur depuis /admin/jetons) : l'appli
        // cliente n'a ainsi qu'à demander login/mot de passe, pas le jeton.
        resultat.setJeton(authService.trouverJetonActif(utilisateur.getId()));
        return resultat;
    }
}
