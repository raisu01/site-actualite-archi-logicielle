package sn.client.ws.contrat;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/** Miroir client de sn.actualite.soap.AuthSoapService (Membre 2), publié sur "/ws/auth". */
@WebService(targetNamespace = ContratConstantes.NS, name = "AuthSoapService")
public interface AuthSoapServiceContrat {

    @WebMethod
    AuthResultatWs authentifier(@WebParam(name = "login") String login,
                                @WebParam(name = "motDePasse") String motDePasse);
}
