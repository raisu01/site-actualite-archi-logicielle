package sn.actualite.rest.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import sn.actualite.model.Article;
import sn.actualite.model.Categorie;

import java.util.List;
import java.util.stream.Collectors;

/** Les articles d'une catégorie donnée, avec le nom de la catégorie. */
@XmlRootElement(name = "categorieArticles")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategorieArticlesXml {

    private int categorieId;
    private String categorie;

    @XmlElement(name = "article")
    private List<ArticleXml> articles;

    public CategorieArticlesXml() {
    }

    public static CategorieArticlesXml depuis(Categorie categorie, List<Article> articles) {
        CategorieArticlesXml dto = new CategorieArticlesXml();
        dto.categorieId = categorie.getId();
        dto.categorie = categorie.getNom();
        dto.articles = articles.stream().map(ArticleXml::depuis).collect(Collectors.toList());
        return dto;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public String getCategorie() {
        return categorie;
    }

    public List<ArticleXml> getArticles() {
        return articles;
    }
}
