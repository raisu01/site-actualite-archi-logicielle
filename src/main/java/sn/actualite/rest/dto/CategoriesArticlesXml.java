package sn.actualite.rest.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/** Enveloppe racine pour les articles groupés par catégorie (réponse REST XML/JSON). */
@XmlRootElement(name = "categoriesArticles")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoriesArticlesXml {

    @XmlElement(name = "categorieArticles")
    private List<CategorieArticlesXml> groupes;

    public CategoriesArticlesXml() {
    }

    public static CategoriesArticlesXml depuis(List<CategorieArticlesXml> groupes) {
        CategoriesArticlesXml dto = new CategoriesArticlesXml();
        dto.groupes = groupes;
        return dto;
    }

    public List<CategorieArticlesXml> getGroupes() {
        return groupes;
    }
}
