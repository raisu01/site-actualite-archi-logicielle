package sn.client.model;

/**
 * Représentation cliente d'un utilisateur, telle qu'échangée avec le service
 * SOAP soap/UtilisateurSoapService (Membre 2). Volontairement indépendante du
 * modèle serveur sn.actualite.model.Utilisateur : un client SOAP ne partage
 * pas de code avec le serveur qu'il appelle, seulement un contrat XML.
 */
public class UtilisateurDTO {

    private int id;
    private String login;
    private String motDePasse;
    private String role;

    public UtilisateurDTO() {
    }

    public UtilisateurDTO(int id, String login, String role) {
        this.id = id;
        this.login = login;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return login + " (" + role + ")";
    }
}
