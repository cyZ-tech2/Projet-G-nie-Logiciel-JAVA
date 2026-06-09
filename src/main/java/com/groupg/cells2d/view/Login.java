package com.groupg.cells2d.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Login extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            //Parent root = FXMLLoader.load(getClass().getResource("researcherInterface.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
