package sn.actualite.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Article {

    private static final DateTimeFormatter FORMAT_AFFICHAGE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String titre;
    private String contenu;
    private LocalDateTime datePublication;
    private Categorie categorie;
    private Utilisateur auteur;

    public Article() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    /**
     * Date de publication déjà formatée (dd/MM/yyyy HH:mm), prête à afficher
     * directement dans une JSP via ${article.datePublicationFormatee}.
     */
    public String getDatePublicationFormatee() {
        return datePublication == null ? "" : datePublication.format(FORMAT_AFFICHAGE);
    }

    /**
     * Résumé court du contenu (200 caractères max + points de suspension),
     * pour l'affichage en liste sur la page d'accueil.
     */
    public String getResume() {
        if (contenu == null) {
            return "";
        }
        if (contenu.length() <= 200) {
            return contenu;
        }
        return contenu.substring(0, 200) + "...";
    }
}
