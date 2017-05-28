package com.github.winchopper.pd.ui;

import com.github.windchopper.common.util.Pipeliner;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static java.util.Arrays.asList;

public class ContainerFactory {

    public static Parent createSceneRoot() {
        return Pipeliner.of(BorderPane::new)
//            .set(root -> root::setBackground, new Background(new BackgroundFill(Color.TRANSPARENT, null, null)))
            .set(root -> root::setTop, createSceneRootMenuBar())
            .set(root -> root::setCenter, createPasswordTree())
            .get();
    }

    public static MenuBar createSceneRootMenuBar() {
        return Pipeliner.of(MenuBar::new)
//            .set(menuBar -> menuBar::setBackground, new Background(new BackgroundFill(Color.TRANSPARENT, null, null)))
            .add(menuBar -> menuBar::getMenus, asList(
                Pipeliner.of(Menu::new)
                    .set(menu -> menu::setText, "File")
                    .add(menu -> menu::getItems, asList(
                        Pipeliner.of(MenuItem::new)
                            .set(menuItem -> menuItem::setText, "Open...")
                            .get(),
                        Pipeliner.of(MenuItem::new)
                            .set(menuItem -> menuItem::setText, "Reload")
                            .get(),
                        Pipeliner.of(SeparatorMenuItem::new)
                            .get(),
                        Pipeliner.of(MenuItem::new)
                            .set(menuItem -> menuItem::setText, "Exit")
                            .get()
                    ))
                    .get(),
                Pipeliner.of(Menu::new)
                    .set(menu -> menu::setText, "Security")
                    .add(menu -> menu::getItems, asList(
                        Pipeliner.of(MenuItem::new)
                            .set(menuItem -> menuItem::setText, "Encrypt")
                            .get(),
                        Pipeliner.of(MenuItem::new)
                            .set(menuItem -> menuItem::setText, "Decrypt")
                            .get()
                    ))
                    .get()
            ))
            .get();
    }

    public static Region createPasswordTree() {
        return Pipeliner.of(TreeView<String>::new)
            .accept(tree -> {

                TreeItem<String> word = new TreeItem<>("word");

                TreeItem<String> page = new TreeItem<>("page");
                page.getChildren().add(word);

                TreeItem<String> root = new TreeItem<>("root");
                root.getChildren().add(page);

                tree.setRoot(root);
            })
            .get();
    }

}
