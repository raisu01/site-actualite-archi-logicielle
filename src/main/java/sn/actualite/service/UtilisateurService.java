package sn.actualite.service;

import sn.actualite.dao.UtilisateurDao;
import sn.actualite.model.Utilisateur;

import java.util.List;

/**
 * Logique métier autour des utilisateurs (comptes éditeur/administrateur).
 *
 * NOTE ÉQUIPE : ce fichier est assigné au Membre 3 dans plan-repartition.md
 * (logique métier utilisateurs, réutilisée par le service SOAP). Version
 * minimale créée ici pour débloquer la gestion des utilisateurs côté site web
 * (Membre 1) — à fusionner/coordonner avec le Membre 3, pas à dupliquer.
 */
public class UtilisateurService {

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    public List<Utilisateur> listerTous() {
        return utilisateurDao.listerTous();
    }

    public Utilisateur trouverParId(int id) {
        return utilisateurDao.trouverParId(id);
    }

    public Utilisateur trouverParLogin(String login) {
        return utilisateurDao.trouverParLogin(login);
    }

    public int creer(Utilisateur utilisateur) {
        return utilisateurDao.creer(utilisateur);
    }

    public void modifier(Utilisateur utilisateur) {
        utilisateurDao.modifier(utilisateur);
    }

    public void supprimer(int id) {
        utilisateurDao.supprimer(id);
    }
}
