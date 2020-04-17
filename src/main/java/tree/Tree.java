/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. DO NOT
 * ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package tree;

import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;


public class Tree extends Group {


  List<List<Branch>> generations = new ArrayList<>();
  List<Branch> crown = new ArrayList<>();// This branches  doesn't have child branches
  List<Flower> flowers = new ArrayList<>();
  List<Leaf> leafage = new ArrayList<>();

  public Tree(int depth) {
    for (var i = 0; i < depth; i++) {
      generations.add(new ArrayList<>());
    }
  }
}
