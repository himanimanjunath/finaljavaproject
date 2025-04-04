package com.example.javafxprojectcsa.Controllers;

import com.example.javafxprojectcsa.Application;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Pane;
import javafx.scene.shape.*;

import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class GameViewController implements Initializable {
    private double angle;
    private double initialX, initialY;
    public static int successfulShots = 0;

    @FXML
    private TextField angleText;
    @FXML
    private Label errorLabel;
    @FXML
    private Button shootBtn, continueBtn, angleBtn, exitBtn;
    @FXML
    private ImageView hoopImage, ballImage, personImage;
    @FXML
    private Label shotsMadeLabel, shotResultLabel;
    @FXML
    private QuadCurve quadCurve;
    @FXML
    private Pane borderPane;

    // Sets images upon loading of the screen and the default position of the ball
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("src/main/resources/Image/hoopImage.png");
        Image image = new Image(file.toURI().toString());
        hoopImage.setImage(image);

        File file1 = new File("src/main/resources/Image/basketball.png");
        Image image1 = new Image(file1.toURI().toString());
        ballImage.setImage(image1);

        File file2 = new File("src/main/resources/Image/personImage.png");
        Image image2 = new Image(file2.toURI().toString());
        personImage.setImage(image2);

        initialX = ballImage.getLayoutX();
        initialY = ballImage.getLayoutY();
    }

    @FXML
    public void onExitBtnClick() {
        Application.loadScene("/FXML/end-view.fxml", shootBtn, null);
    }

    @FXML
    private void onAngleBtnClick() {
        // If the user hasn't already made a shot
        if (!continueBtn.isVisible()) {
            shotResultLabel.setVisible(false);
            errorLabel.setVisible(false);
            String input = angleText.getText();

            try {
                double number = Double.parseDouble(input);

                if (number < 0 || number > 90) {
                    errorLabel.setText("Invalid input.");
                    shootBtn.setVisible(false);
                } else {
                    errorLabel.setText("Angle entered is: " + number);
                    shootBtn.setVisible(true);

                    angle = number * Math.PI / 180;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Invalid input.");
                shootBtn.setVisible(false);
            }
            errorLabel.setVisible(true);
        }
        angleText.clear();
    }

    @FXML
    private void onShootBtnClick(){
        // If the user hasn't already made a shot
        if (!continueBtn.isVisible()) {
            double endX;
            double startX = initialX;
            double startY = initialY;
            int v1 = 20;

            // Calculates the end X value of the ball
            endX = 10 * (2 * Math.pow(v1, 2) * Math.cos(angle) * Math.sin(angle)) / 9.8;

            calculateShotPath(startX, startY, endX, v1);

            shotsMadeLabel.setText("Shots Made: " + successfulShots);
            shootBtn.setVisible(false);
            errorLabel.setVisible(false);
        }
    }

    private void calculateShotPath(double startX, double startY, double endX, int v1) {
        int numPoints = 100;
        double[] pathX = new double[numPoints];
        double[] pathY = new double[numPoints];

        double y = startY;

        // Calculates each coordinate in the path
        for (int i = 1; i < numPoints; i++) {
            double x = i * (startX + endX) / numPoints;

            if (i <= numPoints / 2)
            {
                y = y - 2;
            } else {
                y = y + 2;
            }
            pathX[i] = x;
            pathY[i] = y;
        }

        double height = startX / (v1 * Math.cos(angle));
        double midX = (startX + endX)/2;
        double midY = startY - 2*(startX*Math.tan(angle) + 0.5*9.8*Math.pow(height, 2));
        // Calculates the peak of the arc
        var cpX = 2 * midX - pathX[0]/2 - pathX[99]/2;
        var cpY = 2 * midY - pathY[0]/2 - pathY[99]/2;

        // Creates a curve using the calculated values
        quadCurve.setStartX(initialX);
        quadCurve.setStartY(initialY);
        quadCurve.setControlX(cpX);
        quadCurve.setControlY(cpY);
        quadCurve.setEndY(pathY[99]);
        quadCurve.setEndX(pathX[99]);

        // Sets up the animation
        PathTransition pathTransitionTest = new PathTransition();
        pathTransitionTest.setNode(ballImage);
        pathTransitionTest.setDuration(Duration.seconds(3));
        pathTransitionTest.setPath(quadCurve);

        // Checks for the center of the ball colliding with the center of the hoop
        AnimationTimer collisionChecker = new AnimationTimer() {
            @Override
            public void handle(long now) {
            Bounds ballBounds = ballImage.getBoundsInParent();
            double ballCenterX = ballBounds.getMinX() + ballBounds.getWidth() / 2;
            double ballCenterY = ballBounds.getMinY() + ballBounds.getHeight() / 2;

            Bounds hoopBounds = hoopImage.getBoundsInParent();
            double hoopCenterX = hoopBounds.getMinX() + hoopBounds.getWidth() / 2;
            double hoopCenterY = hoopBounds.getMinY() + hoopBounds.getHeight() / 2;

            double distance = Math.sqrt(Math.pow(ballCenterX - hoopCenterX, 2) + Math.pow(ballCenterY - hoopCenterY, 2));

            // If the distance between the centers is less than 15 pixels, it is a successful shot
            if (distance < 15) {
                successfulShots++;
                shotsMadeLabel.setText("Shots Made: " + successfulShots);

                this.stop();
                shotResultLabel.setText("Nice Shot!");
                shotResultLabel.setVisible(true);
                continueBtn.setVisible(true);
            }
            }
        };

        pathTransitionTest.setOnFinished(event -> collisionChecker.stop());

        pathTransitionTest.play();
        collisionChecker.start();
    }

    // Randomizes the hoop's position and resets the UI
    public void onContinueBtnPress() {
        double minY = hoopImage.getFitHeight();
        double maxY = 30;

        Random rand = new Random();
        double newY = minY + (maxY - minY) * rand.nextDouble();

        hoopImage.setLayoutX(rand.nextDouble(150, 432));
        hoopImage.setLayoutY(newY);

        ballImage.setLayoutX(initialX);
        ballImage.setLayoutY(initialY);

        continueBtn.setVisible(false);
        shotResultLabel.setVisible(false);
    }
}