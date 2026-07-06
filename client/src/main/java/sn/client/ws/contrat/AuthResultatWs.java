package sn.client.ws.contrat;

/** Miroir client de sn.actualite.soap.AuthResultat (Membre 2). */
public class AuthResultatWs {

    private boolean succes;
    private String role;
    private String message;
    private String jeton;

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

    public String getJeton() {
        return jeton;
    }

    public void setJeton(String jeton) {
        this.jeton = jeton;
    }
}
