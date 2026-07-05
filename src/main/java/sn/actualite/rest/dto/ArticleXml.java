package sn.actualite.rest.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import sn.actualite.model.Article;

/**
 * Représentation d'un article exposée par le service REST (XML/JSON).
 * Volontairement distincte de model.Article : un article contient un auteur
 * (Utilisateur, avec mot de passe) qui ne doit jamais être sérialisé tel quel
 * vers un client externe.
 */
@XmlRootElement(name = "article")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleXml {

    private int id;
    private String titre;
    private String contenu;
    private String datePublication;
    private String categorie;
    private String auteur;

    public ArticleXml() {
    }

    public static ArticleXml depuis(Article article) {
        ArticleXml dto = new ArticleXml();
        dto.id = article.getId();
        dto.titre = article.getTitre();
        dto.contenu = article.getContenu();
        dto.datePublication = article.getDatePublicationFormatee();
        dto.categorie = article.getCategorie() != null ? article.getCategorie().getNom() : null;
        dto.auteur = article.getAuteur() != null ? article.getAuteur().getLogin() : null;
        return dto;
    }

    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getContenu() {
        return contenu;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getAuteur() {
        return auteur;
    }
}
