package com.example.javafxprojectcsa.Controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class EndViewController implements Initializable {
    public Label endResultLabel;
    public Button quitBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String text = "You made " + GameViewController.successfulShots + " ";

        text += GameViewController.successfulShots == 1 ? "shot!" : "shots!";

        endResultLabel.setText(text);
    }

    public void onQuitBtnClick() {
        System.exit(0);
    }
}
