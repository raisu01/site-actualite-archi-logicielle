package sn.client.ws.contrat;

/**
 * Espace de noms du contrat SOAP, doit rester identique à
 * sn.actualite.soap.SoapConstantes côté serveur (Membre 2) : c'est ce qui
 * permet à un proxy JAX-WS généré sans WSDL de dialoguer avec le service.
 */
public final class ContratConstantes {

    public static final String NS = "http://actualite.sn/ws";

    private ContratConstantes() {
    }
}
