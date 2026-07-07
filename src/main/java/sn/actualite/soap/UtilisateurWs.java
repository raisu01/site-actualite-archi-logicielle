package sn.actualite.soap;

/**
 * Représentation d'un utilisateur échangée sur le fil SOAP (sans mot de
 * passe : le CRUD utilisateurs ne le renvoie jamais en lecture).
 * Bean JAXB simple (constructeur vide + accesseurs).
 */
public class UtilisateurWs {

    private int id;
    private String login;
    private String role;

    public UtilisateurWs() {
    }

    public UtilisateurWs(int id, String login, String role) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
