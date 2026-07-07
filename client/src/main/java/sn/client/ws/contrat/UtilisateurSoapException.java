package sn.client.ws.contrat;

import jakarta.xml.ws.WebFault;

/** Miroir client de sn.actualite.soap.UtilisateurSoapException (Membre 2). */
@WebFault(name = "UtilisateurSoapFault", targetNamespace = ContratConstantes.NS)
public class UtilisateurSoapException extends Exception {

    private UtilisateurSoapFaultInfo faultInfo;

    public UtilisateurSoapException() {
    }

    public UtilisateurSoapException(String message, UtilisateurSoapFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public UtilisateurSoapFaultInfo getFaultInfo() {
        return faultInfo;
    }

    public void setFaultInfo(UtilisateurSoapFaultInfo faultInfo) {
        this.faultInfo = faultInfo;
    }
}
