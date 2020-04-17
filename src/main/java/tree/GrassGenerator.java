/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package tree;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import static tree.Util.addChildToParent;


class GrassGenerator {

    Group content;
    private final int numBlades;

    public GrassGenerator(Group content, int numBlades) {
        this.content = content;
        this.numBlades = numBlades;
    }

    public List<Blade> generateGrass() {


        List<Blade> grass = new ArrayList<Blade>(numBlades);
        for (int i = 0; i < numBlades; i++) {

            final Blade blade = new Blade();
            grass.add(blade);

            addChildToParent(content, blade);
        }
        return grass;
    }
}
