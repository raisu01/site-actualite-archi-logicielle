package sn.client.ui;

import sn.client.model.UtilisateurDTO;
import sn.client.ws.SoapClient;
import sn.client.ws.SoapClientException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Écran de gestion des comptes utilisateurs (éditeurs/administrateurs) :
 * lister/créer/modifier/supprimer via soap/UtilisateurSoapService (Membre 2),
 * protégé par le jeton d'accès saisi à l'écran de connexion.
 */
public class GestionUtilisateursFrame extends JFrame {

    private final SoapClient client;
    private final String jeton;

    private final DefaultTableModel modeleTable =
            new DefaultTableModel(new Object[]{"Id", "Login", "Rôle"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable table = new JTable(modeleTable);

    public GestionUtilisateursFrame(SoapClient client, String jeton, String loginConnecte) {
        super("ActuSN - Gestion des utilisateurs (" + loginConnecte + ")");
        this.client = client;
        this.jeton = jeton;

        construireInterface();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        rafraichir();
    }

    private void construireInterface() {
        JPanel panneau = new JPanel(new BorderLayout(10, 10));
        panneau.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panneau.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel barreActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton boutonRafraichir = new JButton("Rafraîchir");
        JButton boutonNouveau = new JButton("Nouvel utilisateur");
        JButton boutonModifier = new JButton("Modifier");
        JButton boutonSupprimer = new JButton("Supprimer");

        boutonRafraichir.addActionListener(e -> rafraichir());
        boutonNouveau.addActionListener(e -> ouvrirFormulaire(null));
        boutonModifier.addActionListener(e -> {
            UtilisateurDTO selection = utilisateurSelectionne();
            if (selection != null) {
                ouvrirFormulaire(selection);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur.");
            }
        });
        boutonSupprimer.addActionListener(e -> supprimerSelection());

        barreActions.add(boutonRafraichir);
        barreActions.add(boutonNouveau);
        barreActions.add(boutonModifier);
        barreActions.add(boutonSupprimer);

        panneau.add(barreActions, BorderLayout.NORTH);
        setContentPane(panneau);
    }

    private UtilisateurDTO utilisateurSelectionne() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            return null;
        }
        int id = (int) modeleTable.getValueAt(ligne, 0);
        String login = (String) modeleTable.getValueAt(ligne, 1);
        String role = (String) modeleTable.getValueAt(ligne, 2);
        return new UtilisateurDTO(id, login, role);
    }

    private void rafraichir() {
        SwingWorker<List<UtilisateurDTO>, Void> tache = new SwingWorker<>() {
            private Exception erreur;

            @Override
            protected List<UtilisateurDTO> doInBackground() {
                try {
                    return client.listerUtilisateurs(jeton);
                } catch (SoapClientException e) {
                    erreur = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (erreur != null) {
                    afficherErreur(erreur);
                    return;
                }
                try {
                    remplirTable(get());
                } catch (Exception e) {
                    afficherErreur(e);
                }
            }
        };
        tache.execute();
    }

    private void remplirTable(List<UtilisateurDTO> utilisateurs) {
        modeleTable.setRowCount(0);
        for (UtilisateurDTO u : utilisateurs) {
            modeleTable.addRow(new Object[]{u.getId(), u.getLogin(), u.getRole()});
        }
    }

    private void ouvrirFormulaire(UtilisateurDTO utilisateurEnEdition) {
        FormulaireUtilisateurDialog dialogue =
                new FormulaireUtilisateurDialog(this, utilisateurEnEdition);
        dialogue.setVisible(true);

        UtilisateurDTO resultat = dialogue.getResultat();
        if (resultat == null) {
            return; // annulé
        }

        SwingWorker<Void, Void> tache = new SwingWorker<>() {
            private Exception erreur;

            @Override
            protected Void doInBackground() {
                try {
                    if (utilisateurEnEdition == null) {
                        client.creerUtilisateur(jeton, resultat);
                    } else {
                        client.modifierUtilisateur(jeton, resultat);
                    }
                } catch (SoapClientException e) {
                    erreur = e;
                }
                return null;
            }

            @Override
            protected void done() {
                if (erreur != null) {
                    afficherErreur(erreur);
                } else {
                    rafraichir();
                }
            }
        };
        tache.execute();
    }

    private void supprimerSelection() {
        UtilisateurDTO selection = utilisateurSelectionne();
        if (selection == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer l'utilisateur \"" + selection.getLogin() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Void, Void> tache = new SwingWorker<>() {
            private Exception erreur;

            @Override
            protected Void doInBackground() {
                try {
                    client.supprimerUtilisateur(jeton, selection.getId());
                } catch (SoapClientException e) {
                    erreur = e;
                }
                return null;
            }

            @Override
            protected void done() {
                if (erreur != null) {
                    afficherErreur(erreur);
                } else {
                    rafraichir();
                }
            }
        };
        tache.execute();
    }

    private void afficherErreur(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
