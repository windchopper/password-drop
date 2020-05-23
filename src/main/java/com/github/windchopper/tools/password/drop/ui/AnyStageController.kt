package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.common.fx.dialog.DialogFrame
import com.github.windchopper.common.fx.dialog.StageDialogFrame
import javafx.scene.Parent
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

    open fun prepareModalDialogFrame(): DialogFrame {
        return StageDialogFrame(Stage()
            .also {
                it.initOwner(stage)
                it.initModality(Modality.WINDOW_MODAL)
                it.icons.addAll(stage.icons)
                it.isResizable = false
            })
    }

}
