package sn.actualite.soap;

import jakarta.jws.WebService;
import sn.actualite.model.Role;
import sn.actualite.model.Utilisateur;
import sn.actualite.service.UtilisateurService;

import java.util.List;
import java.util.stream.Collectors;

@WebService(endpointInterface = "sn.actualite.soap.UtilisateurSoapService",
        targetNamespace = SoapConstantes.NS, serviceName = "UtilisateurSoapService")
public class UtilisateurSoapServiceImpl implements UtilisateurSoapService {

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @Override
    public List<UtilisateurWs> listerUtilisateurs() {
        return utilisateurService.listerTous().stream()
                .map(u -> new UtilisateurWs(u.getId(), u.getLogin(), u.getRole().name()))
                .collect(Collectors.toList());
    }

    @Override
    public int creerUtilisateur(String login, String motDePasse, String role) throws UtilisateurSoapException {
        Role roleValide = lireRole(role);
        if (login == null || login.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            throw new UtilisateurSoapException("Login et mot de passe sont obligatoires.");
        }
        if (utilisateurService.trouverParLogin(login.trim()) != null) {
            throw new UtilisateurSoapException("Ce login existe deja : " + login);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(login.trim());
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setRole(roleValide);
        return utilisateurService.creer(utilisateur);
    }

    @Override
    public void modifierUtilisateur(int id, String login, String motDePasse, String role) throws UtilisateurSoapException {
        Utilisateur utilisateur = utilisateurService.trouverParId(id);
        if (utilisateur == null) {
            throw new UtilisateurSoapException("Utilisateur introuvable (id=" + id + ")");
        }
        Role roleValide = lireRole(role);

        utilisateur.setLogin(login != null && !login.isBlank() ? login.trim() : utilisateur.getLogin());
        utilisateur.setRole(roleValide);
        if (motDePasse != null && !motDePasse.isBlank()) {
            utilisateur.setMotDePasse(motDePasse);
        }
        utilisateurService.modifier(utilisateur);
    }

    @Override
    public void supprimerUtilisateur(int id) throws UtilisateurSoapException {
        if (utilisateurService.trouverParId(id) == null) {
            throw new UtilisateurSoapException("Utilisateur introuvable (id=" + id + ")");
        }
        utilisateurService.supprimer(id);
    }

    private Role lireRole(String role) throws UtilisateurSoapException {
        if (role == null) {
            throw new UtilisateurSoapException("Role invalide (attendu : VISITEUR, EDITEUR ou ADMINISTRATEUR).");
        }
        try {
            return Role.valueOf(role.trim());
        } catch (IllegalArgumentException e) {
            throw new UtilisateurSoapException("Role invalide (attendu : VISITEUR, EDITEUR ou ADMINISTRATEUR).");
        }
    }
}
