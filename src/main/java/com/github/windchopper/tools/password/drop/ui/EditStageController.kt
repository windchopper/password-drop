package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.common.fx.cdi.form.FormLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.book.BookPart
import com.github.windchopper.tools.password.drop.book.Phrase
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event
import javax.enterprise.event.Observes
import javax.inject.Inject

@ApplicationScoped @Form(Application.FXML_EDIT) class EditStageController: AnyStageController() {

    @Inject private lateinit var formLoadEvent: Event<FormLoad>

    @FXML private lateinit var nameField: TextField
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
        reinitialize()
    }

    fun reinitialize() {
        (bookPart is Phrase)
            .let { bookPartIsPhrase ->
                textLabel.isVisible = bookPartIsPhrase
                textField.isVisible = bookPartIsPhrase

                nameField.text = bookPart?.name

                if (bookPartIsPhrase) {
                    textField.text = (bookPart as Phrase).text
                }
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
            event.newSelection
                ?.let {
                    bookPart = it.value
                    reinitialize()
                }
        }
    }

    fun hideFired(@Observes event: TreeHide) {
        stage?.hide()
    }

}