/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package tree;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;

import static java.lang.Math.random;
import static java.lang.Math.sin;
import static javafx.animation.Animation.INDEFINITE;
import static tree.Util.addChildToParent;


public class Animator implements Runnable {

  public static final Duration BRANCH_GROWING_DURATION = Duration.seconds(2);
  public static final Duration GRASS_BECOME_GREEN_DURATION = Duration.seconds(5);
  public static final Duration GRASS_BECOME_YELLOW_DURATION = Duration.seconds(5);
  public static final Duration LEAF_BECOME_YELLOW_DURATION = Duration.seconds(5);
  public static final Duration WIND_CYCLE_DURATION = Duration.seconds(5);
  public static final Duration LEAF_APPEARING_DURATION = Duration.seconds(2);
  public static final Duration FLOWER_APPEARING_DURATION = Duration.seconds(1);
  private final TreeGenerator treeGenerator;
  private final GrassGenerator grassGenerator;

  Animator(TreeGenerator treeGenerator, GrassGenerator grassGenerator) {
    this.treeGenerator = treeGenerator;
    this.grassGenerator = grassGenerator;
  }

  @Override
  public void run() {

    var tree = treeGenerator.generateTree();
    var grass = grassGenerator.generateGrass();

    // branch growing animation
    var branchGrowingAnimation = new SequentialTransition();
    //Wind animation
    var treeWindAnimation = new ParallelTransition();

    for (var i = 0; i < tree.generations.size(); i++) {
      var branchGeneration = tree.generations.get(i);
      branchGrowingAnimation.getChildren().add(animateBranchGrowing(branchGeneration, i, BRANCH_GROWING_DURATION)); //create animation for current crown
      treeWindAnimation.getChildren().add(animateTreeWind(branchGeneration, i, WIND_CYCLE_DURATION));
    }


    // Main animation: grass bending, tree bending, tree growing, seasons changing
    final Transition all = new ParallelTransition(new GrassWindAnimation(grass), treeWindAnimation, new SequentialTransition(branchGrowingAnimation, seasonsAnimation(tree, grass)));
    all.play();

  }

  //Animatation for  growing branches
  private Animation animateBranchGrowing(List<Branch> branchGeneration, int depth, Duration duration) {
    final var sameDepthBranchAnimation = new ParallelTransition();

    for (final var branch : branchGeneration) {
      final var keyValue = new KeyValue(branch.base.endYProperty(), branch.length);
      final var keyFrame = new KeyFrame(duration, keyValue);
      final var branchGrowingAnimation = new Timeline(keyFrame);//line is growing by changinh endY from 0 to brunch.lengt

      final var pauseTransition = new PauseTransition();
      pauseTransition.setDuration(Duration.ONE);
      pauseTransition.setOnFinished(event -> branch.base.setStrokeWidth(branch.length / 25));

      sameDepthBranchAnimation.getChildren().add(
        new SequentialTransition(
          //To set width from 0 to some value we use pause transition with duration.one millisecond
          //trick to show lines
          pauseTransition,
          branchGrowingAnimation
        )
      );
    }

    return sameDepthBranchAnimation;

  }

  // animate wind. Tree is bending
  private Animation animateTreeWind(List<Branch> branchGeneration, int depth, Duration duration) {
    var wind = new ParallelTransition();
    for (final var brunch : branchGeneration) {
      final var rotation = new Rotate(0);
      brunch.getTransforms().add(rotation);

      var timeline = new Timeline();
      var keyValue = new KeyValue(rotation.angleProperty(), depth * 2);

      timeline.getKeyFrames().add(new KeyFrame(duration, keyValue));
      timeline.setAutoReverse(true);
      timeline.setCycleCount(INDEFINITE);

      wind.getChildren().add(timeline);
    }
    return wind;
  }

  private Transition seasonsAnimation(final Tree tree, final List<Blade> grass) {

    var spring = animateSpring(tree.leafage, grass);
    var flowers = animateFlowers(tree.flowers);
    var autumn = animateAutumn(tree.leafage, grass);

    var sequentialTransition = new SequentialTransition();
    sequentialTransition.getChildren().addAll(spring, flowers, autumn);
    sequentialTransition.setCycleCount(INDEFINITE);

    return sequentialTransition;
  }

  private Transition animateSpring(List<Leaf> leafage, List<Blade> grass) {
    var springAnimation = new ParallelTransition();
    for (final var blade : grass) {
      //grass become green

      final var fillTransition = new FillTransition();
      fillTransition.setShape(blade);
      fillTransition.setToValue(blade.SPRING_COLOR);
      fillTransition.setDuration(GRASS_BECOME_GREEN_DURATION);

      springAnimation.getChildren().add(fillTransition);
    }
    for (var leaf : leafage) {
      //leafage appear
      final var scaleTransition = new ScaleTransition();
      scaleTransition.setToX(1);
      scaleTransition.setToY(1);
      scaleTransition.setNode(leaf);
      scaleTransition.setDuration(LEAF_APPEARING_DURATION);

      springAnimation.getChildren().add(scaleTransition);
    }
    return springAnimation;
  }

