package sn.actualite.service;

import sn.actualite.dao.UtilisateurDao;
import sn.actualite.model.Utilisateur;

/**
 * Logique d'authentification (login web + service SOAP d'authentification).
 *
 * NOTE ÉQUIPE : ce service est utilisé à la fois par le site web (LoginServlet)
 * et par le service SOAP d'authentification (soap/AuthSoapService, Membre 3).
 * Ne pas dupliquer la vérification des identifiants ailleurs : passer par ici.
 *
 * TODO (à décider en équipe) : les mots de passe sont actuellement stockés et
 * comparés en clair (cf. schema.sql). Pour la qualité du code, il faudrait les
 * hacher (BCrypt ou au minimum SHA-256) — impact sur schema.sql et sur la
 * création/modification d'utilisateurs (UtilisateurDao / GestionUtilisateursServlet),
 * donc à faire ensemble pour ne pas désynchroniser les données de test.
 */
public class AuthService {

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    /**
     * Vérifie le couple login/mot de passe.
     * @return l'utilisateur authentifié, ou null si login inconnu ou mot de passe incorrect.
     */
    public Utilisateur connecter(String login, String motDePasse) {
        if (login == null || motDePasse == null) {
            return null;
        }
        Utilisateur utilisateur = utilisateurDao.trouverParLogin(login.trim());
        if (utilisateur == null) {
            return null;
        }
        if (!utilisateur.getMotDePasse().equals(motDePasse)) {
            return null;
        }
        return utilisateur;
    }

    /** Un éditeur ou un administrateur peut gérer les articles/catégories. */
    public boolean peutGererContenu(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return false;
        }
        return switch (utilisateur.getRole()) {
            case EDITEUR, ADMINISTRATEUR -> true;
            default -> false;
        };
    }

    /** Seul un administrateur peut gérer les utilisateurs et les jetons. */
    public boolean estAdministrateur(Utilisateur utilisateur) {
        return utilisateur != null && utilisateur.getRole() == sn.actualite.model.Role.ADMINISTRATEUR;
    }
}
