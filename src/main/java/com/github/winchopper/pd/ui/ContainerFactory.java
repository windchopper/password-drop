package com.github.winchopper.pd.ui;

import com.github.windchopper.common.util.Pipeliner;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;

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

    public static void applyRandomRotationAndBackgroundColor(Region region) {
        Random random = new Random();
        region.setRotate(
            0.1 * random.nextInt(40) * (random.nextBoolean() ? +1 : -1));
        region.setBackground(
            new Background(
                new BackgroundFill(
                    backgroundColors[random.nextInt(backgroundColors.length)],
                    null,
                    null)));
    }

    public static Parent createSceneRoot(Supplier<Region> containerFactory) {
        return Pipeliner.of(BorderPane::new)
            .add(outer -> outer::getStyleClass, singleton("outer-container"))
            .set(outer -> outer::setCenter, Pipeliner.of(containerFactory)
                .add(inner -> inner::getStyleClass, singleton("inner-container"))
                .set(inner -> inner::setOnMouseMoved, ContainerFactory::move)
                .accept(ContainerFactory::applyRandomRotationAndBackgroundColor)
                .get())
            .get();
    }

    static void move(MouseEvent event) {
        Region region = (Region) event.getSource();

        Bounds outerBounds = region.getLayoutBounds();
        Bounds innerBounds = new BoundingBox(outerBounds.getMinX() + 3, outerBounds.getMinY() + 3, outerBounds.getWidth() - 6, outerBounds.getHeight() - 6);

        Point2D center = new Point2D(innerBounds.getMinX() + innerBounds.getWidth() / 2, innerBounds.getMinY() + innerBounds.getHeight() / 2);
        Point2D pointer = new Point2D(event.getX(), event.getY());

        ObservableList<Transform> transforms = region.getTransforms();

        for (int i = transforms.size(); --i >= 0; ) {
            try {
                pointer = transforms.get(i).inverseTransform(pointer);
            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }
        }

        double aspectRatioCorrection = outerBounds.getWidth() / outerBounds.getHeight();

        Point2D pointerTranslated = new Point2D(
            pointer.getX() - center.getX(),
            -1 * (pointer.getY() - center.getY()) * aspectRatioCorrection
        );

        double angle = Math.toDegrees(Math.atan2(pointerTranslated.getY(), pointerTranslated.getX()));

        if (angle < 0) angle = 360 + angle;

        if (outerBounds.contains(pointer) && !innerBounds.contains(event.getX(), event.getY())) {
            if (incl(angle, 0, 43)) region.setCursor(Cursor.E_RESIZE);
            else if (incl(angle, 43, 47)) region.setCursor(Cursor.NE_RESIZE);
            else if (incl(angle, 47, 133)) region.setCursor(Cursor.N_RESIZE);
            else if (incl(angle, 133, 137)) region.setCursor(Cursor.NW_RESIZE);
            else if (incl(angle, 137, 223)) region.setCursor(Cursor.W_RESIZE);
            else if (incl(angle, 223, 227)) region.setCursor(Cursor.SW_RESIZE);
            else if (incl(angle, 227, 313)) region.setCursor(Cursor.S_RESIZE);
            else if (incl(angle, 313, 317)) region.setCursor(Cursor.SE_RESIZE);
            else if (incl(angle, 317, 360)) region.setCursor(Cursor.E_RESIZE);
            else region.setCursor(Cursor.CROSSHAIR);
        } else {
            region.setCursor(Cursor.DEFAULT);
        }
    }

    static boolean incl(double val, double min, double max) {
        return val >= min && val <= max;
    }

}
