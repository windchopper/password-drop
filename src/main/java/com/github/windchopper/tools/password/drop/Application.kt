package com.github.windchopper.tools.password.drop

import com.github.windchopper.common.fx.cdi.ResourceBundleLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.fx.dialog.ExceptionDialog
import com.github.windchopper.common.fx.dialog.ExceptionDialogModel
import com.github.windchopper.common.preferences.PlatformPreferencesStorage
import com.github.windchopper.common.preferences.PreferencesEntry
import com.github.windchopper.common.preferences.PreferencesStorage
import com.github.windchopper.common.preferences.types.FlatType
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.tools.password.drop.ui.AnyStageController
import javafx.application.Platform
import javafx.stage.Stage
import org.jboss.weld.environment.se.Weld
import org.jboss.weld.environment.se.WeldContainer
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import java.util.prefs.Preferences

class Exit

class Application: javafx.application.Application() {

    companion object {

        const val FXML_MAIN = "com/github/windchopper/tools/password/drop/main.fxml"
        const val FXML_EDIT = "com/github/windchopper/tools/password/drop/edit.fxml"

        private val resourceBundle = ResourceBundle.getBundle("com.github.windchopper.tools.password.drop.i18n.messages")

        val messages = resourceBundle.keySet()
            .map { it to resourceBundle.getString(it) }
            .toMap()

        private val defaultBufferLifetime = Duration.ofMinutes(1)
        private val preferencesStorage: PreferencesStorage = PlatformPreferencesStorage(Preferences.userRoot().node("com/github/windchopper/tools/password/drop"))

        val openBookPath = PreferencesEntry<Path>(preferencesStorage, "openBookPath", FlatType(Function { Paths.get(it) }, Function { it.toString() }), defaultBufferLifetime)
        val stayOnTop = PreferencesEntry<Boolean>(preferencesStorage, "stayOnTop", FlatType(Function { it?.toBoolean()?:false }, Function { it.toString() }), defaultBufferLifetime)

    }

    private lateinit var weld: Weld
    private lateinit var weldContainer: WeldContainer

    override fun init() {
        weld = Weld()
        weldContainer = weld.initialize()

        Platform.setImplicitExit(false)
    }

    override fun start(primaryStage: Stage) {
        with (weldContainer.beanManager) {
            fireEvent(ResourceBundleLoad(resourceBundle))
            fireEvent(StageFormLoad(ClassPathResource(FXML_MAIN), Supplier { primaryStage }))
        }
    }

    override fun stop() {
        weldContainer.beanManager.fireEvent(Exit())
        weld.shutdown()
    }

}

fun <T> T.display(stageController: AnyStageController) where T: Throwable = Platform.runLater {
    ExceptionDialog(stageController.prepareModalDialogFrame(), ExceptionDialogModel()
        .also { it.exception = this })
        .show()
}

fun AnyStageController.exceptionally(runnable: () -> Unit) = try {
    runnable.invoke()
} catch (thrown: Exception) {
    thrown.display(this)
}

fun <T> AnyStageController.exceptionally(supplier: () -> T, defaultSupplier: () -> T): T = try {
    supplier.invoke()
} catch (thrown: Exception) {
    thrown.display(this)
    defaultSupplier.invoke()
}