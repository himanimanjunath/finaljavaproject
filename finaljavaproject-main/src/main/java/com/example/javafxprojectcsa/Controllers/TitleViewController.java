package com.example.javafxprojectcsa.Controllers;

import com.example.javafxprojectcsa.Application;
import javafx.scene.control.Button;

public class TitleViewController {
    public Button startBtn;

    public void onStartBtnClick() {
        Application.loadScene("/FXML/game-view.fxml", startBtn, null);
    }
}
