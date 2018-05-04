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
package org.vns.javafx.dock.api.dragging.view;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public abstract class WindowNodeFraming extends AbstractNodeFraming implements EventHandler<MouseEvent> {

    private ChangeListener<Bounds> boundsInParentListener;

    private final ObservableList<String> styleClass = FXCollections.observableArrayList();

    private final ObjectProperty<String> style = new SimpleObjectProperty<>();

    private Window window;

    private Resizer resizer;

    private StackPane root;

    private Window nodeWindow;
    private Insets nodeInsets;
    protected double borderWidth = 0;
    double borderHeight = 0;
    double insetsWidth = 0;
    double insetsHeight = 0;
    double insetsLeft = 0;
    double insetsTop = 0;

    double insetsRootTop = 0;
    double insetsRootLeft = 0;

    //private final DoubleProperty workWidth = new SimpleDoubleProperty(-1);
    //private final DoubleProperty workHeight = new SimpleDoubleProperty(-1);
    private final Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private boolean cursorSupported = false;

    //private Point2D startMousePos;
//    private NodeFraming ing;
    private RectangleFrame rectangleFrame;

    private double translateX;
    private double translateY;
    private Cursor saveCursor;

    protected WindowNodeFraming() {
        super();
    }

    @Override
    public ObservableList<String> getStyleClass() {
        return styleClass;
    }

    @Override
    public ObjectProperty<String> styleProperty() {
        return style;
    }

    @Override
    public String getStyle() {
        return style.get();
    }

    @Override
    public void setStyle(String style) {
        this.style.set(style);
    }

    public void setDefaultStyle() {
        setStyle("-fx-background-color: yellow; -fx-opacity: 0.5; -fx-border-width: 1; -fx-border-color: black; -fx-border-style: dashed");
        //setStyle("-fx-background-color: transparent;-fx-border-width: 1; -fx-border-color: black; -fx-border-style: dashed");        
    }

    protected void setWindow(Window window) {
        this.window = window;
//        init();
    }

    private void init() {
        //workHeight.set(-1);
        //workWidth.set(-1);

        window.setOnShown(e -> {
            DockRegistry.register(window, true); // true means exclude when searfor target window
        });
        window.setOnHidden(e -> {
            DockRegistry.unregister(window);
        });

        if (getNode() instanceof Region) {
            init((Region) getNode());
        }
        initScene();
    }

    protected abstract void setWindowSize(Bounds bounds, double borderWidth, double borderHeight);

    protected abstract void initScene();

    protected abstract void createWindow();

    protected void doShow(Window owner) {
    }

    public Node getRoot() {
        return root;
    }

    public Window getWindow() {
        return window;
    }

    private void init(Region region) {

        root = new StackPane() {
            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };
        //Border b = new NodeResizerBorder().getBorder();
        //root.setBorder(b);
//        System.err.println("WindowNodeFraming: styles.size() = " + root.getStyleClass().size());
        //root.setStyle("-fx-background-color: yellow; -fx-opacity: 0.5; -fx-border-width: 1; -fx-border-color: black; -fx-border-style: dashed");
        if (getStyle() == null && getStyleClass().isEmpty()) {
            setDefaultStyle();
            root.setStyle(getStyle());
        } else if (getStyle() != null) {
            root.setStyle(getStyle());
        } else {
            getStyleClass().forEach(s -> {
                root.getStyleClass().add(s);
            });

        }
        root.applyCss();

        borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
        borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();

        nodeInsets = ((Region) region).getInsets();
        if (nodeInsets != null) {
            insetsWidth = nodeInsets.getLeft() + nodeInsets.getRight();
            insetsHeight = nodeInsets.getTop() + nodeInsets.getBottom();
            insetsTop = nodeInsets.getTop();
            insetsLeft = nodeInsets.getLeft();
        }
        insetsRootTop = root.getInsets() != null ? root.getInsets().getTop() : 0;
        insetsRootLeft = root.getInsets() != null ? root.getInsets().getLeft() : 0;

        Bounds screenBounds = getNode().localToScreen(getNode().getLayoutBounds());

        window.setX(screenBounds.getMinX() - insetsRootLeft);
        window.setY(screenBounds.getMinY() - insetsRootTop);

        window.setWidth(getNode().getLayoutBounds().getWidth() + borderWidth);
        window.setHeight(getNode().getLayoutBounds().getHeight() + borderHeight);

        ((Region) getNode()).setPrefWidth(((Region) getNode()).getWidth());
        ((Region) getNode()).setPrefHeight(((Region) getNode()).getHeight());

        //setWorkWidth(getNode().getLayoutBounds().getWidth());
        //setWorkHeight(getNode().getLayoutBounds().getHeight());
        root.setPrefWidth(getNode().getLayoutBounds().getWidth() + borderWidth);
        root.setPrefHeight(getNode().getLayoutBounds().getHeight() + borderHeight);

        boundsInParentListener = (o, ov, nv) -> {

            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();

            Insets nodeInsets = ((Region) region).getInsets();
            if (nodeInsets != null) {
                insetsWidth = nodeInsets.getLeft() + nodeInsets.getRight();
                insetsHeight = nodeInsets.getTop() + nodeInsets.getBottom();
                insetsTop = nodeInsets.getTop();
                insetsLeft = nodeInsets.getLeft();
            }
            insetsRootTop = root.getInsets() != null ? root.getInsets().getTop() : 0;
            insetsRootLeft = root.getInsets() != null ? root.getInsets().getLeft() : 0;

            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());

            window.setX(sb.getMinX() - insetsRootLeft);
            window.setY(sb.getMinY() - insetsRootTop);

            //setWorkWidth(sb.getWidth());
            //setWorkHeight(sb.getHeight());
            root.setPrefWidth(sb.getWidth() + borderWidth);
            root.setPrefHeight(sb.getHeight() + borderHeight);
            //System.err.println("+root.getWidth = " + root.getWidth());
            //System.err.println("window.getWidth = " + window.getWidth());
            setWindowSize(sb, borderWidth, borderHeight);
        };

        getNode().boundsInParentProperty().addListener(boundsInParentListener);

        //
        //  show to widthProperty and heightProperty
        //
        //screenBounds = getNode().localToScreen(getNode().getLayoutBounds());
        //setWorkWidth(getNode().getLayoutBounds().getWidth());
        //setWorkHeight(getNode().getLayoutBounds().getHeight());
        nodeWindow = region.getScene().getWindow();
        setWindowSize(getNode().getLayoutBounds(), borderWidth, borderHeight);

        bindWindowPosition(nodeWindow);
    }

    protected void bindWindowPosition(Window owner) {
        owner.xProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });
        owner.yProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });

    }

    protected static Bounds windowBounds(Window window, Node node) {
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        double borderWidth = 0;
        double borderHeight = 0;
        double borderX = 0;
        double borderY = 0;
        if (window == null) {
            return b;
        }
        Region root = (Region) window.getScene().getRoot();
        if (root.getInsets() != null) {
            borderX = root.getInsets().getLeft();
            borderY = root.getInsets().getTop();

            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        window.setX(b.getMinX() - borderX);
        window.setY(b.getMinY() - borderY);

        if (window instanceof Stage) {
            window.setWidth(b.getWidth() + borderWidth);
            window.setHeight(b.getHeight() + borderHeight);
        }
        return b;
    }

    /*    public DoubleProperty workWidthProperty() {
        return workWidth;
    }

    public double getWorkWidth() {
        return workWidth.get();
    }

    public void setWorkWidth(double width) {
        workWidth.set(width);
    }

    public DoubleProperty workHeightProperty() {
        return workHeight;
    }

    public double getWorkHeight() {
        return workHeight.get();
    }

    public void setWorkHeight(double height) {
        workHeight.set(height);
    }
     */
    @Override
    protected void initializeOnShow(Node node) {
  /*      if (window != null) {
            removeWindowListeners();
        }
*/
        createWindow();
        if (window == null) {
            return;
        }

        init();
        show();

    }

    protected void show() {
        Window newOwner = getNode().getScene().getWindow();
        if (nodeWindow == newOwner) {
            window.hide();
        }

        resizer = new NodeResizer(window, (Region) getNode());

/*        window.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
        window.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        window.addEventFilter(MouseEvent.MOUSE_MOVED, this);
        window.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
*/
        doShow(newOwner);
    }

/*    protected void removeWindowListeners() {
        window.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        window.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        window.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
        window.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
    }
*/
    @Override
    public void handle(MouseEvent ev) {
        /*        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
             System.err.println("WindowNodeFraming mouse moved");
            Cursor c = NodeResizer.cursorBy(new Point2D(ev.getX(), ev.getY()), (Region) window.getScene().getRoot());

            if (!isCursorSupported(c)) {
                window.getScene().setCursor(Cursor.DEFAULT);
            } else {
                window.getScene().setCursor(c);
            }
            if (!c.equals(Cursor.DEFAULT)) {
                ev.consume();
            }

        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
             System.err.println("WindowNodeFraming mouse pressed");
            if (!window.getScene().getRoot().contains(ev.getX(), ev.getY())) {
                removeWindowListeners();
                hide();

                return;
            }
            saveCursor = NodeResizer.cursorBy(new Point2D(ev.getX(), ev.getY()), root);
            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                window.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            resizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        } else         if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
             System.err.println("WindowNodeFraming mouse released");
        } else 
         */
        if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (!cursorSupported) {
                return;
            }
//            System.err.println("WindowNodeFraming mouse dragged");
//            if (!resizer.isStarted()) {
//                System.err.println("WindowNodeFraming resiser.start");
//                resizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
//            } else {
//            Platform.runLater(() -> {
            resizer.resize(ev.getScreenX(), ev.getScreenY());
//            });
//            }
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            //getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
            getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
//            System.err.println("WindowNodeFraming redirectMouseReleased");
            hide();

            if (rectangleFrame != null) {
 //               Platform.runLater(() -> {
                    rectangleFrame.setVisible(true);
//                });

            }
        }
    }

    /*  private EventHandler<MouseEvent> redirectMouseReleasedHandler = ev -> {
        redirectMouseReleased(ev);
    };
     */
 /*    public void redirectMouseEvents(MouseEvent ev, Point2D startMousePos, NodeFraming redirectSource) {
        this.startMousePos = startMousePos;
        this.redirectSource = redirectSource;

        removeWindowListeners();

        saveCursor = getNode().getScene().getCursor();
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        redirectMousePressed(ev);
       //windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        resizer.start(ev, this, saveCursor, getSupportedCursors());
    }
     */
    public void redirectMouseEvents(MouseEvent ev, Point2D startMousePos, RectangleFrame redirectSource) {
        //this.startMousePos = startMousePos;
        this.rectangleFrame = redirectSource;

        //removeWindowListeners();

        saveCursor = getNode().getScene().getCursor();
        //getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);

        cursorSupported = isCursorSupported(saveCursor);
        if (!cursorSupported) {
            window.getScene().setCursor(Cursor.DEFAULT);
        }

        //redirectMousePressed(ev);
        //windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        getWindow().getScene().setCursor(saveCursor);
        resizer.start(ev, this, saveCursor, getSupportedCursors());
    }

    /*    protected void redirectMousePressed(MouseEvent ev) {
        cursorSupported = isCursorSupported(saveCursor);
        if (!cursorSupported) {
            window.getScene().setCursor(Cursor.DEFAULT);
            return;
        }
    }
     */
 /*    protected void onMouseReleased(MouseEvent ev) {
        //getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
        getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);

        hide();

        if (rectangleFrame != null) {
            Platform.runLater(() -> {
                rectangleFrame.setVisible(true);
            });

        }
    }
     */
    @Override
    protected void finalizeOnHide(Node node) {
        if (root != null) {
            //root.prefWidthProperty().unbind();
            //root.prefHeightProperty().unbind();
            root.setPrefWidth(-1);
            root.setPrefHeight(-1);
            root = null;
        }
        if (node != null) {
            node.boundsInParentProperty().removeListener(boundsInParentListener);
        }
        if (window != null) {
            //removeWindowListeners();
            window.hide();
            window = null;
        }

    }

    public Resizer getResizer() {
        return resizer;
    }

    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }

    public boolean isCursorSupported(Cursor cursor) {
        if (cursor == null || cursor == Cursor.DEFAULT) {
            return false;
        }
        boolean retval = false;
        for (Cursor c : getSupportedCursors()) {
            if (c == cursor) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    /*    public static class LBListener implements ChangeListener<Bounds> {

        private WindowNodeFraming wnf;

        public LBListener(WindowNodeFraming wnf) {
            this.wnf = wnf;
        }

        @Override
        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
            if (newValue == null) {
                return;
            }

            Bounds sb = wnf.getNode().localToScreen(wnf.getNode().getBoundsInLocal());
            if (sb == null) {
                return;
            }
            wnf.setWorkWidth(newValue.getWidth());
            wnf.setWorkHeight(newValue.getHeight());
            wnf.setWindowSize(newValue, wnf.borderWidth, wnf.borderHeight);

        }

    }
     */
}
