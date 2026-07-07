package sn.actualite.soap;

import jakarta.xml.ws.WebFault;

/**
 * Erreur métier du CRUD utilisateurs (login déjà pris, rôle invalide,
 * utilisateur introuvable...). Le runtime JAX-WS la transforme automatiquement
 * en soap:Fault côté fil, avec {@link #getFaultInfo()} comme détail.
 */
@WebFault(name = "UtilisateurSoapFault", targetNamespace = SoapConstantes.NS)
public class UtilisateurSoapException extends Exception {

    private final UtilisateurSoapFaultInfo faultInfo;

    public UtilisateurSoapException(String message) {
        super(message);
        this.faultInfo = new UtilisateurSoapFaultInfo(message);
    }

    public UtilisateurSoapFaultInfo getFaultInfo() {
        return faultInfo;
    }
}
