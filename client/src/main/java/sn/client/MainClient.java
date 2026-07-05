package sn.client;

import sn.client.ui.LoginFrame;

import javax.swing.*;

/** Point d'entrée de l'application client (partie 3 : gestion des utilisateurs via SOAP). */
public class MainClient {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
