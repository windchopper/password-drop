package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.book.BookPart
import com.github.windchopper.tools.password.drop.book.Phrase
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Screen
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped @Form(Application.FXML_EDIT) class EditStageController: AnyStageController() {

    @FXML private lateinit var textLabel: Label
    @FXML private lateinit var textField: TextField

    private var bookPart: BookPart? = null

    override fun preferredStageSize(): Dimension2D {
        return Screen.getPrimary().visualBounds
            .let { Dimension2D(it.width / 3, it.height / 4) }
    }

    override fun afterLoad(form: Parent, parameters: MutableMap<String, *>, formNamespace: MutableMap<String, *>) {
        super.afterLoad(form, parameters, formNamespace)

        bookPart = parameters["bookPart"] as BookPart

        if (bookPart is Phrase) {
            textLabel.isVisible = true
            textField.isVisible = true
            textField.text = (bookPart as Phrase).text
        } else {
            textLabel.isVisible = false
            textField.isVisible = false
        }
    }

}