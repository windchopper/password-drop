@file:Suppress("NestedLambdaShadowedImplicitParameter", "UNUSED_ANONYMOUS_PARAMETER")

package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.misc.isNotBlank
import com.github.windchopper.tools.password.drop.misc.newInsets
import com.github.windchopper.tools.password.drop.misc.rootCauseMessage
import com.github.windchopper.tools.password.drop.misc.screen
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.binding.Bindings.not
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.GridPane.setHgrow
import javafx.scene.layout.GridPane.setMargin
import javafx.scene.layout.Priority
import java.io.PrintWriter
import java.io.StringWriter

class ErrorAlert(exception: Throwable): Alert(AlertType.ERROR) {

    init {
        title = Application.messages["error.title"]
        headerText = exception.rootCauseMessage(500, true)
        contentText = Application.messages["error.description"]

        with (dialogPane) {
            maxWidth = scene.window.screen().visualBounds.width / 4
            expandableContent = GridPane().also { pane ->
                pane.children.add(TextArea().also { textArea ->
                    textArea.isEditable = false
                    textArea.prefColumnCount = 10
                    textArea.prefRowCount = 10
                    setHgrow(textArea, Priority.ALWAYS)
                    textArea.text = StringWriter().use {
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

class PasswordAlert(newBook: Boolean): Alert(AlertType.NONE) {

    val passwordProperty: StringProperty = SimpleStringProperty(this, "password")

    init {
        graphic = ImageView(Image("/com/github/windchopper/tools/password/drop/images/lock_48.png"))
        title = Application.messages["main.password.enter"]
        headerText = if (newBook) Application.messages["main.password.new"] else Application.messages["main.password.opened"]
        buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        with(dialogPane) {
            val passwordField = PasswordField().also {
                setHgrow(it, Priority.ALWAYS)
                it.prefColumnCount = 20
                it.textProperty().bindBidirectional(passwordProperty)
            }

            val passwordLabel = Label(Application.messages["main.password"]).also {
                setMargin(it, newInsets(right = 8.0))
                it.labelFor = passwordField
            }

            maxWidth = scene.window.screen().visualBounds.width / 4

            scene.window.setOnShown {
                passwordField.requestFocus()
            }

            content = GridPane().also { pane ->
                pane.add(passwordLabel, 0, 0)
                pane.add(passwordField, 1, 0)
            }

            lookupButton(ButtonType.OK).disableProperty().bind(not(createBooleanBinding(
                { passwordField.text?.isNotBlank()?:false },
                passwordField.textProperty())))
        }
    }

}