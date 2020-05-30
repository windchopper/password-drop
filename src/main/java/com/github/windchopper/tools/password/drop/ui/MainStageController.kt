package com.github.windchopper.tools.password.drop.ui

import com.github.windchopper.common.fx.cdi.form.Form
import com.github.windchopper.tools.password.drop.Application
import com.github.windchopper.tools.password.drop.Exit
import com.github.windchopper.tools.password.drop.book.*
import com.github.windchopper.tools.password.drop.crypto.EncryptEngine
import com.github.windchopper.tools.password.drop.exceptionally
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Screen
import javafx.stage.StageStyle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Font
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionListener
import java.net.URL
import java.nio.file.Paths
import java.util.concurrent.Callable
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event
import javax.enterprise.event.Observes
import javax.enterprise.inject.spi.BeanManager
import javax.imageio.ImageIO
import javax.inject.Inject
import kotlin.math.abs
import kotlin.reflect.KClass

@ApplicationScoped @Form(Application.FXML_MAIN) class MainStageController: AnyStageController() {

    @Inject private lateinit var bookCase: BookCase
    @Inject private lateinit var treeEditEvent: Event<TreeEdit<BookPart>>
    @Inject private lateinit var treeSelectionEvent: Event<TreeSelection<BookPart>>
    @Inject private lateinit var treeHideEvent: Event<TreeHide>

    @FXML private lateinit var bookView: TreeView<BookPart>
    @FXML private lateinit var newPageMenuItem: MenuItem
    @FXML private lateinit var newParagraphMenuItem: MenuItem
    @FXML private lateinit var newPhraseMenuItem: MenuItem
    @FXML private lateinit var editMenuItem: MenuItem
    @FXML private lateinit var deleteMenuItem: MenuItem
    @FXML private lateinit var stayOnTopMenuItem: CheckMenuItem
    @FXML private lateinit var reloadBookMenuItem: MenuItem

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
            initStyle(StageStyle.UTILITY)
            title = Application.messages["main.head"]

            isAlwaysOnTop = Application.stayOnTop.load()?:false
            stayOnTopMenuItem.isSelected = isAlwaysOnTop

            setOnCloseRequest { event ->
                treeHideEvent.fire(TreeHide())
            }

