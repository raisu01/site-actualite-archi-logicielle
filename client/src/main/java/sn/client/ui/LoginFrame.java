package sn.client.ui;

import sn.client.ws.SoapClient;
import sn.client.ws.SoapClientException;

import javax.swing.*;
import java.awt.*;

/**
 * Écran de connexion de l'application client : vérifie le login/mot de passe
 * auprès de soap/AuthSoapService (Membre 2), puis ouvre l'écran de gestion des
 * utilisateurs si le compte est administrateur.
 *
 * Le jeton d'accès (généré au préalable par un administrateur via le
 * back-office web, voir controller/admin/GestionJetonsServlet) n'est pas
 * ressaisi ici : soap/AuthSoapService le renvoie automatiquement dans sa
 * réponse quand le compte authentifié en possède un actif.
 */
public class LoginFrame extends JFrame {

    /** Adresse du service SOAP (Membre 2), publié sous "/actualite/ws". */
    private static final String URL_SERVEUR = "http://localhost:8080/actualite/ws";

    private final JTextField champLogin = new JTextField(20);
    private final JPasswordField champMotDePasse = new JPasswordField(20);
    private final JLabel etiquetteMessage = new JLabel(" ");
    private final JButton boutonConnexion = new JButton("Se connecter");

    public LoginFrame() {
        super("ActuSN - Connexion");
        construireInterface();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void construireInterface() {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int ligne = 0;
        ajouterChamp(panneau, c, ligne++, "Login", champLogin);
        ajouterChamp(panneau, c, ligne++, "Mot de passe", champMotDePasse);

        c.gridx = 0;
        c.gridy = ligne;
        c.gridwidth = 2;
        etiquetteMessage.setForeground(Color.RED);
        panneau.add(etiquetteMessage, c);

        ligne++;
        c.gridx = 0;
        c.gridy = ligne;
        c.gridwidth = 2;
        boutonConnexion.addActionListener(e -> seConnecter());
        panneau.add(boutonConnexion, c);

        getRootPane().setDefaultButton(boutonConnexion);
        setContentPane(panneau);
    }

    private void ajouterChamp(JPanel panneau, GridBagConstraints c, int ligne, String etiquette, JComponent champ) {
        c.gridx = 0;
        c.gridy = ligne;
        c.gridwidth = 1;
        panneau.add(new JLabel(etiquette), c);
        c.gridx = 1;
        panneau.add(champ, c);
    }

    private void seConnecter() {
        String login = champLogin.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword());

        if (login.isEmpty() || motDePasse.isEmpty()) {
            afficherMessage("Login et mot de passe sont obligatoires.");
            return;
        }

        boutonConnexion.setEnabled(false);
        afficherMessage(" ");

        SwingWorker<SoapClient.ResultatAuth, Void> tache = new SwingWorker<>() {
            private final SoapClient client = new SoapClient(URL_SERVEUR);
            private Exception erreur;

            @Override
            protected SoapClient.ResultatAuth doInBackground() {
                try {
                    return client.authentifier(login, motDePasse);
                } catch (SoapClientException e) {
                    erreur = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                boutonConnexion.setEnabled(true);

                if (erreur != null) {
                    afficherMessage("Connexion impossible : " + erreur.getMessage());
                    return;
                }

                SoapClient.ResultatAuth resultat;
                try {
                    resultat = get();
                } catch (Exception e) {
                    afficherMessage("Connexion impossible : " + e.getMessage());
                    return;
                }

                if (!resultat.succes()) {
                    afficherMessage(resultat.message() != null ? resultat.message() : "Login ou mot de passe incorrect.");
                    return;
                }

                if (!"ADMINISTRATEUR".equals(resultat.role())) {
                    afficherMessage("Accès réservé aux administrateurs.");
                    return;
                }

                if (resultat.jeton() == null || resultat.jeton().isBlank()) {
                    afficherMessage("Aucun jeton actif pour ce compte : demandez à un administrateur "
                            + "d'en générer un depuis le site web (Administration > Jetons).");
                    return;
                }

                new GestionUtilisateursFrame(client, resultat.jeton(), login).setVisible(true);
                dispose();
            }
        };
        tache.execute();
    }

    private void afficherMessage(String message) {
        etiquetteMessage.setText(message);
    }
}
