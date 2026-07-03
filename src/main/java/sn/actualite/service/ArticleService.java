package sn.actualite.service;

import sn.actualite.dao.ArticleDao;
import sn.actualite.model.Article;

import java.util.List;

/**
 * Logique métier autour des articles.
 * Le contrôleur (servlet) ne doit jamais appeler ArticleDao directement :
 * il passe toujours par ce service.
 */
public class ArticleService {

    /** Nombre d'articles affichés par page sur la page d'accueil. */
    public static final int TAILLE_PAGE = 5;

    private final ArticleDao articleDao = new ArticleDao();

    /**
     * Retourne les articles de la page demandée, triés du plus récent au plus ancien.
     * @param page numéro de page, à partir de 1
     */
    public List<Article> listerPage(int page) {
        if (page < 1) {
            page = 1;
        }
        int decalage = (page - 1) * TAILLE_PAGE;
        return articleDao.lister(TAILLE_PAGE, decalage);
    }

    /** Nombre total d'articles, utile pour savoir si le bouton "suivant" doit être actif. */
    public int compterArticles() {
        return articleDao.compter();
    }

    /** Nombre total de pages, arrondi au supérieur (au moins 1). */
    public int compterPages() {
        int total = compterArticles();
        int pages = (int) Math.ceil(total / (double) TAILLE_PAGE);
        return Math.max(pages, 1);
    }

    public Article trouverParId(int id) {
        return articleDao.trouverParId(id);
    }

    /** Les N articles les plus récents, tous confondus — utilisé pour le bandeau "EN DIRECT". */
    public List<Article> listerDerniers(int nombre) {
        return articleDao.lister(nombre, 0);
    }

    public List<Article> listerParCategorie(int categorieId) {
        return articleDao.listerParCategorie(categorieId);
    }

    public List<Article> listerTous() {
        return articleDao.listerTous();
    }

    public int creer(Article article) {
        return articleDao.creer(article);
    }

    public void modifier(Article article) {
        articleDao.modifier(article);
    }

    public void supprimer(int id) {
        articleDao.supprimer(id);
    }
}
