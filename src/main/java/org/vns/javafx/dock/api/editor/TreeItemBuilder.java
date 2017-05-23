package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public class TreeItemBuilder {

    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";

    public TreeItemBuilder() {
        init();
    }

    private void init() {
    }

    public boolean isAcceptable(Object obj) {
        return false;
    }

    public boolean isAcceptable(TreeItem<ItemValue> target, Object obj) {
        if (target.getValue().getTreeItemObject() == obj) {
            return false;
        }
        return isAcceptable(obj);
    }

    public boolean isDragTarget() {
        return true;
    }

    public boolean isDragPlace(TreeItem<ItemValue> target, TreeItem<ItemValue> place, Object source) {
        boolean retval = true;
        if (place.getValue().isPlaceholder() && target != null) {
            TreeItemBuilder builder = place.getValue().getBuilder();
            if (!(builder instanceof PlaceHolderBuilder) && place.getParent() != null) {
                builder = place.getParent().getValue().getBuilder().getPlaceHolderBuilder(place.getParent());
                System.err.println("   ---  1 IS DRAG PLACE = " + retval);
                retval = builder.isDragPlace(target, place, source);
            }

        } else if (target == null) {
            retval = false;
        }

        return retval;
    }

    /**
     *
     * @param treeView the treeView/ Cannot be null
     * @param target the item which is an actual target item to accept a dragged
     * object
     * @param place the item which is a gesture target during the drag&drop
     * operation
     * @param dragObject an object which is an actual object to be accepted by
     * the target item.
     * @return true id the builder evaluates that a specified dragObject can be
     * accepted by the given target tree item
     */
    public boolean isAdmissiblePosition(TreeItem<ItemValue> target,
            TreeItem<ItemValue> place,
            Object dragObject) {
        if (target.getValue().getTreeItemObject() == dragObject) {
            return false;
        }
        return isAcceptable(dragObject);
    }

    public TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        return null;
    }

    //public void childrenTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {    }
    protected void removeObject(Object parent, Object toRemove) {

    }

    protected void removeItem(TreeItem<ItemValue> parent, TreeItem<ItemValue> toRemove) {

    }

    protected void notifyObjectRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null && toRemove != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            Object remove = ((ItemValue) toRemove.getValue()).getTreeItemObject();
            TreeItemRegistry.getInstance().getBuilder(parent).removeObject(parent, remove);
        }
    }

    /*    protected void notifyGestureObjectRemove(TreeView treeView, DragNodeGesture gesture) {
        System.err.println("notifyGestureObjectRemove obj = " + gesture.getGestureSourceObject());            
        Object obj = gesture.getGestureSourceObject();
        TreeItem item = EditorUtil.findTreeItemByObject(treeView, obj);
        if ( item != null && item.getParent() != null ) {
            TreeItemRegistry.getInstance().getBuilder(gesture.getGestureSourceObject()).removeObject(item.getParent(),item);
        }
    }    
     */
    protected void notifyTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        if (toRemove == null) {
            return;
        }
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            TreeItemRegistry.getInstance().getBuilder(parent).removeItem(parentItem, toRemove);
        }
    }

    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof Node) {
            retval = createItem(obj);
        }
        return retval;
    }

    protected TreeItem createItem(Object obj, Object... others) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);

        itv.setTreeItemObject(obj);
        itv.setCellGraphic(anchorPane);

        item.setValue(itv);
        box.getChildren().add(createItemContent(obj, others));

        return item;
    }

    public HBox getItemContentPane(TreeItem<ItemValue> item) {
        return (HBox) ((AnchorPane) item.getValue().getCellGraphic()).getChildren().get(0);
    }

    /*    public static TreeItem<ItemValue> findTreeItemByObject(TreeItem<ItemValue> item, Object obj) {
        TreeItem<ItemValue> retval = null;
        TreeItem<ItemValue> root = null;
        TreeItem<ItemValue> parent = item;

        while (parent != null) {
            root = parent;
            parent = parent.getParent();
        }
        return retval;
    }
    public static TreeItem<ItemValue> findTreeItemByObject(TreeView<ItemValue> treeView, Object obj) {
        TreeItem<ItemValue> retval = null;
        TreeItem<ItemValue> root = treeView.getRoot();
        for ( TreeItem ti : root.getChildren()) {
            TreeItem found = findChildTreeItem(ti, obj);
            if ( found != null ) {
                retval = found;
                break;
            }
        }
        return retval;
    }
    
    public static TreeItem<ItemValue> findChildTreeItem(TreeItem<ItemValue> treeItem, Object obj) {
        TreeItem<ItemValue> retval = null;
        for ( TreeItem<ItemValue> ti : treeItem.getChildren()) {
            if ( ti.getValue().getTreeItemObject() == obj) {
                retval = ti;
                break;
            }
        }
        return retval;
    }
     */
    public String getStyle() {
        return "-fx-backGround-color: aqua";
    }

    protected Node createItemContent(Object obj, Object... others) {
        return createDefaultContent(obj, others);
    }

    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if (obj != null && (obj instanceof Labeled)) {
            text = ((Labeled) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Returns an Empty array of objects of type {@literal TreeItem}.
     *
     * @param obj
     * @return
     */
    /*    public TreeItem[] createPlaceHolders(Object obj) {
        return new TreeItem[0];
    }
     */
 /*    public boolean hasPlaceHolders() {
        return false;
    }
     */
    public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
        return null;
    }

    public static interface PlaceHolderBuilder {

    }
}// TreeItemBuilder
