package sn.actualite.rest.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import sn.actualite.model.Article;

import java.util.List;
import java.util.stream.Collectors;

/** Enveloppe racine pour une liste d'articles (réponse REST XML/JSON). */
@XmlRootElement(name = "articles")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticlesXml {

    @XmlElement(name = "article")
    private List<ArticleXml> articles;

    public ArticlesXml() {
    }

    public static ArticlesXml depuis(List<Article> articles) {
        ArticlesXml dto = new ArticlesXml();
        dto.articles = articles.stream().map(ArticleXml::depuis).collect(Collectors.toList());
        return dto;
    }

    public List<ArticleXml> getArticles() {
        return articles;
    }
}
