package org.vns.javafx.designer;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 *
 * @author Valery
 * @param <T> ??
 */
public class TreeViewEx<T> extends TreeView implements EventHandler<NodeDragEvent> {

    public static final String LOOKUP_SELECTOR = "UUID-e651abfa-c321-4249-b78a-120db404b641";
    private final SceneGraphView sceneGraphView;
            
    private final NodeDragEvent nodeDragEvent = new NodeDragEvent((MouseEvent) null);
    private DragEvent dragEvent;
    private boolean dragAccepted;

    public TreeViewEx(SceneGraphView editor) {
        super();
        this.sceneGraphView = editor;
        init();

    }

    public TreeViewEx(SceneGraphView editor, TreeItem<T> root) {
        super(root);
        this.sceneGraphView = editor;
        init();
    }

    private void init() {
        addEventFilter(NodeDragEvent.NODE_DRAG, this);
        getStyleClass().add(LOOKUP_SELECTOR);
    }
    public static int cc = 0;

    public NodeDragEvent getNodeDragEvent(MouseEvent ev) {
        nodeDragEvent.setMouseEvent(ev);
        return nodeDragEvent;
    }

    public DragEvent getDragEvent() {
        return dragEvent;
    }
    public boolean isDragAccepted() {
        return dragAccepted;
    }

    public void notifyDragEvent(DragEvent dragEvent) {
        this.dragEvent = dragEvent;
    }
    public void notifyDragAccepted(boolean dragAccepted) {
        this.dragAccepted = dragAccepted;
    }

    /**
     * Removes the object specified by the parameter {@code sourceTreeItem}. 
     * When a tree item @code sourceTreeItem} is dragged and dropped on another item
 {@code item2}
     *
     * @param sourceTreeItem ??
     */
    public static void updateOnMove(TreeItemEx sourceTreeItem) {
        if (sourceTreeItem != null && sourceTreeItem.getParent() != null) {
            TreeItemEx parentItem = (TreeItemEx) sourceTreeItem.getParent();
            //20.01TreeItemBuilderRegistry.getInstance().getBuilder(parentItem.getObject()).updateOnMove(sourceTreeItem);
        }
    }

/*    public void removeTreeItem(TreeItem<ItemValue> toRemove) {
        if (toRemove == null) {
            return;
        }
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        
        if (parentItem != null) {
            parentItem.getChildren().remove(toRemove);
            //Object parent = parentItem.getValue().getTreeItemObject();
            //TreeItemBuilderRegistry.getInstance().getBuilder(parent).removeChildTreeItem((TreeItemEx)parentItem, (TreeItemEx)toRemove);
        }
    }
*/
    public SceneGraphView getSceneGraphView() {
        return sceneGraphView;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TreeViewExSkin<>(this);
    }

    public VirtualScrollBar getVScrollBar() {
        return ((TreeViewExSkin) getSkin()).getVScrollBar();
    }

    public VirtualScrollBar getHScrollBar() {
        return ((TreeViewExSkin) getSkin()).getHScrollBar();
    }

    private boolean isInsideScrollBar(MouseEvent ev) {
        boolean retval = false;
        VirtualScrollBar sb = getVScrollBar();
        Bounds sbBounds = sb.localToScreen(sb.getBoundsInLocal());
        if (sbBounds.contains(ev.getScreenX(), ev.getScreenY())) {
            retval = true;
        }
        return retval;
    }

    private boolean isInsideTreeView(MouseEvent ev) {
        boolean retval = false;
        Bounds sbBounds = localToScreen(getBoundsInLocal());
        if (sbBounds.contains(ev.getScreenX(), ev.getScreenY())) {
            retval = true;
        }
        return retval;
    }

    private DragEvent createDragEvent(MouseEvent ev, EventType eventType, Object gestureSource, Object gestureTarget) {
        DragEvent retval = new DragEvent(
                eventType,
                null,
                ev.getSceneX(),
                ev.getSceneY(),
                ev.getScreenX(),
                ev.getScreenY(),
                TransferMode.MOVE,
                gestureSource,
                gestureTarget,
                null);
        return retval;

    }

    @Override
    public void handle(NodeDragEvent ev) {
        TreeItem<ItemValue> item = EditorUtil.findTreeItem(this, ev.getMouseEvent().getScreenX(), ev.getMouseEvent().getScreenY());

        DragEvent dragEvent;

        if (isInsideScrollBar(ev.getMouseEvent())) {
            dragEvent = createDragEvent(ev.getMouseEvent(), DragEvent.DRAG_ENTERED,
                    ev.getGestureSource(), getVScrollBar());
        } else {
            dragEvent = createDragEvent(ev.getMouseEvent(), DragEvent.DRAG_EXITED,
                    ev.getGestureSource(), getVScrollBar());
        }
        getVScrollBar().fireEvent(dragEvent);

        EventType dragEventType = DragEvent.DRAG_OVER;
        if (ev.getMouseEvent().getEventType() == MouseEvent.MOUSE_RELEASED) {
            dragEventType = DragEvent.DRAG_DROPPED;
        }
        dragEvent = createDragEvent(ev.getMouseEvent(), dragEventType,
                ev.getGestureSource(),
                item != null ? item.getValue().getCellGraphic().getParent() : this);

        if (item != null) {
            item.getValue().getCellGraphic().getParent().fireEvent(dragEvent);

        } else if (isInsideTreeView(ev.getMouseEvent())) {
            fireEvent(dragEvent);
        }
    }
    

}