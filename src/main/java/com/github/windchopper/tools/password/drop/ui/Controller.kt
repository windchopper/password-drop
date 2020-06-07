package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.misc.left
import com.github.windchopper.tools.password.drop.misc.right
import com.github.windchopper.tools.password.drop.misc.trimToNull
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.io.PrintWriter
import java.io.StringWriter

abstract class Controller: StageFormController() {

    override fun afterLoad(form: Parent, parameters: MutableMap<String, *>, formNamespace: MutableMap<String, *>) {
        super.afterLoad(form, parameters, formNamespace)

        listOf(
            Image("/com/github/windchopper/tools/password/drop/images/keys_16.png"),
            Image("/com/github/windchopper/tools/password/drop/images/keys_24.png"),
            Image("/com/github/windchopper/tools/password/drop/images/keys_32.png"),
            Image("/com/github/windchopper/tools/password/drop/images/keys_48.png"))
                .also {
                    stage.icons.addAll(it)
                }
    }

    open fun prepareAlert(type: AlertType, modality: Modality? = Modality.WINDOW_MODAL, centerOnScreen: Screen? = null): Alert {
        return Alert(type).also { alert ->
            alert.initModality(modality)
            alert.initOwner(stage)

            alert.dialogPane.scene.window.let { window ->
                if (window is Stage) {
                    window.icons.addAll(stage.icons)
                }

                centerOnScreen?.let { screen ->
                    window.addEventHandler(WindowEvent.WINDOW_SHOWN) {
                        window.x = (screen.visualBounds.width - window.width) / 2
                        window.y = (screen.visualBounds.height - window.height) / 2
                    }
                }
            }
        }
    }

    open fun prepareErrorAlert(modality: Modality? = Modality.WINDOW_MODAL, exception: Throwable): Alert {
        return prepareAlert(AlertType.ERROR, modality).also { alert ->
            alert.title = Application.messages["error.title"]
            alert.headerText = exception.localizedMessage?.trimToNull()?.left(50, true)
                ?:exception::class.qualifiedName?.right(50, true)
            alert.contentText = Application.messages["error.description"]
            alert.dialogPane.expandableContent = GridPane().also {
                it.children.add(TextArea().also {
                    it.isEditable = false
                    it.prefColumnCount = 10
                    it.prefRowCount = 10
                    GridPane.setHgrow(it, Priority.ALWAYS)
                    it.text = StringWriter().use {
                        PrintWriter(it).use {
                            exception.printStackTrace(writer = it)
                        }

                        it.toString()
                    }
                })
            }
        }
    }

}
