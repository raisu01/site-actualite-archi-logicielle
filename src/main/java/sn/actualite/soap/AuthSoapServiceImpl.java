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
        return new AuthResultat(true, utilisateur.getRole().name(), "Connexion reussie.");
    }
}
