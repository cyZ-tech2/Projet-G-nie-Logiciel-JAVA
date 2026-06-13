package com.groupg.cells2d.controller;

import com.groupg.cells2d.model.user.Researcher;
import com.groupg.cells2d.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button        loginButton;
    @FXML private Text          wrongText;

    private Stage  stage;
    private Scene  scene;
    private Parent root;

    /**
     * Switches to the correct interface if credentials are correct.
     */
    @FXML
    public void login(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        wrongText.setText("");

        if (User.login(username, password)) {
            User user = User.get(username);
            if (user instanceof Researcher) {
                root = FXMLLoader.load(getClass().getResource(
                    "/com/groupg/cells2d/view/researcherInterface.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource(
                    "/com/groupg/cells2d/view/doctorInterface.fxml"));
            }
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            wrongText.setText("Wrong username/password");
        }
    }
}
