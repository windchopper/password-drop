package com.github.windchopper.tools.password.drop.misc

import com.github.windchopper.tools.password.drop.ui.Controller
import com.github.windchopper.tools.password.drop.ui.ErrorAlert
import javafx.application.Platform
import javafx.beans.property.Property
import javafx.geometry.Insets
import javafx.stage.Modality
import javafx.stage.Screen
import javafx.stage.Window
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun String.trimToNull(): String? = with (trim()) {
    return if (length == 0) null else this
}

fun String.isNotBlank(): Boolean = with (trim()) {
    return length > 0
}

fun String.uncapitalize(): String {
    return if (length > 0) {
        this[0].toLowerCase() + substring(1)
    } else {
        this
    }
}

fun String.left(maxLength: Int, ellipsis: Boolean? = false): String = if (length > maxLength) {
    if (ellipsis == true) {
        substring(0, maxLength - 3) + "..."
    } else {
        substring(0, maxLength)
    }
} else {
    this
}

fun String.right(maxLength: Int, ellipsis: Boolean? = false): String = if (length > maxLength) {
    if (ellipsis == true) {
        "..." + substring(length - maxLength - 3)
    } else {
        substring(length - maxLength)
    }
} else {
    this
}

fun <T> T.display(controller: Controller) where T: Throwable = Platform.runLater {
    controller.prepareAlert(ErrorAlert(this), Modality.APPLICATION_MODAL)
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

suspend fun <T> runWithFxThread(action: () -> T): T {
    var result: T? = null
    val locker = Mutex(true)

    Platform.runLater {
        result = action.invoke()
        locker.unlock()
    }

    return locker.withLock {
        result!!
    }
}

fun Window.screen(): Screen {
    return Screen.getScreensForRectangle(x, y, width, height).firstOrNull()
        ?:Screen.getPrimary()
}

fun newInsets(top: Double = 0.0, right: Double = 0.0, bottom: Double = 0.0, left: Double = 0.0): Insets {
    return Insets(top, right, bottom, left)
}
