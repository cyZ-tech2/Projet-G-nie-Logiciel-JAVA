package com.groupg.cells2d.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class LoginController {

    // Ces deux lignes permettent de récupérer ce que l'utilisateur tape.
    // (Assure-toi que dans ton fichier login.fxml, les fx:id soient bien "usernameField" et "passwordField")
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {

        // Récupération sécurisée du texte (si les champs ne sont pas encore créés dans le FXML, on met "admin" par défaut)
        String username = (usernameField != null) ? usernameField.getText() : "admin";
        String password = (passwordField != null) ? passwordField.getText() : "admin";

        // ---- REQUISITIONS DU LOGIN TEMPORAIRE ----
        if ("admin".equals(username) && "admin".equals(password)) {
            try {
                // Recherche intelligente du fichier simulation FXML (on teste les deux chemins possibles)
                URL simulationUrl = getClass().getResource("/com/groupg/cells2d/simulation.fxml");
                if (simulationUrl == null) {
                    simulationUrl = getClass().getResource("/com/groupg/cells2d/view/simulation.fxml");
                }

                // Si aucun des deux chemins ne fonctionne
                if (simulationUrl == null) {
                    throw new IOException("Fichier 'simulation.fxml' introuvable.\nVérifie qu'il est bien dans src/main/resources/com/groupg/cells2d/");
                }


                FXMLLoader fxmlLoader = new FXMLLoader(simulationUrl);
                Scene simulationScene = new Scene(fxmlLoader.load(), 800, 800);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setTitle("Simulation Épidémiologique SEIR 2D");
                stage.setScene(simulationScene);
                stage.centerOnScreen(); // Centre la fenêtre de simulation qui est plus grande
                stage.show();

            } catch (IOException e) {
                afficherErreur("Erreur d'ouverture", "Impossible de charger la simulation : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            afficherErreur("Connexion échouée", "Identifiants incorrects !\n\nUtilise :\nIdentifiant : admin\nMot de passe : admin");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}