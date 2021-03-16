/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package io.trydent.treefx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Night extends Application {

  private static final String SOURCE = Night.class.getClassLoader().getResource("Ronkorb_etenraku_ryuteki.wav").toExternalForm();
  private static final int SCENE_WIDTH = 1000;
  private static final int SCENE_HEIGHT = 800;
  private static final int NUMBER_OF_BRANCH_GENERATIONS = 8;
  private static final int NUM_BLADES = 200;
  private Group rootContent;
  private Group grassContent;
  private Group treeContent;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) {
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.sizeToScene();
    stage.setScene(new AppScene());

    //close application
    final Button close = new Button("X");
    close.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    close.setStyle("-fx-background-color:transparent;-fx-text-fill:#ff0000;");
    close.setOpacity(0);
    close.setOnAction(arg0 -> {
      Platform.exit();
      System.exit(0);
    });
    close.setTranslateY(stage.getScene().getHeight() - 20);
    rootContent.getChildren().add(close);

    stage.getScene().setOnMouseEntered(arg0 -> close.setOpacity(1));
    stage.getScene().setOnMouseExited(arg0 -> close.setOpacity(0));
    stage.show();

    // close application
    stage.setOnCloseRequest(t -> {
        Platform.exit();
        System.exit(0);
    });

    new Animator(
      new TreeGenerator(treeContent, NUMBER_OF_BRANCH_GENERATIONS),
      new GrassGenerator(grassContent, NUM_BLADES)
    ).run();

    MediaPlayer sound = new MediaPlayer(new Media(SOURCE));
    sound.setCycleCount(MediaPlayer.INDEFINITE);

    sound.play();

  }

  private class AppScene extends Scene {

    public AppScene() {
      super(rootContent = new Group(), SCENE_WIDTH, SCENE_HEIGHT, Color.TRANSPARENT);
      rootContent.setClip(new Ellipse(0, SCENE_HEIGHT / 2, SCENE_WIDTH / 3, SCENE_HEIGHT / 2)); //Scene shape and size

      final Rectangle background = new Rectangle(-SCENE_WIDTH / 2, 0, SCENE_WIDTH, SCENE_HEIGHT);
      background.setFill(new LinearGradient(0, 0, 0, SCENE_HEIGHT, false, CycleMethod.NO_CYCLE, new Stop(0, Color.YELLOWGREEN), new Stop(0.3, Color.LIGHTBLUE),
        new Stop(1., new Color(1, 1, 1, 0)))); //background color
      rootContent.getChildren().add(background);
      rootContent.getChildren().add(treeContent = new Group()); // io.treefx.tree layout
      rootContent.getChildren().add(grassContent = new Group()); // grass layout
      rootContent.getTransforms().addAll(new Translate(SCENE_WIDTH / 2, SCENE_HEIGHT), new Rotate(180));

    }
  }
}
