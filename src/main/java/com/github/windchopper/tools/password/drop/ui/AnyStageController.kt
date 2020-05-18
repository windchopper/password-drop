package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.StageFormController

import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Modality
import javafx.stage.Stage

abstract class AnyStageController: StageFormController() {

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

    open fun prepareAlert(type: AlertType, message: String? = null, vararg buttons: ButtonType): Alert {
        return Alert(type, null, *buttons)
            .let { alert ->
                alert.initOwner(stage)
                alert.initModality(Modality.APPLICATION_MODAL)

                val alertWindow = alert.dialogPane.scene.window

                if (alertWindow is Stage)
                    alertWindow.icons.addAll(stage.icons)

                message?.let {
                    alert.headerText = message
                }

                alert
            }
    }

}
