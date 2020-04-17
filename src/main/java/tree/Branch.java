/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package tree;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Rotate;
import static tree.RandomUtil.*;
import static tree.Util.addChildToParent;


public class Branch extends Group {

    public Line base;
    public double length;
    public SimpleDoubleProperty globalAngle = new SimpleDoubleProperty(90);
    public double globalH = 0; //used for change color of top leaves

    public enum Type {

        TOP, LEFT, RIGHT;
    }

    public Branch() { //default constructor for root branch
        base = new Line();
        getChildren().add(base);
        setTranslateY(30);
        length = 150;
        globalH = 0;
        setBranchStyle(0);
    }

    public Branch(Branch parentBranch, Type type, int depth) {
        this();
        SimpleDoubleProperty locAngle = new SimpleDoubleProperty(0);
        globalAngle.bind(locAngle.add(parentBranch.globalAngle.get()));
        double transY = 0; //place of beggining child branch
        switch (type) {
            case TOP: //creates top branch
                transY = parentBranch.length;
                length = parentBranch.length * 0.8;
                locAngle.set(getRandom(10));
                break;
            case LEFT: //create left branch
            case RIGHT: //create right branch
                transY = parentBranch.length - getGaussianRandom(0, parentBranch.length, parentBranch.length / 10, parentBranch.length / 10);
                locAngle.set(getGaussianRandom(35, 10) * (Type.LEFT == type ? 1 : -1));
                if ((0 > globalAngle.get() || globalAngle.get() > 180) && depth < 4) {
                    length = parentBranch.length * getGaussianRandom(0.3, 0.1); //branches pointed down are shorter
                } else {
                    length = parentBranch.length * 0.6;
                }
                break;
        }
        setTranslateY(transY);
        getTransforms().add(new Rotate(locAngle.get(), 0, 0)); // rotate branch to local angle relative to parent branch        
        globalH = getTranslateY() * cos(PI / 2 - parentBranch.globalAngle.get() * PI / 180) + parentBranch.globalH;
        setBranchStyle(depth);
        addChildToParent(parentBranch, this);
    }

    private void setBranchStyle(int depth) {
        base.setStroke(Color.color(0.4, 0.1, 0.1, 1));

        if (depth < 5) { //line rendering optimization
            base.setStrokeLineJoin(StrokeLineJoin.ROUND);
            base.setStrokeLineCap(StrokeLineCap.ROUND);
        }
        base.setStrokeWidth(0); //trick to hide lines
    }
}
