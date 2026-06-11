package com.groupg.cells2d.view;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Handles Scene changes
 */
public class NavigationManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage){
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath){

    }
}
