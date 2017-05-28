package com.github.winchopper.pd.ui;

import com.github.windchopper.common.util.Pipeliner;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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

import static java.util.Arrays.asList;
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

    private static Color mainColor;

    static {
        Random random = new Random();
        mainColor = backgroundColors[random.nextInt(backgroundColors.length)];
    }

    public static Parent createSceneRoot(Supplier<Region> containerFactory) {
        Random random = new Random();
        Rotate rotate = new Rotate(0.1 * random.nextInt(10) *
            (random.nextBoolean() ? +1 : -1));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Bounds initialBounds = new BoundingBox(0, 0, screenBounds.getWidth(), screenBounds.getHeight());
        Bounds rotatedBounds = rotate.transform(initialBounds);

        return Pipeliner.of(BorderPane::new)
            .add(outer -> outer::getStyleClass, asList("any", "outer-container"))
            .set(outer -> outer::setBackground, new Background(
                new BackgroundFill(
                    Color.TRANSPARENT,
                    null,
                    null)))
            .set(outer -> outer::setCenter, Pipeliner.of(containerFactory)
                .add(inner -> inner::getStyleClass, asList("any", "inner-container"))
                .accept(inner -> new WindowMoveResizeBehavior(inner).apply(inner))
                .add(inner -> inner::getTransforms, singleton(rotate))
                .accept(inner -> BorderPane.setMargin(inner, new Insets(Math.max(rotatedBounds.getWidth() - initialBounds.getWidth(),
                    rotatedBounds.getHeight() - initialBounds.getHeight()))))
                .set(inner -> inner::setBackground, new Background(
                    new BackgroundFill(
                        mainColor,
                        null,
                        null)))
                .get())
            .get();
    }

    public static Region createPasswordTree() {
        return Pipeliner.of(BorderPane::new)
            .set(pane -> pane::setPadding, new Insets(20, 10, 20, 10))
            .add(pane -> pane::getStyleClass, asList("any"))
            .set(pane -> pane::setBackground, new Background(new BackgroundFill(mainColor.brighter(), null, null)))
            .set(pane -> pane::setCenter, Pipeliner.of(TreeView<String>::new)
                .accept(tree -> {
                    TreeItem<String> word = new TreeItem<>("word");

                    TreeItem<String> page = new TreeItem<>("page");
                    page.getChildren().add(word);

                    TreeItem<String> root = new TreeItem<>("root");
                    root.getChildren().add(page);

                    tree.setRoot(root);
                })
                .get())
            .get();
    }

}
