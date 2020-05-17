package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.behavior.WindowMoveResizeBehavior
import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.book.BookPart
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.StageStyle
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped @Form(Application.FXML_MAIN) class MainStageController: StageFormController() {

    @FXML protected lateinit var outerContainer: BorderPane
    @FXML protected lateinit var innerContainer: BorderPane
    @FXML protected lateinit var bookView: TreeView<BookPart>

    override fun preferredStageSize(): Dimension2D {
        return Screen.getPrimary().visualBounds
            .let { Dimension2D(it.width / 6, it.height / 3) }
    }

    override fun afterLoad(form: Parent?, parameters: MutableMap<String, *>, formNamespace: MutableMap<String, *>) {
        super.afterLoad(form, parameters, formNamespace)

        Image("/com/github/windchopper/tools/password/drop/images/keys-48.png")
            .let { stage.icons.add(it) }

        with (stage) {
            initStyle(StageStyle.TRANSPARENT)
            title = Application.messages["main.title"]

            with (scene) {
                fill = Color.TRANSPARENT
                stylesheets.add("/com/github/windchopper/tools/password/drop/styles.css")
            }
        }

        WindowMoveResizeBehavior(innerContainer)
            .apply(innerContainer)
    }

}