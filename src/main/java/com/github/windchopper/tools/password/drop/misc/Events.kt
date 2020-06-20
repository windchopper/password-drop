package com.github.windchopper.tools.password.drop.misc

import com.github.windchopper.tools.password.drop.ui.Controller
import javafx.scene.control.TreeItem

class TreeEdit<T>(val invokerController: Controller, val item: TreeItem<T>)

class TreeSelection<T>(val invokerController: Controller, val oldSelection: TreeItem<T>?, val newSelection: TreeItem<T>?)

class TreeUpdateRequest

class MainHide

class Exit
