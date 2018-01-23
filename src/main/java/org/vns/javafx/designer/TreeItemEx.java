package org.vns.javafx.designer;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 *
 */
public class TreeItemEx extends TreeItem<Object> {

    //private TreeItemBuilder placeholderBuilder; 
    
    public TreeItemEx() {

    }

    public TreeItemEx(Object value) {
        super(value);
    }

    public TreeItemEx(Object value, Node graphic) {
        super(value, graphic);
    }

/*!!!23.01    public TreeItemBuilder getBuilder() {
        TreeItemBuilder retval = null;
        if (getValue().isPlaceholder() && getValue().getTreeItemObject() == null ) {
            ////20.01retval = this.getPlaceholderBuilder();
        } else {
            ////20.01retval = TreeItemBuilderRegistry.getInstance().getBuilder(getValue().getTreeItemObject());
        }
        return retval;
    }    
*/    

/*    public TreeItemEx treeItemOf(Object obj) {
        TreeItemEx retval = null;
        TreeItemEx t = (TreeItemEx) EditorUtil.findRootTreeItem(this);
        if (t == null) {
            return null;
        }
        t = (TreeItemEx) EditorUtil.findChildTreeItem(t, obj);
        if (t != null) {
            retval = t;
        }
        return retval;
    }
*/    
/*    public TreeItemBuilder getPlaceholderBuilder() {
        return placeholderBuilder;
    }
    public void setPlaceholderBuilder(TreeItemBuilder placeholderBuilder) {
        this.placeholderBuilder = placeholderBuilder;
    }
*/    
/*    public TreeItemEx createPlaceholder(int placeholderId, Object newValue)  {
        TreeItemEx ph = null; 
        //20.01if ( getBuilder() instanceof PlaceholderBuilderFactory ) {
            //20.01PlaceholderBuilder pb = ((PlaceholderBuilderFactory)getBuilder()).getPlaceholderBuilder(placeholderId);            
            //20.01ph = pb.buildPlaceholder(newValue);
            //20.01ph.setPlaceholderBuilder((TreeItemBuilder) pb);
        //20.01}
        return ph;
    }
*/    
/*    public boolean isAdmissiblePosition(TreeView treeView, org.vns.javafx.dock.api.editor.TreeItemEx target,
            org.vns.javafx.dock.api.editor.TreeItemEx place,
            Object dragObject) {
        if (target.getValue().getTreeItemObject() == dragObject) {
            return false;
        }
        //System.err.println("TreeItemBuilder isAdmissiblePosition 1 " );
        org.vns.javafx.dock.api.editor.TreeItemEx dragItem = org.vns.javafx.dock.api.editor.EditorUtil.findTreeItemByObject(treeView, dragObject);
        //
        // We do not want to insert the draggedItem before or after itself
        //
        if (target == place.getParent() && dragItem != null) {
            if (dragItem == place || dragItem.previousSibling() == place) {
                //System.err.println("TreeItemBuilder isAdmissiblePosition 2 " + ((TreeItemEx)dragItem).getObject() );

                return false;
            }
        } else if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {
            int level = treeView.getTreeItemLevel(target) + 1;
            TreeItem<org.vns.javafx.dock.api.editor.ItemValue> actualPlace = org.vns.javafx.dock.api.editor.EditorUtil.parentOfLevel(treeView, place, level);
            if (dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
//                System.err.println("builder 2");
                //System.err.println("TreeItemBuilder isAdmissiblePosition 3 " );

                return false;
            }
        }
        //System.err.println("TreeItemBuilder isAdmissiblePosition 4 " );

        return isAcceptable(target.getObject(), dragObject);
    }
*/    
    
    private String propertyName;
    private int index;
    private int dragDropQualifier;
    
    private ItemType itemType = ItemType.CONTENT;
    
    public static enum ItemType {
        CONTENT, HEADER, PLACEHOLDER
    }
    
    private Node cellGraphic;

    public Node getCellGraphic() {
        return cellGraphic;
    }

    public void setCellGraphic(Node cellGraphic) {
        this.cellGraphic = cellGraphic;
    }
    
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    public int getIndex() {
        return index;
    }

    public int getDragDropQualifier() {
        return dragDropQualifier;
    }

    public void setDragDropQualifier(int dragDropQualifier) {
        this.dragDropQualifier = dragDropQualifier;
    }

    public ItemType getItemType() {
        return itemType;
    }

    protected void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    
}
