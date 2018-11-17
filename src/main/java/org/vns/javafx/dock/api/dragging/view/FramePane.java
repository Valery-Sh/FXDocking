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
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import static org.vns.javafx.dock.api.dragging.view.FramePane.Direction.*;

/**
 *
 * @author Valery Shyskin
 */
public class FramePane extends Control {
    public static String ID = "ID-89528991-bd7a-4792-911b-21bf56660bfb";
    public static String CSS_CLASS = "CSS-89528991-bd7a-4792-911b-21bf56660bfb";
    public static final String NODE_ID = "NODE-" + ID;
    public static final String PARENT_ID = "PARENT-" + ID;

    private final ObservableMap<Direction, ResizeShape> sideShapes = FXCollections.observableHashMap();
    private Class<?> shapeClass;
    private final ObjectProperty<Node> boundNode = new SimpleObjectProperty<>();
    private final boolean enableResize;
    private Point2D startMousePos;
    //private MouseEventHandler eventHandler;

    public enum Direction {
        nShape, //north indicator
        neShape, //north-east indicator
        eShape, //east indicator
        seShape, //south-east indicator
        sShape, //south indicator

        swShape, // south-west indicator
        wShape, // west indicator
        nwShape   // north-west indicator
    }

    public FramePane() {
        this(null, true);
    }
    public FramePane(boolean enableResize) {
        this(null, enableResize);
    }
    public FramePane(Class<?> resizeShapeClass) {
        this(resizeShapeClass, true);
    }

    public FramePane(Class<?> resizeShapeClass, boolean enableResize) {
        this.shapeClass = resizeShapeClass;
        if (shapeClass == null) {
            this.shapeClass = Circle.class;
        }
        this.enableResize = enableResize;
        init();
    }

    private void init() {
        getStyleClass().add(CSS_CLASS);
        getStyleClass().add("frame-control");
        setManaged(false);
        if ( ! enableResize ) {
            setMouseTransparent(true);
        }
        //eventHandler = new MouseEventHandler(this);
    }

    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public boolean isEnableResize() {
        return enableResize;
    }

    public ObservableMap<Direction, ResizeShape> getSideShapes() {
        return sideShapes;
    }

    public ObjectProperty<Node> boundNodeProperty() {
        return boundNode;
    }

    public Node getBoundNode() {
        return boundNode.get();
    }

    public void setBoundNode(Node boundNode) {
        this.boundNode.set(boundNode);
    }

    public Class<?> getShapeClass() {
        return shapeClass;
    }
    private FrameRectangleSkin skinBase;

