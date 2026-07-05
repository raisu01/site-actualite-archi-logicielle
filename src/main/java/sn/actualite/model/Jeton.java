package sn.actualite.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Jeton {

    private static final DateTimeFormatter FORMAT_AFFICHAGE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String valeur;
    private Utilisateur utilisateur;
    private LocalDateTime dateCreation;
    private boolean actif;

    public Jeton() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /**
     * Date de création déjà formatée (dd/MM/yyyy HH:mm), prête à afficher
     * directement dans une JSP via ${jeton.dateCreationFormatee}.
     */
    public String getDateCreationFormatee() {
        return dateCreation == null ? "" : dateCreation.format(FORMAT_AFFICHAGE);
    }
}
