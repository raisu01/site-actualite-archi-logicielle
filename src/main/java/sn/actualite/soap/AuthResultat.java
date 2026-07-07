package sn.actualite.soap;

/**
 * Résultat renvoyé par {@link AuthSoapService#authentifier(String, String)}.
 * Bean JAXB simple (constructeur vide + accesseurs), sérialisé dans le corps
 * SOAP de la réponse.
 */
public class AuthResultat {

    private boolean succes;
    private String role;
    private String message;

    public AuthResultat() {
    }

    public AuthResultat(boolean succes, String role, String message) {
        this.succes = succes;
        this.role = role;
        this.message = message;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
