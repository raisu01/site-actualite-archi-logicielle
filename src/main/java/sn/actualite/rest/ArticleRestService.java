package sn.actualite.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import sn.actualite.model.Article;
import sn.actualite.model.Categorie;
import sn.actualite.rest.dto.ArticlesXml;
import sn.actualite.rest.dto.CategorieArticlesXml;
import sn.actualite.rest.dto.CategoriesArticlesXml;
import sn.actualite.service.ArticleService;
import sn.actualite.service.CategorieService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service REST de consultation des articles, ouvert (lecture seule, pas de
 * jeton requis contrairement au CRUD utilisateurs SOAP). Chaque opération
 * répond en XML ou en JSON selon l'en-tête HTTP "Accept" du client.
 */
@Path("/articles")
public class ArticleRestService {

    private final ArticleService articleService = new ArticleService();
    private final CategorieService categorieService = new CategorieService();

    /** Tous les articles, du plus récent au plus ancien. */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ArticlesXml listerTous() {
        return ArticlesXml.depuis(articleService.listerTous());
    }

    /** Tous les articles groupés par catégorie. */
    @GET
    @Path("/categories")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CategoriesArticlesXml groupesParCategorie() {
        List<Categorie> categories = categorieService.listerToutes();
        List<CategorieArticlesXml> groupes = categories.stream()
                .map(categorie -> CategorieArticlesXml.depuis(categorie, articleService.listerParCategorie(categorie.getId())))
                .collect(Collectors.toList());
        return CategoriesArticlesXml.depuis(groupes);
    }

    /** Les articles d'une catégorie donnée. */
    @GET
    @Path("/categorie/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ArticlesXml parCategorie(@PathParam("id") int categorieId) {
        Categorie categorie = categorieService.trouverParId(categorieId);
        if (categorie == null) {
            throw new NotFoundException("Categorie introuvable (id=" + categorieId + ")");
        }
        List<Article> articles = articleService.listerParCategorie(categorieId);
        return ArticlesXml.depuis(articles);
    }
}
