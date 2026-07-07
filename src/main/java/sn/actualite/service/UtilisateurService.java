package sn.actualite.service;

import sn.actualite.dao.UtilisateurDao;
import sn.actualite.model.Utilisateur;
import sn.actualite.util.SecuriteUtil;

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
        if (utilisateur.getMotDePasse() != null) {
            utilisateur.setMotDePasse(SecuriteUtil.hacherMotDePasse(utilisateur.getMotDePasse()));
        }
        return utilisateurDao.creer(utilisateur);
    }

    public void modifier(Utilisateur utilisateur) {
        // Le mot de passe ne doit être haché que s'il a été modifié.
        // Si l'IHM n'a pas renvoyé de mot de passe, ou si c'est un mot de passe en clair, on doit s'en occuper.
        // Le DAO s'attend à ce que le modèle contienne le mot de passe final.
        // UtilisateurSoapServiceImpl passe le mot de passe en clair à modifier s'il n'est pas vide.
        // On va vérifier si le mot de passe diffère de celui en base (ou sa taille, un hash sha256 fait 64 char).
        // Mais plus simple : s'il fait moins de 64 char, c'est qu'il est en clair.
        if (utilisateur.getMotDePasse() != null && utilisateur.getMotDePasse().length() != 64) {
            utilisateur.setMotDePasse(SecuriteUtil.hacherMotDePasse(utilisateur.getMotDePasse()));
        }
        utilisateurDao.modifier(utilisateur);
    }

    public void supprimer(int id) {
        utilisateurDao.supprimer(id);
    }
}
