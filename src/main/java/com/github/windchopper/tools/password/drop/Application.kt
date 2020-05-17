package com.github.windchopper.tools.password.drop

import com.github.windchopper.common.fx.cdi.ResourceBundleLoad
import com.github.windchopper.common.fx.cdi.form.StageFormController
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.preferences.PlatformPreferencesStorage
import com.github.windchopper.common.preferences.PreferencesEntry
import com.github.windchopper.common.preferences.PreferencesStorage
import com.github.windchopper.common.preferences.types.FlatType
import com.github.windchopper.common.util.ClassPathResource
import com.github.windchopper.tools.password.drop.ui.AnyStageController
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.stage.Modality
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
import javax.enterprise.inject.spi.CDI

class Exit

class Application: javafx.application.Application() {

    companion object {

        const val FXML_MAIN = "com/github/windchopper/tools/password/drop/main.fxml"

        private val resourceBundle = ResourceBundle.getBundle("com.github.windchopper.tools.password.drop.i18n.messages")

        val messages = resourceBundle.keySet()
            .map { it to resourceBundle.getString(it) }
            .toMap()

        private val defaultBufferLifetime = Duration.ofMinutes(1)
        private val preferencesStorage: PreferencesStorage = PlatformPreferencesStorage(Preferences.userRoot().node("com/github/windchopper/tools/password/drop"))

        val openBookPath = PreferencesEntry<Path>(preferencesStorage, "openBookPath", FlatType(Function { Paths.get(it) }, Function { it.toString() }), defaultBufferLifetime)

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
