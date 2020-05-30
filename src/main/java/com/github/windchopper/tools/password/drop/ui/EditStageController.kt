package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.FormLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.book.BookPart
import com.github.windchopper.tools.password.drop.book.Phrase
import javafx.beans.binding.Bindings
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.concurrent.Callable
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event
import javax.enterprise.event.Observes
import javax.inject.Inject

@ApplicationScoped @Form(Application.FXML_EDIT) class EditStageController: AnyStageController() {

    @Inject private lateinit var formLoadEvent: Event<FormLoad>

    @FXML private lateinit var nameField: TextField
    @FXML private lateinit var textLabel: Label
    @FXML private lateinit var textField: TextField
    @FXML private lateinit var restoreButton: Button

    private var savedName: String? = null
    private var savedText: String? = null

    override fun preferredStageSize(): Dimension2D {
        return Screen.getPrimary().visualBounds
            .let { Dimension2D(it.width / 3, it.height / 4) }
    }

    override fun afterLoad(form: Parent, parameters: MutableMap<String, *>, formNamespace: MutableMap<String, *>) {
        super.afterLoad(form, parameters, formNamespace)

        bind(parameters["bookPart"] as BookPart)

        restoreButton.disableProperty().bind(Bindings.createBooleanBinding(
            Callable { savedName == nameField.text && savedText == textField.text },
            nameField.textProperty(),
            textField.textProperty()))
    }

    fun unbind() {
        nameField.textProperty().unbind()
        textField.textProperty().unbind()
    }

    fun bind(bookPart: BookPart) {
        textLabel.isVisible = false
        textField.isVisible = false

        with (JavaBeanStringPropertyBuilder.create().bean(bookPart).name("name").build()) {
            nameField.textProperty().bindBidirectional(this)
            savedName = get()
        }

        if (bookPart !is Phrase) {
            return
        }

        textLabel.isVisible = true
        textField.isVisible = true

        with (JavaBeanStringPropertyBuilder.create().bean(bookPart).name("text").build()) {
            textField.textProperty().bindBidirectional(this)
            savedText = get()
        }
    }

    fun editFired(@Observes event: TreeEdit<BookPart>) {
        if (stage != null) {
            if (stage.isShowing) {
                stage.toFront()
            } else {
                stage.show()
            }
        } else {
            formLoadEvent.fire(StageFormLoad(ClassPathResource(Application.FXML_EDIT), mutableMapOf("bookPart" to event.item.value)) {
                Stage()
                    .also {
                        it.initStyle(StageStyle.UTILITY)
                        it.initModality(Modality.NONE)
                        it.initOwner(stage)
                        it.isResizable = false
                    }
            })
        }
    }

    fun selectionFired(@Observes event: TreeSelection<BookPart>) {
        if (stage != null && stage.isShowing) {
            stage.toFront()
            unbind()
            event.newSelection
                ?.let { bind(it.value) }
        }
    }

    fun hideFired(@Observes event: TreeHide) {
        stage?.hide()
    }

    @FXML fun restore(event: ActionEvent) {
        nameField.textProperty().set(savedName)
        textField.textProperty().set(savedText)
    }

}