
/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package tree;

import static java.lang.Math.*;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Transform;


public class Blade extends Path {

    public final Color SPRING_COLOR = Color.color(random() * 0.5, random() * 0.5 + 0.5, 0.).darker();
    public final Color AUTUMN_COLOR = Color.color(random() * 0.4 + 0.3, random() * 0.1 + 0.4, random() * 0.2);
    private final static double width = 3;
    private double x = RandomUtil.getRandom(170); // width of grass ground
    private double y = RandomUtil.getRandom(20) + 20; // height of grass ground
    private double h = (50 * 1.5 - y / 2) * RandomUtil.getRandom(0.3);   // height of blade
    public SimpleDoubleProperty phase = new SimpleDoubleProperty(); //phase of blade movement

    public Blade() {

        getElements().add(new MoveTo(0, 0));
        final QuadCurveTo curve1;
        final QuadCurveTo curve2;
        getElements().add(curve1 = new QuadCurveTo(-10, h, h / 4, h));
        getElements().add(curve2 = new QuadCurveTo(-10, h, width, 0));

        setFill(AUTUMN_COLOR); //autumn color of blade
        setStroke(null);

        getTransforms().addAll(Transform.translate(x, y));

        curve1.yProperty().bind(new DoubleBinding() {

            {
                super.bind(curve1.xProperty());
            }

            @Override
            protected double computeValue() {

                final double xx0 = curve1.xProperty().get();
                return Math.sqrt(h * h - xx0 * xx0);
            }
        }); //path of top of blade is circle

        //code to bend blade
        curve1.controlYProperty().bind(curve1.yProperty().add(-h / 4));
        curve2.controlYProperty().bind(curve1.yProperty().add(-h / 4));

        curve1.xProperty().bind(new DoubleBinding() {

            final double rand = RandomUtil.getRandom(PI / 4); // to separate blade movement

            {
                super.bind(phase);
            }

            @Override
            protected double computeValue() {
                //calculating shift x for top of blade
                return (h / 4) + ((cos(phase.get() + (x + 400.) * PI / 1600 + rand) + 1) / 2.) * (-3. / 4) * h;
            }
        });
    }
}
