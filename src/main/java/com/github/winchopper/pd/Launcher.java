package com.github.winchopper.pd;

import com.github.winchopper.pd.ui.ContainerFactory;
import com.github.windchopper.common.util.Pipeliner;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Collections;
import java.util.ResourceBundle;

import static java.util.Collections.singleton;

public class Launcher extends Application {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.pd.i18n.messages");

    @Override public void start(Stage primaryStage) throws Exception {
        Rectangle2D primaryScreenVisualBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setScene(Pipeliner.of(() -> new Scene(ContainerFactory.createSceneRoot(ContainerFactory::createPasswordTree), primaryScreenVisualBounds.getWidth() / 6, primaryScreenVisualBounds.getHeight() / 3))
            .set(scene -> scene::setFill, Color.TRANSPARENT)
            .add(scene -> scene::getStylesheets, singleton("/com/github/windchopper/pd/styles.css"))
            .get());

        primaryStage.setTitle(bundle.getString("window.main.title"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

}
