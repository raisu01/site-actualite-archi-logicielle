package sn.actualite.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Contrat SOAP (JAX-WS) d'authentification, publié sur "/ws/auth" par
 * {@link ServeurSoapServlet}. Consommé par l'application client
 * (client/ws/SoapClient#authentifier, Membre 3) pour l'écran de connexion.
 *
 * Volontairement non protégé par jeton (voir util/JetonFilter, qui n'intercepte
 * que "/ws/utilisateurs") : ce service sert justement à vérifier une identité,
 * avant qu'un jeton ne soit fourni.
 */
@WebService(targetNamespace = SoapConstantes.NS, name = "AuthSoapService")
public interface AuthSoapService {

    @WebMethod
    AuthResultat authentifier(@WebParam(name = "login") String login,
                              @WebParam(name = "motDePasse") String motDePasse);
}
