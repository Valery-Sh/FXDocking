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
package org.vns.javafx.dock.api.designer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class SceneGraphViewTargetContext extends LayoutContext {

    private ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();

    public SceneGraphViewTargetContext(Node targetNode) {
        super(targetNode);
    }

/*    public SceneGraphViewTargetContext(Dockable dockable) {
        super(dockable);
    }
*/
    @Override
    protected void initLookup(ContextLookup lookup) {
        //lookup.putUnique(DragManagerFactory.class, new TreeItemDragManagerFactory());
        lookup.putUnique(FloatViewFactory.class, new TreeItemFloatViewFactory());

    }

    public ObjectProperty<Point2D> mousePositionProperty() {
        return mousePosition;
    }

    public Point2D getMousePosition() {
        return mousePosition.get();
    }

    public void setMousePosition(Point2D pos) {
        this.mousePosition.set(pos);
    }

    @Override
    public boolean isDocked(Node node) {
        return false;
    }

    @Override
    public void removeValue(Dockable dockable) {
        Object value = dockable.getContext().getDragContainer().getValue();
        new TreeItemBuilder().removeByItemValue(getTreeView(), value);
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
//        System.err.println("DOCK: dockable.node() = " + dockable.node());
        Dockable d = dockable;
        Window window = null;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc != null && dc.getValue() != null) {
            window = dc.getFloatingWindow(dockable);
            d = Dockable.of(dc.getValue());
        }

        Node node = null;
        if (d != null) {
            node = d.node();
        }

        if (window == null && node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            window = node.getScene().getWindow();
        }
        if (window == null && dockable.node() != node) {
            node = dockable.node();
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                window = node.getScene().getWindow();
            }
        }
        Object value = getValue(dockable);
        Object toAccept = value;
        if (value instanceof Dockable) {
            toAccept = ((Dockable) value).node();
        }
        boolean accepted = acceptValue(mousePos, toAccept);

        if (accepted && d != null) {
            if (d.node().getScene() != null && d.node().getScene().getWindow() != null) {

            }
            //d.getContext().setLayoutContext(this);
        }
        if (accepted && window != null) {
            if ((window instanceof Stage)) {
                ((Stage) window).close();
            } else {
                window.hide();
            }
        }
        if (Dockable.of(toAccept) == null) {
            return;
        }

    }

    /**
     * The method is called by the object {@code DragManager } when the mouse
     * event of type {@code MOUSE_DRAGGED} is handled. The class uses the method
     * when implements animation for the tree view scroll bar. Sets the new
     * mouse position of the (@code mousePosition } property.
     *
     * @param dockable the dragged object
     * @param ev the object of type {@code MouseEvent }
     * @see #mousePositionProperty()
     */
    @Override
    public void mouseDragged(Dockable dockable, MouseEvent ev) {
        setMousePosition(new Point2D(ev.getScreenX(), ev.getScreenY()));
    }

    /**
     * Checks whether the given {@code dockable}  can be accepted by this context.
     * 
     * @param dockable the object to be checked
     * @param mousePos the current mouse position
     * @return true if the {@code dockable} can be accepted
     */
    @Override
    public boolean isAdmissiblePosition(Dockable dockable, Point2D mousePos) {

        SceneGraphView gv = (SceneGraphView) getLayoutNode();
        if ( gv.getTreeView( mousePos.getX(), mousePos.getY()) != null )  {
            if ( gv.getTreeView().getRoot() == null ) {
                return true;
            }
        }
        TreeItemEx place = gv.getTreeItem(mousePos);
        
        if (place == null) {
            return false;
        }

        TreeItemEx target = getDragIndicator().getTargetTreeItem(mousePos.getX(), mousePos.getY(), place);

        if (target == null) {
            return false;
        }

        Object value = getValue(dockable);

        if (value == null) {
            return false;
        }

        if (value instanceof Dockable) {
            value = ((Dockable) value).node();
        }
        return new TreeItemBuilder().isAdmissiblePosition(gv.getTreeView(), target, place, value);

    }

    protected TreeViewEx getTreeView() {
        return ((SceneGraphView) getLayoutNode()).getTreeView();
    }

    protected DragIndicatorManager getDragIndicatorManager() {
        return (DragIndicatorManager) getLookup().lookup(IndicatorManager.class);
    }

    protected DragIndicator getDragIndicator() {
        return getDragIndicatorManager().getDragIndicator();
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {
        return true;
    }

    protected boolean acceptValue(Point2D mousePos, Object value) {
        SceneGraphView gv = (SceneGraphView) getLayoutNode();
//        System.err.println("DO DOCK");
        boolean retval = false;
        if ( gv.getTreeView().getRoot() == null ) {
//            System.err.println("SceneGVcontext: acceptValue value=" + value);
            gv.setRoot((Node)value);
            return true;
        }
        TreeItemEx place = gv.getTreeItem(mousePos);
//        System.err.println("doDock node = " + node);
//        System.err.println("doDock place = " + place);
//        System.err.println("doDock place.value = " + place.getValue());

        if (place != null) {
            TreeItemEx target = getDragIndicator().getTargetTreeItem(mousePos.getX(), mousePos.getY(), place);
            if (target != null) {
//                System.err.println("doDock layoutNode = " + layoutNode);
                new TreeItemBuilder().accept(gv.getTreeView(), target, place, value);

//                System.err.println("accept layoutNode = " + layoutNode.getValue());
//                System.err.println("accept place = " + place.getValue());
                retval = true;
            }
        }
        return retval;
    }
    @Override
    public boolean restore(Dockable dockable) {
        return false;
    }

    @Override
    public void remove(Node dockNode) {
        TreeItemEx item = EditorUtil.findTreeItemByObject(getTreeView(), dockNode);
        if (item != null) {
            new TreeItemBuilder().updateOnMove(item);
        }

    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