    @Override
    protected Skin<?> createDefaultSkin() {
        skinBase = new FrameRectangleSkin(this);
        return skinBase;
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected double computeMinWidth(final double height) {
        //if (skinBase != null) {
        return ((FrameRectangleSkin) skinBase).computeMinWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
        //} 
    }

    /**
     * Computes the minimum allowable height of the Control, based on the
     * provided width. The minimum height is not calculated within the Control,
     * instead the calculation is delegated to the
     * {@link Node#minHeight(double)} method of the {@link Skin}. If the Skin is
     * null, the returned value is 0.
     *
     * @param width The width of the Control, in case this value might dictate
     * the minimum height.
     * @return A double representing the minimum height of this control.
     */
    @Override
    protected double computeMinHeight(final double width) {
        return skinBase.computeMinHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * Computes the maximum allowable width of the Control, based on the
     * provided height. The maximum width is not calculated within the Control,
     * instead the calculation is delegated to the {@link Node#maxWidth(double)}
     * method of the {@link Skin}. If the Skin is null, the returned value is 0.
     *
     * @param height The height of the Control, in case this value might dictate
     * the maximum width.
     * @return A double representing the maximum width of this control.
     */
    @Override
    protected double computeMaxWidth(double height) {
        return skinBase.computeMaxWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * Computes the maximum allowable height of the Control, based on the
     * provided width. The maximum height is not calculated within the Control,
     * instead the calculation is delegated to the
     * {@link Node#maxHeight(double)} method of the {@link Skin}. If the Skin is
     * null, the returned value is 0.
     *
     * @param width The width of the Control, in case this value might dictate
     * the maximum height.
     * @return A double representing the maximum height of this control.
     */
    @Override
    protected double computeMaxHeight(double width) {
        return skinBase.computeMaxHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefWidth(double height) {
        return skinBase.computePrefWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefHeight(double width) {
        return skinBase.computePrefHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * {@inheritDoc}
     */
    /*    @Override
    public double getBaselineOffset() {
            return skinBase.computeBaselineOffset(snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }
     */
    /**
     * *************************************************************************
     * Implementation of layout bounds for the Control. We want to preserve *
     * the lazy semantics of layout bounds. So whenever the width/height *
     * changes on the node, we end up invalidating layout bounds. We then *
     * recompute it on demand. *
     * ************************************************************************
     */
    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren() {
        final double x = snappedLeftInset();
        final double y = snappedTopInset();
        final double w = snapSize(getWidth()) - x - snappedRightInset();
        final double h = snapSize(getHeight()) - y - snappedBottomInset();
        skinBase.layoutChildren(x, y, w, h);
        if (getBoundNode() != null) {
            Bounds b = getBoundNode().getBoundsInParent();
            b = getBoundNode().parentToLocal(b);
            b = getBoundNode().localToScene(b);
            setLayoutX(b.getMinX());
            setLayoutY(b.getMinY());
            //Rectangle r = (Rectangle) lookup("#rectangle");
            //r.setTranslateX(pb.getMinX());
            //r.setTranslateY(pb.getMinY());
            //System.err.println("isTransp = " + r.isMouseTransparent());
        }
    }

    public static class FrameRectangleSkin extends SkinBase<FramePane> {

        MouseEventHandler mouseHandler;
        Pane pane;
        FramePane ctrl;
        Rectangle rect;

        private ChangeListener<Node> boundNodeListener;
        private final ChangeListener<Bounds> boundsInParentListener = (o, ov, nv) -> {
            adjustBoundsToNode(nv);
        };
        private final ChangeListener<Transform> localToSceneTransformListener = (o, ov, nv) -> {
            //adjustBoundsToNode(ctrl.getBoundNode().getBoundsInParent());
        };

        public FrameRectangleSkin(FramePane control) {
            super(control);
            this.ctrl = control;
            rect = new Rectangle();
            rect.setId("rectangle");
            mouseHandler = new MouseEventHandler(ctrl);
            rect.setMouseTransparent(true);
            if (ctrl.isEnableResize()) {
                rect.getStyleClass().add("resizable");
            } else {
                rect.getStyleClass().add("not-resizable");
            }
            pane = new Pane(rect);
            
            rect.toBack();
            pane.setStyle("-fx-background-color: transparent");
            ctrl.setStyle("-fx-background-color: transparent");

            pane.setMouseTransparent(true);
            rect.setMouseTransparent(true);
            ctrl.setManaged(true);
            //pane.setManaged(false);
            ctrl.setManaged(false);
            if (ctrl.isEnableResize()) {
                createSideShapes();
            }
            initBoundNode();
            getChildren().add(pane);
            pane.toBack();
        }

        protected void createSideShapes() {
            ResizeShape sh = createSideShape(nShape);
            sh.centerXProperty().bind(rect.xProperty().add(rect.widthProperty().divide(2)));
            sh.centerYProperty().bind(rect.yProperty());

            sh = createSideShape(neShape);
            sh.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
            sh.centerYProperty().bind(rect.yProperty());

            sh = createSideShape(eShape);
            sh.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
            sh.centerYProperty().bind(rect.yProperty().add(rect.heightProperty().divide(2)));

            sh = createSideShape(seShape);
            sh.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
            sh.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

            sh = createSideShape(sShape);
            sh.centerXProperty().bind(rect.xProperty().add(rect.widthProperty().divide(2)));
            sh.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

            sh = createSideShape(swShape);
            sh.centerXProperty().bind(rect.xProperty());
            sh.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

            sh = createSideShape(wShape);
            sh.centerXProperty().bind(rect.xProperty());
            sh.centerYProperty().bind(rect.yProperty().add(rect.heightProperty().divide(2)));

            sh = createSideShape(nwShape);
            sh.centerXProperty().bind(rect.xProperty());
            sh.centerYProperty().bind(rect.yProperty());

        }

        protected ResizeShape createSideShape(Direction d) {
            ResizeShape retval = new ResizeShape(ctrl.getShapeClass());
            ctrl.getSideShapes().put(d, retval);
            getChildren().add(retval);
            retval.toFront();
            return retval;
        }

        protected void initBoundNode() {

            boundNodeListener = (v, ov, nv) -> {
                if (ov != null) {
                    ov.boundsInParentProperty().removeListener(boundsInParentListener);
                    ov.localToSceneTransformProperty().removeListener(localToSceneTransformListener);

                    if (!ctrl.getSideShapes().isEmpty()) {
                        //getSideShapes().unbind(); // to remove mouseEventListeners
                        removeShapeMouseEventHandlers();
                    }
                }
                if (nv != null) {
                    if (!ctrl.getSideShapes().isEmpty()) {
                        //getSideShapes().bind(); // to remove mouseEventListeners
                        addShapeMouseEventHandlers();
                    }
                    nv.boundsInParentProperty().addListener(boundsInParentListener);
                    nv.localToSceneTransformProperty().addListener(localToSceneTransformListener);

                    if (ctrl.getBoundNode().getScene() != null && ctrl.getBoundNode().getScene().getWindow() != null) {
                        Bounds curPb = ctrl.getBoundNode().getBoundsInParent();
                        adjustBoundsToNode(curPb);
                    }
                } else if (!ctrl.getSideShapes().isEmpty()) {
                    removeShapeMouseEventHandlers();
                }

                if (nv == null) {
                    ctrl.setVisible(false);
                } else {
                    ctrl.setVisible(true);
                }
            };
            ctrl.boundNodeProperty().addListener(boundNodeListener);
        }

        protected void adjustBoundsToNode(Bounds boundsInParent) {
            //
            // We change position of the rectangle in order to enforce layotChildren
            // to be executed. rutLater will restore to (0,0) position
            //
            rect.setX(1);
            rect.setY(1);
            Platform.runLater(() -> {
                rect.setX(0);
                rect.setY(0);
                rect.setWidth(boundsInParent.getWidth());
                rect.setHeight(boundsInParent.getHeight());

            });
        }

        protected void addShapeMouseEventHandlers() {
            ctrl.getSideShapes().forEach((k, v) -> {
                addShapeMouseEventHandlers(v);
            });

        }

        protected void removeShapeMouseEventHandlers() {
            ctrl.getSideShapes().forEach((k, v) -> {
                removeShapeMouseEventHandlers(v);
            });
        }

        protected void addShapeMouseEventHandlers(ResizeShape shape) {
            shape.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            shape.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
            shape.addEventFilter(MouseEvent.MOUSE_MOVED, mouseHandler);
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, mouseHandler);
            shape.addEventFilter(MouseEvent.DRAG_DETECTED, mouseHandler);
            //shape.setVisible(true);
        }

        protected void removeShapeMouseEventHandlers(ResizeShape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
            shape.removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseHandler);
            shape.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseHandler);
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, mouseHandler);
            shape.removeEventFilter(MouseEvent.DRAG_DETECTED, mouseHandler);
        }

        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.minWidth(height);
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.minHeight(width);
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.prefWidth(height) + leftInset + rightInset;
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.prefHeight(width) + topInset + bottomInset;
        }

        @Override
        protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            if (ctrl.getBoundNode() != null) {
                Bounds sb = ctrl.getBoundNode().localToScene(ctrl.getBoundNode().getLayoutBounds());
                Bounds bnds = ctrl.getBoundNode().getBoundsInParent();
                pane.resizeRelocate(x, y, w, h);
            } else {
                pane.resizeRelocate(x, y, w, h);
            }
        }

    }//skin

    public static class MouseEventHandler implements EventHandler<MouseEvent> {

        private FramePane frameRect;

        public MouseEventHandler(FramePane frameRect) {
            this.frameRect = frameRect;
        }

        public void handle(MouseEvent ev, ResizeShape shape, Cursor c) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Point2D pt = shape.screenToLocal(ev.getScreenX(), ev.getScreenY());

                shape.getScene().setCursor(c);
            } else if (ev.getEventType() == MouseEvent.MOUSE_EXITED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                removeMouseExitedListener(shape);
                frameRect.setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));

            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class);
                frameRect.setVisible(false);
                wnf.show(frameRect.getBoundNode());
                wnf.redirectMouseEvents(ev, frameRect.getStartMousePos(), frameRect);
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
                addMouseExitedListener(shape);
            }
        }

        @Override
        public void handle(MouseEvent ev) {
            ResizeShape shape = (ev.getSource() instanceof ResizeShape) ? (ResizeShape) ev.getSource() : null;
            if (shape == null) {
                return;
            }
            if (shape == frameRect.getSideShapes().get(nShape)) {
                handle(ev, frameRect.getSideShapes().get(nShape), Cursor.N_RESIZE);

            } else if (shape == frameRect.getSideShapes().get(neShape)) {
                handle(ev, frameRect.getSideShapes().get(neShape), Cursor.NE_RESIZE);
            } else if (shape == frameRect.getSideShapes().get(eShape)) {
                handle(ev, frameRect.getSideShapes().get(eShape), Cursor.E_RESIZE);

            } else if (shape == frameRect.getSideShapes().get(seShape)) {
                handle(ev, frameRect.getSideShapes().get(seShape), Cursor.SE_RESIZE);
            } else if (shape == frameRect.getSideShapes().get(sShape)) {
                handle(ev, frameRect.getSideShapes().get(sShape), Cursor.S_RESIZE);
            } else if (shape == frameRect.getSideShapes().get(swShape)) {
                handle(ev, frameRect.getSideShapes().get(swShape), Cursor.SW_RESIZE);
            } else if (shape == frameRect.getSideShapes().get(wShape)) {
                handle(ev, frameRect.getSideShapes().get(wShape), Cursor.W_RESIZE);
            } else if (shape == frameRect.getSideShapes().get(nwShape)) {
                handle(ev, frameRect.getSideShapes().get(nwShape), Cursor.NW_RESIZE);
            }
            ev.consume();
        }

        protected void removeMouseExitedListener(ResizeShape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
        }

        protected void addMouseExitedListener(ResizeShape shape) {
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, this);
        }
    }
}
