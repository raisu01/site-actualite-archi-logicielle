package sn.actualite.service;

import sn.actualite.dao.CategorieDao;
import sn.actualite.model.Categorie;

import java.util.List;

/**
 * Logique métier autour des catégories.
 */
public class CategorieService {

    private final CategorieDao categorieDao = new CategorieDao();

    public List<Categorie> listerToutes() {
        return categorieDao.listerToutes();
    }

    public Categorie trouverParId(int id) {
        return categorieDao.trouverParId(id);
    }

    public int creer(Categorie categorie) {
        return categorieDao.creer(categorie);
    }

    public void modifier(Categorie categorie) {
        categorieDao.modifier(categorie);
    }

    public void supprimer(int id) {
        categorieDao.supprimer(id);
    }
}
