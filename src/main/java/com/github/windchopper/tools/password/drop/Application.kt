package com.github.windchopper.tools.password.drop

import com.github.windchopper.common.fx.cdi.ResourceBundleLoad
import com.github.windchopper.common.fx.cdi.form.StageFormLoad
import com.github.windchopper.common.util.ClassPathResource
import javafx.stage.Stage
import org.jboss.weld.environment.se.Weld
import java.util.*
import java.util.function.Supplier
import javax.enterprise.inject.spi.CDI

class Application: javafx.application.Application() {

    companion object {

        const val FXML_MAIN = "com/github/windchopper/tools/password/drop/main.fxml"

        private val resourceBundle = ResourceBundle.getBundle("com.github.windchopper.tools.password.drop.i18n.messages")

        val messages = resourceBundle.keySet()
            .map { it to resourceBundle.getString(it) }
            .toMap()

    }

    private lateinit var weld: Weld

    override fun init() {
        weld = Weld().let {
            it.initialize()
            it
        }
    }

    override fun stop() {
        weld.shutdown()
    }

    override fun start(primaryStage: Stage) {
        with (CDI.current().beanManager) {
            fireEvent(ResourceBundleLoad(resourceBundle))
            fireEvent(StageFormLoad(ClassPathResource(FXML_MAIN), Supplier { primaryStage }))
        }
    }

}