package com.groupg.cells2d.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Login extends Application{
    public void start(Stage primaryStage) {
        try {
            Stage stage = new Stage();
            // Prototype: open the researcher map directly.
            // Switch back to login.fxml when the login flow is ready.
            Parent root = FXMLLoader.load(getClass().getResource("researcherInterface.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
