package com.github.windchopper.tools.password.drop.misc

import javafx.scene.control.TreeItem

class TreeEdit<T>(val item: TreeItem<T>)

class TreeSelection<T>(val oldSelection: TreeItem<T>?, val newSelection: TreeItem<T>?)

class TreeUpdateRequest

class MainHide

class Exit
