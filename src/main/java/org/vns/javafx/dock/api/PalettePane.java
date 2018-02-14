/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.dragging.DefaultMouseDragHandler;
import org.vns.javafx.dock.api.dragging.DragManager;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;

/**
 *
 * @author Valery Shyshkin
 */
public class PalettePane extends Control {

    private PaletteModel model;

    private final ObjectProperty<ScrollPane.ScrollBarPolicy> scrollVBarPolicy = new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();

    public PalettePane() {
        this(false);
    }

    public PalettePane(boolean createDefault) {
        initModel(createDefault);
        DockRegistry.makeDockable(this);
    }

    private void initModel(boolean createDefault) {
        if (createDefault) {
            model = createDefaultPaleteModel();
        } else {
            model = new PaletteModel();
        }

    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PalettePaneSkin(this);
    }

    public PaletteModel getModel() {
        return model;
    }

    public void setDragNode(Node node) {
        dragNode.set(node);
        if (Dockable.of(this) != null) {
            Dockable.of(this).getContext().setDragNode(node);
        }
    }

    public void getDragNode() {
        dragNode.get();
    }

    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }

    public void setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        this.scrollVBarPolicy.set(value);
    }

    public ScrollPane.ScrollBarPolicy getScrollPaneVbarPolicy() {
        return scrollVBarPolicy.get();
    }

    public ObjectProperty<ScrollPane.ScrollBarPolicy> scrollPaneVbarPolicy() {
        return scrollVBarPolicy;
    }

    protected PaletteModel createDefaultPaleteModel() {
        PaletteModel model = new PaletteModel();

        Label lb = new Label("Containers");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        PaletteCategory pc = model.addCategory("containers", lb);
        lb.getStyleClass().add("tree-item-font-bold");

        lb = new Label("Tab");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-node-tab");
        lb.applyCss();
        pc.addItem(lb, Tab.class);
        
        lb = new Label("VBox");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-node-vbox");
        lb.applyCss();
        pc.addItem(lb, VBox.class);

        lb = new Label("HBox");
        //lb = new label("Rectangle");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb, HBox.class);
        lb.getStyleClass().add("tree-item-node-hbox");
        lb.applyCss();

        lb = new Label("Shapes");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-font-bold");

        pc = model.addCategory("shapes", lb);
        lb.applyCss();

        lb = new Label("Rectangle");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb, Rectangle.class);
        lb.getStyleClass().add("tree-item-node-rectangle");
        lb.applyCss();

        lb = new Label("Arc");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb, Arc.class);
        lb.getStyleClass().add("tree-item-node-rectangle");
        return model;
    }

    public static class PaletteItem {

        private final ObjectProperty<Label> label = new SimpleObjectProperty<>();
        private final Class<?> valueClass;

        public Class<?> getValueClass() {
            return valueClass;
        }

        public PaletteItem(Label lb, Class<?> clazz) {
            label.set(lb);
            valueClass = clazz;
            init();
        }

        private void init() {
            if (valueClass != null) {
                DockRegistry.makeDockable(getLabel());
                DockableContext context = Dockable.of(getLabel()).getContext();
                PaletteItemMouseDragHandler handler = new PaletteItemMouseDragHandler(context, this);
                Dockable.of(getLabel())
                        .getContext()
                        .getLookup()
                        .putUnique(MouseDragHandler.class, handler);
            }

        }

        public Label getLabel() {
            return label.get();
        }

        public void setLabel(Label graphic) {
            this.label.set(graphic);
        }

        public ObservableValue<Label> labelProperty() {
            return label;
        }

    }

    public static class PaletteCategory extends PaletteItem {

        private final StringProperty id = new SimpleStringProperty();
        private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();
        private final ObjectProperty<TilePane> graphic = new SimpleObjectProperty<>();

        public PaletteCategory(String id, Label lb) {
            super(lb, null);
            this.id.set(id);
            init();
        }

        private void init() {
            TilePane tp = new TilePane();
            tp.setStyle("-fx-border-color: red");
            tp.setHgap(10);
            tp.setVgap(5);
            tp.setPrefColumns(1);
            tp.setTileAlignment(Pos.TOP_LEFT);
            setGraphic(tp);
        }

        public ObservableList<PaletteItem> getItems() {
            return items;
        }

        protected void itemsChanged(ListChangeListener.Change<? extends PaletteItem> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends PaletteItem> list = change.getRemoved();
                    for (PaletteItem d : list) {
                        //targetContext.undock(d.label());
                    }

                }
                if (change.wasAdded()) {
                    List<? extends PaletteItem> list = change.getAddedSubList();
                    for (PaletteItem d : list) {
                        //dock(d);
                        //targetContext.dock(d);
                    }
                }
            }//while
        }

        public String getId() {
            return id.get();
        }

        public void setId1(String id) {
            this.id.set(id);
        }

        public TilePane getGraphic() {
            return graphic.get();
        }

        public void setGraphic(TilePane pane) {
            this.graphic.set(pane);
        }

        public ObjectProperty<TilePane> graphicProperty() {
            return graphic;
        }

        public PaletteItem addItem(Label label, Class<?> clazz) {
            PaletteItem item = new PaletteItem(label, clazz);
            items.add(item);
            getGraphic().getChildren().add(item.getLabel());

            return item;
        }

    }

    public static class PaletteModel {

        private final ObservableList<PaletteCategory> categories = FXCollections.observableArrayList();

        public PaletteModel() {
        }

        public ObservableList<PaletteCategory> getCategories() {
            return categories;
        }

        public PaletteCategory addCategory(String id, Label label) {
            PaletteCategory c = new PaletteCategory(id, label);
            categories.add(c);
            return c;
        }

        public PaletteCategory getCategory(String id) {
            PaletteCategory retval = null;
            for (PaletteCategory c : categories) {
                if (c.getId().equals(id)) {
                    retval = c;
                    break;
                }
            }
            return retval;
        }

    }

    public static class PaletteItemMouseDragHandler extends DefaultMouseDragHandler {

        private final PaletteItem item;

        public PaletteItemMouseDragHandler(DockableContext context, PaletteItem item) {
            super(context);
            this.item = item;
        }

        @Override
        public void mousePressed(MouseEvent ev) {
            setStartMousePos(null);
            Point2D pos = new Point2D(ev.getX(), ev.getY());

            if (!ev.isPrimaryButtonDown()) {
                return;
            }

            try {
                Object value = item.getValueClass().newInstance();
                Label label = item.getLabel();

//                getContext().setDragContainer(new DragContainer(DragContainer.placeholderOf(item.getLabel()), value));
                WritableImage image = null;

                if (label != null) {
                    image = label.snapshot(null, null);
                    if (image != null) {
                        Node imageNode = new ImageView(image);
                        imageNode.setOpacity(0.75);
                        getContext().setDragContainer(new DragContainer(imageNode, value));
                        //getContext().getDragContainer().setPlaceholder(imageNode);
                    }
                }

            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(PalettePane.class.getName()).log(Level.SEVERE, null, ex);
            }

            setStartMousePos(pos);
        }

        public DragManager getDragManager(MouseEvent ev) {
            DragManager dm = super.getDragManager(ev);
            dm.setHideOption(DragManager.HideOption.CARRIERED);
            return dm;
        }
    }//PalettePaneMouseDragHandler
}//PalettePane