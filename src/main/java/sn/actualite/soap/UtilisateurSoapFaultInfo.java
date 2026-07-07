package sn.actualite.soap;

/** Détail transporté par {@link UtilisateurSoapException} dans le soap:Fault. */
public class UtilisateurSoapFaultInfo {

    private String message;

    public UtilisateurSoapFaultInfo() {
    }

    public UtilisateurSoapFaultInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
