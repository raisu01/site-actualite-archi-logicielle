package sn.diop.actualite.model;

public class Utilisateur {

    private int id;
    private String login;
    private String motDePasse;
    private Role role;

    public Utilisateur() {
    }

    public Utilisateur(int id, String login, String motDePasse, Role role) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
