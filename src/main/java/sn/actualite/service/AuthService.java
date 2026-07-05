package sn.actualite.service;

import sn.actualite.dao.JetonDao;
import sn.actualite.dao.UtilisateurDao;
import sn.actualite.model.Jeton;
import sn.actualite.model.Utilisateur;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

/**
 * Logique d'authentification (login web + service SOAP d'authentification)
 * et gestion des jetons d'accès aux services web (génération/révocation par un
 * administrateur, validation par util/JetonFilter).
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

    private static final SecureRandom GENERATEUR_ALEATOIRE = new SecureRandom();

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();
    private final JetonDao jetonDao = new JetonDao();

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

    public List<Jeton> listerJetons() {
        return jetonDao.listerTous();
    }

    /** Génère et active un nouveau jeton pour l'utilisateur donné. */
    public Jeton genererJeton(int utilisateurId) {
        Utilisateur utilisateur = utilisateurDao.trouverParId(utilisateurId);
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur introuvable (id=" + utilisateurId + ")");
        }

        Jeton jeton = new Jeton();
        jeton.setUtilisateur(utilisateur);
        jeton.setValeur(genererValeurAleatoire());
        jeton.setActif(true);
        jetonDao.creer(jeton);
        return jeton;
    }

    public void revoquerJeton(int id) {
        jetonDao.definirActif(id, false);
    }

    public void reactiverJeton(int id) {
        jetonDao.definirActif(id, true);
    }

    public void supprimerJeton(int id) {
        jetonDao.supprimer(id);
    }

    /**
     * Valide un jeton présenté par un service web (SOAP/REST) : le jeton doit
     * exister et être actif. Utilisé par util/JetonFilter.
     * @return l'utilisateur propriétaire du jeton, ou null si le jeton est invalide/inactif.
     */
    public Utilisateur validerJeton(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        Jeton jeton = jetonDao.trouverParValeur(valeur.trim());
        if (jeton == null || !jeton.isActif()) {
            return null;
        }
        return jeton.getUtilisateur();
    }

    private String genererValeurAleatoire() {
        byte[] octets = new byte[32];
        GENERATEUR_ALEATOIRE.nextBytes(octets);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
    }
}
