package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.tools.password.drop.misc.screen
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.*

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

    open fun <A: Alert> prepareAlert(alert: A, modality: Modality? = Modality.WINDOW_MODAL, centerOnScreen: Screen? = stage.screen()): A {
        return with (alert) {
            initModality(modality)
            initOwner(stage)

            dialogPane.scene.window.let { window ->
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

            this
        }
    }

    open fun prepareChildStage(childStage: Stage = Stage(), modality: Modality = Modality.NONE, style: StageStyle = StageStyle.UNIFIED): Stage {
        return childStage.also {
            it.initModality(modality)
            it.initStyle(style)
            it.initOwner(stage)
        }
    }

}
