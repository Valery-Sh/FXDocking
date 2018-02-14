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
package org.vns.javafx.dock.incubator.designer;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.incubator.designer.SceneGraphView.ANCHOR_OFFSET;
import static org.vns.javafx.dock.incubator.designer.SceneGraphView.FIRST;
import org.vns.javafx.dock.incubator.designer.TreeItemEx.ItemType;
import static org.vns.javafx.dock.incubator.designer.TreeItemEx.ItemType.CONTENT;
import static org.vns.javafx.dock.incubator.designer.TreeItemEx.ItemType.DEFAULTLIST;
import static org.vns.javafx.dock.incubator.designer.TreeItemEx.ItemType.LIST;
import org.vns.javafx.dock.incubator.designer.bean.BeanAdapter;
import org.vns.javafx.dock.incubator.designer.bean.ReflectHelper;

/**
 *
 * @author Valery
 */
public class TreeItemBuilder {

    public static final String ACCEPT_TYPES_KEY = "tree-item-builder-accept-types";
    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";

    //private final Object nodeObject;
    public TreeItemBuilder() {

    }

    public TreeItemEx build(Object obj) {
        return build(obj, null);
    }

    protected TreeItemEx build(Object obj, NodeElement p) {
        TreeItemEx retval;
        if (p != null && (p instanceof NodeContent)) {
            retval = createContentItem(obj, (NodeContent) p);
        } else if (p != null && (p instanceof NodeList)) {
            retval = createListContentItem(obj, (NodeList) p);
        } else {
            retval = createListElementItem(obj);
        }
        if (p != null && (p instanceof Property)) {
            retval.setPropertyName(((Property) p).getName());
        }
        if (p != null && (p instanceof NodeList)) {
            ObservableList ol = (ObservableList) obj;
            for (int i = 0; i < ol.size(); i++) {
                retval.getChildren().add(build(ol.get(i)));
            }
            return retval;
        }
        if (obj == null && !(p instanceof NodeList)) {
            return retval;
        }

        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(obj);

        BeanAdapter adapter = new BeanAdapter(obj);

        nd.getProperties().forEach(cp -> {
            int cpIdx = nd.getProperties().indexOf(cp);
            Object cpObj = adapter.get(cp.getName());
            boolean isNodeContent = false;
            boolean hideIfNull = false;

            if ((cp instanceof NodeList)) {
                if (nd.getProperties().size() == 1 && !((NodeList) cp).isAlwaysVisible()) {
                    //
                    // Omit TreeItem for ListItem
                    //
                    ObservableList ol = (ObservableList) cpObj;
                    for (int i = 0; i < ol.size(); i++) {
                        retval.getChildren().add(build(ol.get(i)));
                    }
                    retval.setItemType(ItemType.DEFAULTLIST);
                } else {
                    TreeItemEx listItem = build(cpObj, (NodeList) cp);
                    retval.getChildren().add(listItem);
                    listItem.setPropertyName(cp.getName());

                }

            } else if ((cp instanceof NodeContent) && (cpObj != null || !((NodeContent) cp).isHideWhenNull())) {
                TreeItemEx item = build(cpObj, (NodeContent) cp);
                item.setPropertyName(cp.getName());
                retval.getChildren().add(item);
            }
        });
        return retval;
    }

    public final HBox createListElementItemContent(Object obj) {

        HBox box = new HBox(new HBox()); // placeholder 
        NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
        String tp = nc.getTitleProperty();
        String text = "";
        if (tp != null) {
            BeanAdapter adapter = new BeanAdapter(obj);
            text = (String) adapter.get(tp);
            if (text == null) {
                text = "";
            }
        }
        Label label = new Label((obj.getClass().getSimpleName() + " " + text).trim());
        String styleClass = nc.getStyleClass();
        if (styleClass == null) {
            styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        }
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    public final TreeItemEx createListElementItem(Object obj) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);

        TreeItemEx retval = new TreeItemEx();

        retval.setValue(obj);