            with (scene) {
                fill = Color.TRANSPARENT
            }
        }

        with (bookView) {
            with (selectionModel) {
                selectionMode = SelectionMode.SINGLE
                selectedItemProperty().addListener { selectedItemProperty, oldSelection, newSelection ->
                    treeSelectionEvent.fire(TreeSelection(oldSelection, newSelection))
                }
            }

            setOnDragDetected { event ->
                selectionModel.selectedItem?.value
                    ?.let { bookPart ->
                        if (bookPart is Phrase) {
                            val content = ClipboardContent()
                            content[DataFormat.PLAIN_TEXT] = bookPart.text?:""

                            val dragBoard = bookView.startDragAndDrop(TransferMode.COPY)
                            dragBoard.setContent(content)

                            fun invertPaint(fill: Paint): Color = if (fill is Color) {
                                fill.invert()
                            } else {
                                Color.WHITE
                            }

                            val label = Label(bookPart.name)
                                .also {
                                    it.background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                                    it.effect = DropShadow(3.0, invertPaint(it.textFill))
                                }

                            val textBounds = Text(bookPart.name)
                                .let {
                                    it.font = label.font
                                    it.boundsInLocal
                                }

                            dragBoard.dragView = WritableImage(textBounds.width.toInt(), textBounds.height.toInt())
                                .also { Scene(label)
                                    .also { it.fill = Color.TRANSPARENT }
                                    .snapshot(it) }

                            event.consume()
                        }
                    }
            }
        }

        addMenuItemBindings()

        addTrayIcon(stage.icons)

        GlobalScope.launch {
            val bookPath = Application.openBookPath.load()

            book = if (bookPath != null) {
                bookCase.readBook(bookPath)
            } else {
                buildNewBook()
            }

            fillBookViewFromBook()
        }
    }

    fun buildNewBook(): Book {
        return Book().also {
            it.name = Application.messages["book.unnamed"]
            it.pages.add(Page().also {
                it.name = Application.messages["page.unnamed"]
                it.paragraphs.add(Paragraph().also {
                    it.name = Application.messages["paragraph.unnamed"]
                    it.phrases.add(Phrase().also {
                        it.name = Application.messages["phrase.unnamed"]
                    })
                })
            })
        }
    }

    fun fillBookViewFromBook() {
        book?.let { loadedBook ->
            reloadBookMenuItem.isDisable = !loadedBook.fileAttached

            val rootItem = TreeItem<BookPart>(book)
                .also { bookView.root = it; it.isExpanded = true }

            loadedBook.pages.forEach { page ->
                val pageItem = TreeItem<BookPart>(page)
                    .also { rootItem.children.add(it); it.isExpanded = true }

                page.paragraphs.forEach { paragraph ->
                    val paragraphItem = TreeItem<BookPart>(paragraph)
                        .also { pageItem.children.add(it); it.isExpanded = true }

                    paragraph.phrases.forEach { word ->
                        TreeItem<BookPart>(word)
                            .also { paragraphItem.children.add(it) }
                    }
                }
            }
        }
    }

    fun addMenuItemBindings() {
        with (bookView.selectionModel) {
            fun selectedItemIs(vararg types: KClass<*>): BooleanBinding {
                return Bindings.createBooleanBinding(
                    Callable {
                        selectedItem?.value
                            ?.let { value -> types.any { type -> type.isInstance(value) } }
                            ?:false
                    },
                    selectedItemProperty())
            }

            newPageMenuItem.disableProperty().bind(selectedItemProperty().isNull.or(selectedItemIs(Book::class, Page::class).not()))
            newParagraphMenuItem.disableProperty().bind(selectedItemProperty().isNull.or(selectedItemIs(Page::class, Paragraph::class).not()))
            newPhraseMenuItem.disableProperty().bind(selectedItemProperty().isNull.or(selectedItemIs(Paragraph::class, Phrase::class).not()))
            editMenuItem.disableProperty().bind(selectedItemProperty().isNull)
            deleteMenuItem.disableProperty().bind(selectedItemProperty().isNull)
        }
    }

    fun addTrayIcon(images: List<Image>) {
        if (SystemTray.isSupported()) {
            val systemTray = SystemTray.getSystemTray()

            val trayIconImage = ImageIO.read(URL(
                images.map { abs(systemTray.trayIconSize.width - it.width) to it.url }.minBy { it.first }
                    !!.second))

            trayIcon = TrayIcon(trayIconImage, Application.messages["tray.head"])
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

                    icon.popupMenu = java.awt.PopupMenu()
                        .also { menu ->
                            menu.add(java.awt.MenuItem(Application.messages["tray.show"])
                                .also { item ->
                                    item.addActionListener(openAction)
                                    item.font = Font.decode(null)
                                        .deriveFont(Font.BOLD)
                                })

                            menu.add(java.awt.MenuItem(Application.messages["tray.exit"])
                                .also { item ->
                                    item.addActionListener {
                                        Platform.exit()
                                    }
                                })
                        }
                }
        }
    }

    @FXML fun newPage(event: ActionEvent) {

    }

    @FXML fun newParagaph(event: ActionEvent) {

    }

    @FXML fun newPhrase(event: ActionEvent) {

    }

    @FXML fun edit(event: ActionEvent) {
        treeEditEvent.fire(TreeEdit(bookView.selectionModel.selectedItem))
    }

    @FXML fun delete(event: ActionEvent) {

    }

    @FXML fun open(event: ActionEvent) {
        FileChooser()
            .let {
                it.initialDirectory = (Application.openBookPath.load()?:Paths.get(System.getProperty("user.home"))).toFile()
                it.extensionFilters.add(ExtensionFilter(Application.messages["books"], "*.book.xml"))
                it.showOpenDialog(stage)
                    ?.let { file ->
                        GlobalScope.launch {
                            exceptionally {
                                book = bookCase.readBook(file.toPath())
                                fillBookViewFromBook()
                            }
                        }
                    }
            }
    }

    @FXML fun reload(event: ActionEvent) {

    }

    @FXML fun save(event: ActionEvent) {

    }

    @FXML fun toggleStayOnTop(event: ActionEvent) {
        stage.isAlwaysOnTop = stayOnTopMenuItem.isSelected
        Application.stayOnTop.save(stage.isAlwaysOnTop)
    }

    @FXML fun exit(event: ActionEvent) {
        Platform.exit()
    }

    fun afterExit(@Observes exit: Exit) {
        trayIcon?.let { icon ->
            SystemTray.getSystemTray().remove(icon)
        }
    }

}