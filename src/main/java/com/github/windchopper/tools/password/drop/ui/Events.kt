package com.github.windchopper.tools.password.drop.ui

import javafx.scene.control.TreeItem

class TreeEdit<T>(val item: TreeItem<T>)

class TreeSelection<T>(val oldSelection: TreeItem<T>?, val newSelection: TreeItem<T>?)

class TreeHide