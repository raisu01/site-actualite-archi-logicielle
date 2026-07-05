package sn.client.ws;

/** Erreur lors d'un appel au service web SOAP (connexion, jeton invalide, fault SOAP...). */
public class SoapClientException extends Exception {

    public SoapClientException(String message) {
        super(message);
    }

    public SoapClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
