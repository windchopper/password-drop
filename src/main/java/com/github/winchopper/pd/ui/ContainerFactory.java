package com.github.winchopper.pd.ui;

import com.github.windchopper.common.util.Pipeliner;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
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
        return Pipeliner.of(GridPane::new)
            .set(pane -> pane::setPadding, new Insets(0))
            .add(pane -> pane::getStyleClass, asList("any"))
            .set(pane -> pane::setBackground, new Background(
                new BackgroundFill(
                    mainColor.brighter(),
                    null,
                    null)))
            .add(pane -> pane::getChildren, asList(
                Pipeliner.of(Button::new)
                    .accept(button -> GridPane.setConstraints(button, 1, 0, 1, 1, HPos.RIGHT, VPos.TOP, Priority.ALWAYS, Priority.NEVER, new Insets(0, 5, 0, 5)))
                    .set(button -> button::setText, "x")
                    .set(button -> button::setBackground, new Background(
                        new BackgroundFill(
                            Color.RED,
                            null,
                            null)))
                    .set(button -> button::setOnAction, actionEvent -> System.exit(0))
                    .set(button -> button::setOnMouseEntered, mouseEvent -> ((Button) mouseEvent.getSource()).setEffect(new Glow()))
                    .set(button -> button::setOnMouseExited, mouseEvent -> ((Button) mouseEvent.getSource()).setEffect(null))
                    .get(),
                Pipeliner.of(TreeView<String>::new)
                    .accept(tree -> GridPane.setConstraints(tree, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(5, 5, 5, 5)))
                    .accept(tree -> {
                        tree.setCursor(Cursor.DEFAULT);
                        TreeItem<String> word = new TreeItem<>("word");
                        TreeItem<String> page = new TreeItem<>("page");
                        page.getChildren().add(word);
                        TreeItem<String> root = new TreeItem<>("root");
                        root.getChildren().add(page);
                        tree.setRoot(root);
                    })
                    .get()))
            .get();
    }

}
