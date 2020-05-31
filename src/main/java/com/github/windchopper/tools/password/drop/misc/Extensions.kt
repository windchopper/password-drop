package com.github.windchopper.tools.password.drop.misc

import com.github.windchopper.common.fx.dialog.ExceptionDialog
import com.github.windchopper.common.fx.dialog.ExceptionDialogModel
import com.github.windchopper.tools.password.drop.ui.Controller
import javafx.application.Platform
import javafx.beans.property.Property

fun <T> T.display(stageController: Controller) where T: Throwable = Platform.runLater {
    ExceptionDialog(stageController.prepareModalDialogFrame(), ExceptionDialogModel()
        .also { it.exception = this })
        .show()
}

fun Controller.exceptionally(runnable: () -> Unit) = try {
    runnable.invoke()
} catch (thrown: Exception) {
    thrown.display(this)
}

fun <T> Controller.exceptionally(supplier: () -> T, defaultSupplier: () -> T): T = try {
    supplier.invoke()
} catch (thrown: Exception) {
    thrown.display(this)
    defaultSupplier.invoke()
}

private object PropertyExternals {

    val boundProperties: MutableMap<Property<*>, MutableSet<Property<*>>> = HashMap()

}

fun <T> Property<T>.bindBidirectionalAndRemember(property: Property<T>) {
    with (PropertyExternals) {
        boundProperties.getOrPut(this@bindBidirectionalAndRemember) { HashSet() }.add(property)
        bindBidirectional(property)
    }
}

fun <T> Property<T>.unbindBidirectionalAndForget() {
    with (PropertyExternals) {
        boundProperties.remove(this@unbindBidirectionalAndForget)?.forEach {
            @Suppress("UNCHECKED_CAST") unbindBidirectional(it as Property<T>)
        }
    }
}