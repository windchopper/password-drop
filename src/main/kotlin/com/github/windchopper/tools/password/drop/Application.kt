package com.github.windchopper.tools.password.drop

import com.github.windchopper.common.fx.cdi.ResourceBundleLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.preferences.PreferencesEntryFlatType
import com.github.windchopper.common.preferences.PreferencesStorage
import com.github.windchopper.common.preferences.entries.BufferedEntry
import com.github.windchopper.common.preferences.entries.StandardEntry
import com.github.windchopper.common.preferences.storages.PlatformStorage
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.tools.password.drop.misc.Exit
import javafx.application.Platform
import javafx.stage.Stage
import org.jboss.weld.environment.se.Weld
import org.jboss.weld.environment.se.WeldContainer
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.time.Duration
import java.util.*
import java.util.function.Supplier
import java.util.prefs.Preferences

class Application: javafx.application.Application() {

    companion object {

        const val FXML_MAIN = "com/github/windchopper/tools/password/drop/main.fxml"
        const val FXML_EDIT = "com/github/windchopper/tools/password/drop/edit.fxml"

        private val resourceBundle = ResourceBundle.getBundle("com.github.windchopper.tools.password.drop.i18n.messages")

        val messages = resourceBundle.keySet().associateWith(resourceBundle::getString)

        private val defaultBufferLifetime = Duration.ofMinutes(1)
        private val preferencesStorage: PreferencesStorage = PlatformStorage(Preferences.userRoot().node("com/github/windchopper/tools/password/drop"))

        val openBookPath = BufferedEntry(defaultBufferLifetime, StandardEntry(preferencesStorage, "openBookPath", PreferencesEntryFlatType({ Path.of(it) }, Path::toString)))
        val stayOnTop = BufferedEntry(defaultBufferLifetime, StandardEntry(preferencesStorage, "stayOnTop", PreferencesEntryFlatType(String::toBoolean, Boolean::toString)))

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
            fireEvent(StageFormLoad(ClassPathResource(FXML_MAIN), { primaryStage }))
        }
    }

    override fun stop() {
        weldContainer.beanManager.fireEvent(Exit())
        weld.shutdown()
    }

}

fun main(vararg args: String) {
    javafx.application.Application.launch(Application::class.java, *args)
}