  private Transition animateFlowers(List<Flower> flowers) {

    var flowersAppearAndFallDown = new ParallelTransition();

    for (var i = 0; i < flowers.size(); i++) {
      final var flower = flowers.get(i);
      for (var pental : flower.getPetals()) {

        final var fadeTransition = new FadeTransition();
        fadeTransition.setDelay(FLOWER_APPEARING_DURATION.divide(3).multiply(i + 1));
        fadeTransition.setDuration(FLOWER_APPEARING_DURATION);
        fadeTransition.setNode(pental);
        fadeTransition.setToValue(1);

        final var sequentialTransition = new SequentialTransition(
          //flowers appearing
          fadeTransition,
          //fall down
          fakeFallDownAnimation(pental)
        );

        flowersAppearAndFallDown.getChildren().add(sequentialTransition);
      }
    }
    return flowersAppearAndFallDown;
  }

  private Transition animateAutumn(List<Leaf> leafage, List<Blade> grass) {
    var autumn = new ParallelTransition();

    //Leafage animation
    var yellowLeafage = new ParallelTransition();
    var dissappearLeafage = new ParallelTransition();

    for (final var leaf : leafage) {

      //become yellow
      final var toYellow = new FillTransition();
      toYellow.setShape(leaf);
      toYellow.setToValue(leaf.AUTUMN_COLOR);
      toYellow.setDuration(LEAF_APPEARING_DURATION);

      yellowLeafage.getChildren().add(toYellow);

      //fall down
      var fakeLeafageDown = fakeFallDownEllipseAnimation(leaf, leaf.AUTUMN_COLOR, node -> {
        node.setScaleX(0);
        node.setScaleY(0);
      });

      final var fillTransition = new FillTransition();
      fillTransition.setShape(leaf);
      fillTransition.setToValue((Color) leaf.getFill());
      fillTransition.setDuration(Duration.ONE);

      //disappear
      final var sequentialTransition = new SequentialTransition(fakeLeafageDown, fillTransition);

      dissappearLeafage.getChildren().add(sequentialTransition);
    }

    //Grass animation
    var grassBecomeYellowAnimation = new ParallelTransition();
    for (final var blade : grass) {
      //become yellow
      final var toYellow = new FillTransition();
      toYellow.setShape(blade);
      toYellow.setToValue(blade.AUTUMN_COLOR);
      toYellow.setDelay(Duration.seconds(1 * random()));
      toYellow.setDuration(GRASS_BECOME_YELLOW_DURATION);

      grassBecomeYellowAnimation.getChildren().add(toYellow);
    }

    autumn.getChildren().addAll(grassBecomeYellowAnimation, new SequentialTransition(yellowLeafage, dissappearLeafage));
    return autumn;
  }

  private Animation fakeFallDownAnimation(final Ellipse pentalOld) {
    return fakeFallDownEllipseAnimation(pentalOld, null, node -> node.setOpacity(0));
  }

  private Animation fakeFallDownEllipseAnimation(final Ellipse sourceEllipse, Color fakeColor, final HideMethod hideMethod) {

    final var fake = copyEllipse(sourceEllipse, fakeColor);
    addChildToParent(treeGenerator.content, fake);

    final var replaceFakeWithSource = new PauseTransition();
    replaceFakeWithSource.setDuration(Duration.ONE);
    replaceFakeWithSource.setDelay(Duration.minutes(0.9 * random() + 0.1));
    replaceFakeWithSource.setOnFinished(event -> {
      final var position = treeGenerator.content.sceneToLocal(sourceEllipse.localToScene(0, 0));

      var sinPath = new DoubleBinding() {

        {
          bind(fake.translateYProperty());
        }

        @Override
        protected double computeValue() {
          return 50 * sin((fake.translateYProperty().doubleValue() - position.getY()) / 20);
        }
      };
      fake.setTranslateY(position.getY());
      fake.setCenterX(0);
      fake.setCenterY(0);
      fake.translateXProperty().bind(sinPath.add(position.getX()));
      fake.rotateProperty().bind(fake.translateYProperty().multiply(2).add(random() * 180));
      //replace source with fake
      fake.setOpacity(1);
      hideMethod.hide(sourceEllipse);
    });

    final var translateTransition = new TranslateTransition();
    translateTransition.setDuration(Duration.seconds(30));
    translateTransition.setToY(random() * 30 + 1);
    translateTransition.setNode(fake);

    final var fadeTransition = new FadeTransition();
    fadeTransition.setToValue(0);
    fadeTransition.setDelay(Duration.seconds(5));
    fadeTransition.setDuration(Duration.seconds(2));
    fadeTransition.setNode(fake);

    return new SequentialTransition(replaceFakeWithSource,
      //fall down
      translateTransition,
      //disappear
      fadeTransition
    );
  }

  private Ellipse copyEllipse(Ellipse petalOld, Color color) {
    var ellipse = new Ellipse();
    ellipse.setRadiusX(petalOld.getRadiusX());
    ellipse.setRadiusY(petalOld.getRadiusY());
    if (color == null) {
      ellipse.setFill(petalOld.getFill());
    } else {
      ellipse.setFill(color);
    }
    ellipse.setRotate(petalOld.getRotate());
    ellipse.setOpacity(0);
    return ellipse;
  }

  private interface HideMethod {
    void hide(Node node);
  }
}
