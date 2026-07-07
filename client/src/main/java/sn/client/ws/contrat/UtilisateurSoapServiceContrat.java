package sn.client.ws.contrat;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

/**
 * Miroir client de sn.actualite.soap.UtilisateurSoapService (Membre 2), publié
 * sur "/ws/utilisateurs" et protégé par jeton (en-tête HTTP "X-Jeton", voir
 * SoapClient#avecJeton).
 */
@WebService(targetNamespace = ContratConstantes.NS, name = "UtilisateurSoapService")
public interface UtilisateurSoapServiceContrat {

    @WebMethod
    List<UtilisateurWs> listerUtilisateurs();

    @WebMethod
    int creerUtilisateur(@WebParam(name = "login") String login,
                         @WebParam(name = "motDePasse") String motDePasse,
                         @WebParam(name = "role") String role) throws UtilisateurSoapException;

    @WebMethod
    void modifierUtilisateur(@WebParam(name = "id") int id,
                             @WebParam(name = "login") String login,
                             @WebParam(name = "motDePasse") String motDePasse,
                             @WebParam(name = "role") String role) throws UtilisateurSoapException;

    @WebMethod
    void supprimerUtilisateur(@WebParam(name = "id") int id) throws UtilisateurSoapException;
}
