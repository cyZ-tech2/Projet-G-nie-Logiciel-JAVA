package com.groupg.cells2d.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Controller {

    /**
     * Gère la transition vers l'écran de simulation lors du clic sur Login
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        try {
            // Récupération du Stage actuel
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Chargement de la vue de la simulation
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/groupg/cells2d/view/simulation.fxml"));

            // On laisse la scène s'adapter à la taille de ton simulation.fxml sans imposer de dimensions restrictives
            Scene simulationScene = new Scene(fxmlLoader.load());

            stage.setScene(simulationScene);
            stage.setResizable(false); // Conserve le format carré de ta grille
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur critique lors du chargement de la simulation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}