        box.getChildren().add(createListElementItemContent(obj));

        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.ELEMENT);
        try {
            retval.registerChangeHandlers();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    protected HBox createListContentContent(Object obj, NodeList h) {
        HBox box = new HBox(new HBox()); // placeholder 
        String title = h.getTitle();

        if (title == null) {
            title = NodeList.DEFAULT_TITLE;
        }

        Label label = new Label(title.trim());
        String styleClass = h.getStyleClass();
        if (styleClass == null) {
            styleClass = NodeList.DEFAULT_STYLE_CLASS;
        }
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    public final TreeItemEx createListContentItem(Object obj, NodeList h) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);

        TreeItemEx retval = new TreeItemEx();

        retval.setValue(obj);
        box.getChildren().add(createListContentContent(obj, h));

        retval.setCellGraphic(anchorPane);

        retval.setItemType(TreeItemEx.ItemType.LIST);
        try {
            retval.registerChangeHandlers();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public final TreeItemEx createContentItem(Object obj, NodeContent cp) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        //anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx retval = new TreeItemEx();

        retval.setValue(obj);

        box.getChildren().add(createContentItemContent(obj, cp));
        retval.setCellGraphic(anchorPane);
        retval.setItemType(TreeItemEx.ItemType.CONTENT);
        try {
            retval.registerChangeHandlers();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TreeItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retval;
    }

    protected HBox createContentItemContent(Object obj, NodeContent cp) {
        Label iconLabel = new Label();
        HBox retval = new HBox(iconLabel);

        String style = cp.getStyleClass();
        if (style == null) {
            style = NodeContent.DEFAULT_STYLE_CLASS;
        }

        iconLabel.getStyleClass().add(style);

        String title = cp.getTitle();
        if (title == null) {
            title = NodeContent.DEFAULT_TITLE;
        }

        if (obj == null) {
            iconLabel.setText(title.trim());
        } else {

            NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(obj);
            title = "";
            String tp = nc.getTitleProperty();
            if (tp != null) {
                BeanAdapter adapter = new BeanAdapter(obj);
                title = (String) adapter.get(tp);
                if (title == null) {
                    title = "";
                }
            }
            Label glb = new Label(obj.getClass().getSimpleName());

            glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
            retval.getChildren().add(glb);

            glb.setText(glb.getText() + " " + title.trim());
        }
        return retval;
    }

    /**
     *
     * @param treeView the treeView/ Cannot be null
     * @param target the item which is an actual target item to accept a dragged
     * object
     * @param place the item which is a gesture target during the drag-and-drop
     * operation
     * @param dragObject an object which is an actual object to be accepted by
     * the target item.
     * @return true id the builder evaluates that a specified dragObject can be
     * accepted by the given target tree item
     */
    public boolean isAdmissiblePosition(TreeViewEx treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        //
        // Check if the dragObject equals to targetItemObject 
        //
        if (target.getValue() == dragObject) {
            return false;
        }
        TreeItemEx dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        /*        System.err.println("dragItem = " + dragItem);
        System.err.println("target = " + target);
        System.err.println("place = " + place);
        System.err.println("place.getParent = " + place.getParentSkipHeader());
        System.err.println("dragItem.previousSibling() = " + dragItem.previousSibling());
        System.err.println("-----------------------------------------");
         */
        //
        // First check if the target item corresponds to LIST ItemType
        //
        boolean isList = target.getItemType() == LIST;

        NodeDescriptor nd = null;
        if (!isList) {
            nd = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());

            if (target.getValue() != null && nd.getProperties().size() == 1) {
                Property p = nd.getProperties().get(0);
                if ((p instanceof NodeList) && !((NodeList) p).isAlwaysVisible()) {
                    isList = true;
                }
            }
        }
        //if (isList) {
        if (target.getItemType() == LIST || target.getItemType() == DEFAULTLIST) {
            if (dragItem.previousSibling() == place) {
                return false;
            }
            if (dragItem == place) {

                if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {

                    int level = treeView.getTreeItemLevel(target) + 1;

                    TreeItemEx actualPlace = (TreeItemEx) EditorUtil.parentOfLevel(treeView, place, level);
                    if (dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
                        return false;
                    }
                }
            }
            int insPos = getInsertIndex(treeView, target, place);
            int dragPos = target.getChildren().indexOf(dragItem);
            int targetSize = target.getChildren().size();

            if (target == place && target.getChildren().contains(dragItem)) {
                if (insPos == 0 && dragPos == 0) {
                    return false;
                }
                if (insPos == targetSize && dragPos == targetSize - 1) {
                    return false;
                }
            }
            if (dragItem == place && target != place && target.getChildren().contains(dragItem)) {
                if (insPos - dragPos == 1) {
                    return false;
                }
            }

//        } else if (target.getItemType() == CONTENT) {
        } else if (target.getValue() != null) {
            if (target != place) {
                return false;
            }
            Property prop = nd.getDefaultContentProperty();
            if (prop == null) {
                return false;
            }

            //
            // target.getValue() may be null for NodeContent
            //
            if (target.getValue() != null) {
                BeanAdapter ba = new BeanAdapter(target.getValue());

                Object o = ba.get(prop.getName());
                if (o != null && (prop instanceof NodeContent) && !((NodeContent) prop).isReplaceable()) {
                    return false;
                }
            }
        }
        //return true;
        return isAcceptable(target, dragObject);
    }

    protected boolean isAcceptable(TreeItemEx target, Object toAccept) {

        if (toAccept == null) {
            return false;
        }
        boolean retval = true;
        NodeDescriptor nd;
        if (target.getItemType() == CONTENT) {

            TreeItemEx parent = (TreeItemEx) target.getParent();
            Property cp = parent.getProperty(target.getPropertyName());//nc.getProperties().get(target.getInsertIndex());
            BeanAdapter adapter = new BeanAdapter(parent.getValue());
            retval = adapter.getType(cp.getName()).isAssignableFrom(toAccept.getClass());

            //} else if (target.getItemType() == LIST || NodeDescriptor.isList(target.getValue())) {
        } else if (target.getItemType() == LIST || target.getItemType() == DEFAULTLIST) {
            TreeItemEx listItem = getListTreeItemFor(target);

            String propName = null;
            if (listItem != null) {
                propName = getListPropertyNameFor(target);
            }
            if (listItem != null) {
                Class clazz = ReflectHelper.getListGenericType(listItem.getValue().getClass(), propName);
                if (clazz != null) {
                    retval = clazz.isAssignableFrom(toAccept.getClass());
                }
            }
        } else {
            //
            // The ItemType of the target TreeItem equals to NodeElement
            //
            nd = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
            Property prop = nd.getDefaultContentProperty();
            if (prop != null) {
                BeanAdapter ba = new BeanAdapter((target.getValue()));
                retval = ba.getType(prop.getName()).isAssignableFrom(toAccept.getClass());
            }
        }
        return retval;
    }

    protected TreeItemEx getListTreeItemFor(TreeItemEx item) {
        TreeItemEx retval = null;
        if (item.getItemType() == LIST) {
            retval = (TreeItemEx) item.getParent();
        } else if (item.getItemType() == DEFAULTLIST) {
            retval = item;
        }
        return retval;
    }

    protected String getListPropertyNameFor(TreeItemEx item) {
        String retval = null;
        if (item.getItemType() == LIST) {
            retval = item.getPropertyName();
        } else if (item.getItemType() == DEFAULTLIST) {
//            retval = NodeDescriptor.gatListPropertyName(item.getValue());
            //retval = NodeDescriptor.gatListPropertyName(item.getValue());
            NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(item.getValue());
            
            Property p = nd.getDefaultListProperty();
            if ( p != null ) {
                retval = p.getName();
            }
        }
        return retval;
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        if (parent == null) {
            //
            // child is a root TreeItem
            //
            return;
        }
        if (parent.getItemType() == LIST) {
            ((ObservableList) parent.getValue()).remove(child.getValue());
        } else if (parent.getItemType() == DEFAULTLIST) {
            NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(parent.getValue());
            BeanAdapter ba = new BeanAdapter(parent.getValue());
            //((ObservableList) ba.get(nd.getProperties().get(0).getName())).remove(child.getValue());
            ((ObservableList) ba.get(nd.getDefaultListProperty().getName())).remove(child.getValue());            

        } else {
            BeanAdapter ba = new BeanAdapter(parent.getValue());
            ba.put(child.getPropertyName(), null);
        }
    }

    public void accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        //TreeItem retval = null;
/*        System.err.println("ACCEPTL target = " + target);
        System.err.println("ACCEPTL place = " + place);
        System.err.println("ACCEPTL gestureSource = " + gestureSource);
         */
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        Object value = dg.getGestureSourceObject();

        //
        // A position where a new TreeItem should be inserted before uptateOnMove 
        // method call. We must consider for the list that the insertion 
        // position can change, since the method updateOnMove deletes the item which
        // corresponds to the dragged value
        //
        int insertIndex = getInsertIndex(treeView, target, place);

        //if (target != null && (target.getItemType() == LIST || NodeDescriptor.isList(target.getValue()))) {
        if (target != null && (target.getItemType() == LIST || target.getItemType() == DEFAULTLIST)) {
            TreeItemEx it = EditorUtil.findTreeItemByObject(treeView, value);
            if (it != null) {
                int idx = target.getChildren().indexOf(it);
                if (idx >= 0 && idx < insertIndex) {
                    insertIndex--;
                }
            }
        }
        if (target != null && place != null && value != null) {

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItemEx treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    updateOnMove(treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, value);
                if (sourceTreeItem != null) {
                    updateOnMove(sourceTreeItem);
                }
            }
        }
        update(treeView, target, insertIndex, value);

    }

    protected void update(TreeViewEx treeView, TreeItemEx target, int insertIndex, Object sourceObject) {

        /*        System.err.println("update: target = " + target);
        System.err.println("update: place = " + place);
        System.err.println("update: place.getParent = " + place.getParent());
        
        System.err.println("update:  place.getPropertyName = " + place.getPropertyName());
        System.err.println("-----------------------------------------");
         */
        switch (target.getItemType()) {
            case LIST:
                ((ObservableList) target.getValue()).add(insertIndex, sourceObject);
                break;
            case DEFAULTLIST:
                updateList(treeView, target, insertIndex, sourceObject);
                break;
            default:
//                if (!updateList(treeView, target, insertIndex, sourceObject)) {
                NodeDescriptor nd;
                if (target.getValue() == null) {
                    BeanAdapter ba = new BeanAdapter(target.getParent().getValue());
                    ba.put(target.getPropertyName(), sourceObject);
                } else {
                    nd = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
                    BeanAdapter ba = new BeanAdapter(target.getValue());
                    ba.put(nd.getDefaultContentProperty().getName(), sourceObject);
                }
                //              }
                break;

        }
    }

    protected void updateList(TreeViewEx treeView, TreeItemEx target, int placeIndex, Object sourceObject) {
        //boolean retval = false;

        //if (target.getValue() != null && target.getItemType() != ItemType.LIST) {
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(target.getValue());
        //Property p = nd.getProperties().get(0);
        //if (nd.getProperties().size() == 1 && (p instanceof NodeList) && !((NodeList) p).isAlwaysVisible()) {
        BeanAdapter ba = new BeanAdapter(target.getValue());
        ObservableList ol = (ObservableList) ba.get(nd.getProperties().get(0).getName());
        ol.add(placeIndex, sourceObject);
        //retval = true;
        //}
        //}

        //return retval;
    }

    /**
     * Tries to calculate an index in the children collection of the item
     * specified by the parameter {@code target } where a new item can be
     * inserted.
     *
     * @param treeView the node to search in
     * @param target the target TreeItem where the new TreeItem should be place
     * as a children
     * @param place the object of type TreeItem which represents a drag target
     * TreeCell
     *
     * @return an index in the collection of children in the target TreeItem
     * used to insert a new TreeItem
     */
    protected int getInsertIndex(TreeViewEx treeView, TreeItemEx target, TreeItemEx place) {
        int idx = -1;

        if (target == place) {
            int q = place.getDragDropQualifier();

            if (q == FIRST) {
                idx = 0;
            } else {
                idx = target.getChildren().size();
            }
        } else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItemEx parent = place;
            if (targetLevel - placeLevel != 1) {
                //
                // Occurs when place is the last TreeItem of it's parent
                //
                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {
                    parent = (TreeItemEx) parent.getParent();
                }
            }
            idx = target.getChildren().indexOf(parent) + 1;
        }
        return idx;
    }

}