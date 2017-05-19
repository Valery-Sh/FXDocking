/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery
 */
public class TabItemBuilder extends TreeItemBuilder {


    public TabItemBuilder() {
    }

    private void init() {

    }

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Tab) {
            Tab tab = (Tab) obj;
            retval = createItem((Tab) obj);
            if (tab.getContent() != null) {
                TreeItemBuilder b = TreeItemRegistry.getInstance().getBuilder(tab.getContent());
                retval.getChildren().add(b.build(tab.getContent()));
            }
        }
        return retval;
    }

    @Override
    protected Node createDefaultContent(Object obj) {
        String text = ((Tab) obj).getText();
        text = text == null ? "" : text;
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Checks whether the specified object can be used as a value of the graphic
     * property. May accepts only objects of type {@literal Node} witch becomes
     * a value of the graphic property. of the
     *
     * @param obj an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node/
     */
    @Override
    public boolean isAcceptable(Object obj) {
        return obj != null && (obj instanceof Node);
    }


    @Override
    public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItemEx retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        Object value = dg.getGestureSourceObject();
        Tab tab = (Tab) ((ItemValue) target.getValue()).getTreeItemObject();
        if (isAcceptable(target, value)) {
            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                TreeCell cell = (TreeCell) dg.getGestureSource();
                if (cell.getTreeItem() instanceof TreeItemEx) {
                    notifyTreeItemRemove(treeView, cell.getTreeItem());
                    cell.getTreeItem().getParent().getChildren().remove(cell.getTreeItem());
                }
            } else if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof Node)) {
                TreeItem<ItemValue> treeItem= TreeItemBuilder.findTreeItemByObject(treeView, dg.getGestureSource());
                if ( treeItem != null && treeItem.getParent() != null) {
                    //
                    // We must delete the item
                    //
                    notifyTreeItemRemove(treeView, treeItem);
                    treeItem.getParent().getChildren().remove(treeItem);
                }
            }
            
            retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
            if ( ! target.getChildren().isEmpty() ) {
                target.getChildren().clear();
            }
            target.getChildren().add(retval);
            tab.setContent((Node)value);
            Node n = (Node) value;
            //((Pane)n.getParent()).getChildren().remove(n);
            //System.err.println("tab.getContent()=" + tab.getContent() + " ; new Value)=" + value);

        }
        return retval;
    }
/*    @Override
    public void childrenTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        TreeItem<ItemValue> parent = toRemove.getParent();
        if (parent != null ) {
            Object obj = ((ItemValue) parent.getValue()).getTreeItemObject();
            if ( obj instanceof Tab) {
                ((Tab)obj).setContent(null);
            }
//            TreeItemRegistry.getInstance().getBuilder(obj).childrenTreeItemRemove(treeView, toRemove);
        }
    }
*/    
    @Override
    public void remove(Object parent, Object toRemove) {
        if (parent != null && ( parent instanceof Tab) ) {
            ((Tab)parent).setContent(null);
        }
    }
    
}