package com.groupg.cells2d;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/groupg/cells2d/view/login.fxml"));

        // On ne force aucune taille ici pour respecter le carré 600x600 de ton FXML
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Connexion - Simulation");
        stage.setScene(scene);
        stage.setResizable(false); // Bloque la fenêtre pour garder le carré parfait
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}