package sn.client.ws;

import jakarta.xml.ws.WebServiceException;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPException;
import sn.client.model.UtilisateurDTO;
import sn.client.ws.contrat.AuthResultatWs;
import sn.client.ws.contrat.AuthSoapServiceContrat;
import sn.client.ws.contrat.UtilisateurSoapException;
import sn.client.ws.contrat.UtilisateurSoapServiceContrat;
import sn.client.ws.contrat.UtilisateurWs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client des services SOAP (JAX-WS, Apache CXF) exposés par le serveur
 * (soap/AuthSoapService et soap/UtilisateurSoapService, Membre 2). Le contrat
 * (sn.client.ws.contrat) est un miroir des interfaces serveur : même namespace,
 * mêmes opérations, ce qui permet à CXF de générer un proxy directement à
 * partir du Java, sans avoir besoin de récupérer le WSDL au démarrage.
 */
public class SoapClient {

    private final String baseUrl;

    public SoapClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Résultat de l'authentification (login/mot de passe) auprès de AuthSoapService.
     * Le jeton est celui, déjà généré au préalable par un administrateur, actif
     * pour ce compte ; il peut être null si aucun jeton n'a encore été généré.
     */
    public record ResultatAuth(boolean succes, String role, String message, String jeton) {
    }

    public ResultatAuth authentifier(String login, String motDePasse) throws SoapClientException {
        try {
            AuthSoapServiceContrat service = creerProxy(AuthSoapServiceContrat.class, "/auth", null);
            AuthResultatWs resultat = service.authentifier(login, motDePasse);
            return new ResultatAuth(resultat.isSucces(), resultat.getRole(), resultat.getMessage(), resultat.getJeton());
        } catch (Exception e) {
            throw traduire(e);
        }
    }

    public List<UtilisateurDTO> listerUtilisateurs(String jeton) throws SoapClientException {
        try {
            UtilisateurSoapServiceContrat service = creerProxy(UtilisateurSoapServiceContrat.class, "/utilisateurs", jeton);
            return service.listerUtilisateurs().stream()
                    .map(u -> new UtilisateurDTO(u.getId(), u.getLogin(), u.getRole()))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            throw traduire(e);
        }
    }

    public int creerUtilisateur(String jeton, UtilisateurDTO utilisateur) throws SoapClientException {
        try {
            UtilisateurSoapServiceContrat service = creerProxy(UtilisateurSoapServiceContrat.class, "/utilisateurs", jeton);
            return service.creerUtilisateur(utilisateur.getLogin(), utilisateur.getMotDePasse(), utilisateur.getRole());
        } catch (Exception e) {
            throw traduire(e);
        }
    }

    public void modifierUtilisateur(String jeton, UtilisateurDTO utilisateur) throws SoapClientException {
        try {
            UtilisateurSoapServiceContrat service = creerProxy(UtilisateurSoapServiceContrat.class, "/utilisateurs", jeton);
            service.modifierUtilisateur(utilisateur.getId(), utilisateur.getLogin(), utilisateur.getMotDePasse(), utilisateur.getRole());
        } catch (Exception e) {
            throw traduire(e);
        }
    }

    public void supprimerUtilisateur(String jeton, int id) throws SoapClientException {
        try {
            UtilisateurSoapServiceContrat service = creerProxy(UtilisateurSoapServiceContrat.class, "/utilisateurs", jeton);
            service.supprimerUtilisateur(id);
        } catch (Exception e) {
            throw traduire(e);
        }
    }

    private <T> T creerProxy(Class<T> contrat, String chemin, String jeton) {
        JaxWsProxyFactoryBean fabrique = new JaxWsProxyFactoryBean();
        fabrique.setServiceClass(contrat);
        fabrique.setAddress(baseUrl + chemin);
        T proxy = contrat.cast(fabrique.create());

        if (jeton != null) {
            Client client = ClientProxy.getClient(proxy);
            Map<String, List<String>> entetes = new HashMap<>();
            entetes.put("X-Jeton", Collections.singletonList(jeton));
            client.getRequestContext().put(Message.PROTOCOL_HEADERS, entetes);
        }
        return proxy;
    }

    /** Traduit les erreurs bas niveau (CXF/JAX-WS) en message compréhensible pour l'IHM. */
    private SoapClientException traduire(Exception e) {
        if (e instanceof UtilisateurSoapException faute) {
            return new SoapClientException(faute.getFaultInfo().getMessage(), e);
        }
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof HTTPException http && http.getResponseCode() == 401) {
                return new SoapClientException("Jeton manquant, invalide ou révoqué.");
            }
        }
        if (e instanceof WebServiceException) {
            return new SoapClientException("Impossible de contacter le service web (" + baseUrl + ") : " + e.getMessage(), e);
        }
        return new SoapClientException("Erreur inattendue : " + e.getMessage(), e);
    }
}
