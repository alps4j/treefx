/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package tree;

import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

import static tree.Util.addChildToParent;


class GrassGenerator {

  private final int numBlades;
  Group content;

  public GrassGenerator(Group content, int numBlades) {
    this.content = content;
    this.numBlades = numBlades;
  }

  public List<Blade> generateGrass() {


    List<Blade> grass = new ArrayList<Blade>(numBlades);
    for (var i = 0; i < numBlades; i++) {

      final var blade = new Blade();
      grass.add(blade);

      addChildToParent(content, blade);
    }
    return grass;
  }
}
