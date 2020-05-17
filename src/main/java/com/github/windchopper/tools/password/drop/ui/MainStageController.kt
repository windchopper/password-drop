package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.behavior.WindowMoveResizeBehavior
import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.Exit
import com.github.windchopper.tools.password.drop.book.*
import com.github.windchopper.tools.password.drop.crypto.EncryptEngine
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.StageStyle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.*
import java.awt.event.ActionListener
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.imageio.ImageIO
import javax.inject.Inject
import kotlin.math.abs

@ApplicationScoped @Form(Application.FXML_MAIN) class MainStageController: AnyStageController() {

    @Inject protected lateinit var bookCase: BookCase

    @FXML protected lateinit var outerContainer: BorderPane
    @FXML protected lateinit var innerContainer: BorderPane
    @FXML protected lateinit var bookView: TreeView<BookPart>

    private var trayIcon: TrayIcon? = null
    private var encryptEngine: EncryptEngine? = null
    private var book: Book? = null

    override fun preferredStageSize(): Dimension2D {
        return Screen.getPrimary().visualBounds
            .let { Dimension2D(it.width / 6, it.height / 3) }
    }

    override fun afterLoad(form: Parent, parameters: MutableMap<String, *>, formNamespace: MutableMap<String, *>) {
        super.afterLoad(form, parameters, formNamespace)

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

        addTrayIcon(stage.icons)

        GlobalScope.launch {
            val bookPath = Application.openBookPath.load()

            if (bookPath != null) exceptionally {
                book = bookCase.readBook(bookPath)
            } else {
                book = Book()
                    .also {
                        it.name = Application.messages["book.unnamed"]
                        it.pages.add(Page()
                            .also {
                                it.name = Application.messages["page.unnamed"]
                                it.paragraphs.add(Paragraph()
                                    .also {
                                        it.name = Application.messages["paragraph.unnamed"]
                                        it.words.add(Word()
                                            .also {
                                                it.name = Application.messages["word.unnamed"]
                                            })
                                    })
                            })
                    }
            }

            fillBookViewFromBook()
        }
    }

    fun fillBookViewFromBook() {
        book?.let { loadedBook ->
            val rootItem = TreeItem<BookPart>(book)
                .also { bookView.root = it; it.isExpanded = true }

            loadedBook.pages.forEach { page ->
                val pageItem = TreeItem<BookPart>(page)
                    .also { rootItem.children.add(it); it.isExpanded = true }

                page.paragraphs.forEach { paragraph ->
                    val paragraphItem = TreeItem<BookPart>(paragraph)
                        .also { pageItem.children.add(it); it.isExpanded = true }

                    paragraph.words.forEach { word ->
                        TreeItem<BookPart>(word)
                            .also { paragraphItem.children.add(it) }
                    }
                }
            }
        }
    }

    fun addTrayIcon(images: List<Image>) {
        if (SystemTray.isSupported()) {
            val systemTray = SystemTray.getSystemTray()

            val trayIconImage = ImageIO.read(URL(
                images.map { abs(systemTray.trayIconSize.width - it.width) to it.url }.minBy { it.first }
                    !!.second))

            trayIcon = TrayIcon(trayIconImage, Application.messages["tray.title"])
                .also { icon ->
                    systemTray.add(icon)

                    val openAction = ActionListener {
                        Platform.runLater {
                            with (stage) {
                                show()
                                toFront()
                            }
                        }
                    }

                    icon.addActionListener(openAction)

                    icon.popupMenu = PopupMenu()
                        .also { menu ->
                            menu.add(MenuItem(Application.messages["tray.open"])
                                .also { item ->
                                    item.addActionListener(openAction)
                                    item.font = Font.decode("Verdana")
                                        .deriveFont(Font.BOLD)
                                })

                            menu.add(MenuItem(Application.messages["tray.exit"])
                                .also { item ->
                                    item.addActionListener {
                                        Platform.exit()
                                    }
                                })
                        }
                }
        }
    }

    @FXML fun hide(event: ActionEvent) {
        stage.hide()
    }

    fun exit(@Observes exit: Exit) {
        trayIcon?.let { icon ->
            SystemTray.getSystemTray().remove(icon)
        }
    }

}