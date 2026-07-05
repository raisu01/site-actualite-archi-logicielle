package sn.client.ui;

import sn.client.model.UtilisateurDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Boîte de dialogue de création/modification d'un utilisateur, utilisée par
 * GestionUtilisateursFrame. Renvoie le DTO saisi via {@link #getResultat()},
 * ou {@code null} si l'utilisateur a annulé.
 */
class FormulaireUtilisateurDialog extends JDialog {

    private final JTextField champLogin = new JTextField(20);
    private final JPasswordField champMotDePasse = new JPasswordField(20);
    private final JComboBox<String> champRole = new JComboBox<>(new String[]{"EDITEUR", "ADMINISTRATEUR"});

    private final boolean modification;
    private final int idEnEdition;
    private UtilisateurDTO resultat;

    FormulaireUtilisateurDialog(Frame parent, UtilisateurDTO utilisateurEnEdition) {
        super(parent, utilisateurEnEdition == null ? "Nouvel utilisateur" : "Modifier l'utilisateur", true);
        this.modification = utilisateurEnEdition != null;
        this.idEnEdition = modification ? utilisateurEnEdition.getId() : 0;

        if (modification) {
            champLogin.setText(utilisateurEnEdition.getLogin());
            champRole.setSelectedItem(utilisateurEnEdition.getRole());
        }

        construireInterface();
        pack();
        setLocationRelativeTo(parent);
    }

    private void construireInterface() {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        ajouterChamp(panneau, c, 0, "Login", champLogin);
        ajouterChamp(panneau, c, 1,
                modification ? "Mot de passe (laisser vide pour ne pas changer)" : "Mot de passe",
                champMotDePasse);
        ajouterChamp(panneau, c, 2, "Rôle", champRole);

        JButton boutonEnregistrer = new JButton("Enregistrer");
        JButton boutonAnnuler = new JButton("Annuler");
        boutonEnregistrer.addActionListener(e -> valider());
        boutonAnnuler.addActionListener(e -> dispose());

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        boutons.add(boutonAnnuler);
        boutons.add(boutonEnregistrer);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        panneau.add(boutons, c);

        getRootPane().setDefaultButton(boutonEnregistrer);
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

    private void valider() {
        String login = champLogin.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword());
        String role = (String) champRole.getSelectedItem();

        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le login est obligatoire.");
            return;
        }
        if (!modification && motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire.");
            return;
        }

        UtilisateurDTO dto = new UtilisateurDTO(idEnEdition, login, role);
        dto.setMotDePasse(motDePasse.isEmpty() ? null : motDePasse);
        this.resultat = dto;
        dispose();
    }

    UtilisateurDTO getResultat() {
        return resultat;
    }
}
