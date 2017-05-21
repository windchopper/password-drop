package com.github.winchopper.pd;

import com.github.winchopper.pd.ui.ContainerFactory;
import com.github.windchopper.common.util.Pipeliner;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static java.util.Collections.singleton;

public class Main extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        Rectangle2D primaryScreenVisualBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setScene(Pipeliner.of(() -> new Scene(ContainerFactory.createSceneRoot(ContainerFactory::createPasswordTree), primaryScreenVisualBounds.getWidth() / 6, primaryScreenVisualBounds.getHeight() / 3))
            .set(scene -> scene::setFill, Color.TRANSPARENT)
            .add(scene -> scene::getStylesheets, singleton("styles.css"))
            .get());

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

}
