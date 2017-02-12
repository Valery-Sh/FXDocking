package org.vns.javafx.dock.api;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public class ScenePaneController extends DockTargetController {

    private final Dockable dockable;

    private ChangeListener<? super Parent> parentListener;

    public ScenePaneController(Dockable dockable) {
        super(dockable);
        this.dockable = dockable;
        init();

    }

    private void init() {
        parentListener = this::parentChanged;
        if (isDocked(dockable.node())) {
            setDockPane((Pane) dockable.node().getParent());
        }
        dockable.node().parentProperty().addListener(parentListener);
    }

    protected void parentChanged(ObservableValue<? extends Parent> value, Parent oldValue, Parent newValue) {
        if (newValue != null && !(newValue instanceof Pane)) {
            return;
        }

        setDockPane((Pane) newValue);

        if (oldValue != null) {
            oldValue.parentProperty().removeListener(parentListener);
        }
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            if (node.getParent() != null && (node.getParent() instanceof Pane)) {
                retval = true;
            }
        }
        return retval;
    }

    @Override
    public void remove(Node dockNode) {
        if (dockNode.getParent() != null && dockNode.getParent().getParent() != null
                && (dockNode.getParent().getParent() instanceof SplitPane)) {
            ((SplitPane) dockNode.getParent().getParent()).getItems().remove(dockNode);
        }

        if (dockNode.getParent() != null && (dockNode.getParent() instanceof Pane)) {
            ((Pane) dockNode.getParent()).getChildren().remove(dockNode);
        }
    }

}