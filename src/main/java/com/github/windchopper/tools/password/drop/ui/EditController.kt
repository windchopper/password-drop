package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.FormLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.book.BookPart
import com.github.windchopper.tools.password.drop.book.Phrase
import com.github.windchopper.tools.password.drop.misc.*
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

@ApplicationScoped @Form(Application.FXML_EDIT) class EditController: Controller() {

    @Inject private lateinit var formLoadEvent: Event<FormLoad>
    @Inject private lateinit var treeUpdateRequestEvent: Event<TreeUpdateRequest>

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

        nameField.textProperty().addListener { property, oldValue, newValue ->
            treeUpdateRequestEvent.fire(TreeUpdateRequest())
        }

        restoreButton.disableProperty().bind(Bindings.createBooleanBinding(
            Callable { savedName == nameField.text && savedText == textField.text },
            nameField.textProperty(),
            textField.textProperty()))
    }

    fun unbind() {
        nameField.textProperty().unbindBidirectionalAndForget()
        textField.textProperty().unbindBidirectionalAndForget()
    }

    fun bind(bookPart: BookPart) {
        textLabel.isVisible = false
        textField.isVisible = false

        with (JavaBeanStringPropertyBuilder.create().bean(bookPart).name("name").build()) {
            savedName = get()
            nameField.textProperty().bindBidirectionalAndRemember(this)
        }

        if (bookPart !is Phrase) {
            return
        }

        textLabel.isVisible = true
        textField.isVisible = true

        with (JavaBeanStringPropertyBuilder.create().bean(bookPart).name("text").build()) {
            savedText = get()
            textField.textProperty().bindBidirectionalAndRemember(this)
        }
    }

    fun editFired(@Observes event: TreeEdit<BookPart>) {
        if (stage != null) if (stage.isShowing) stage.toFront() else stage.show() else
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

    fun selectionFired(@Observes event: TreeSelection<BookPart>) {
        if (stage != null && stage.isShowing) {
            stage.toFront()
            unbind()
            event.newSelection
                ?.let {
                    bind(it.value)
                }
        }
    }

    fun hideFired(@Observes event: MainHide) {
        stage?.hide()
    }

    @FXML fun restore(event: ActionEvent) {
        nameField.textProperty().set(savedName)
        textField.textProperty().set(savedText)
    }

}