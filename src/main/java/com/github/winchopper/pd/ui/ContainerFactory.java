package com.github.winchopper.pd.ui;

import com.github.windchopper.common.util.Pipeliner;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import name.wind.common.fx.behavior.WindowMoveResizeBehavior;

import java.util.Random;
import java.util.function.Supplier;

import static java.util.Collections.singleton;

public class ContainerFactory {

    private static final Color []backgroundColors = {
        Color.BROWN,
        Color.BLUEVIOLET,
        Color.CADETBLUE,
        Color.CHOCOLATE,
        Color.CORNFLOWERBLUE,
        Color.TEAL,
        Color.TOMATO
    };

    public static Parent createSceneRoot(Supplier<Region> containerFactory) {
        Random random = new Random();
        Rotate rotate = new Rotate(0.1 * random.nextInt(30) *
            (random.nextBoolean() ? +1 : -1));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Bounds initialBounds = new BoundingBox(0, 0, screenBounds.getWidth(), screenBounds.getHeight());
        Bounds rotatedBounds = rotate.transform(initialBounds);

        return Pipeliner.of(BorderPane::new)
            .add(outer -> outer::getStyleClass, singleton("outer-container"))
            .set(outer -> outer::setCenter, Pipeliner.of(containerFactory)
                .add(inner -> inner::getStyleClass, singleton("inner-container"))
                .accept(inner -> new WindowMoveResizeBehavior().apply(inner))
                .add(inner -> inner::getTransforms, singleton(rotate))
                .accept(inner -> BorderPane.setMargin(inner, new Insets(Math.max(rotatedBounds.getWidth() - initialBounds.getWidth(),
                    rotatedBounds.getHeight() - initialBounds.getHeight()))))
                .set(inner -> inner::setBackground, new Background(
                    new BackgroundFill(
                        backgroundColors[random.nextInt(backgroundColors.length)],
                        null,
                        null)))
                .get())
            .get();
    }

}
