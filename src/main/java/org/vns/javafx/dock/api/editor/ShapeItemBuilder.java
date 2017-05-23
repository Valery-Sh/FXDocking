package org.vns.javafx.dock.api.editor;

import javafx.scene.control.TreeItem;
import javafx.scene.shape.Shape;

/**
 *
 * @author Valery
 */
public class ShapeItemBuilder extends TreeItemBuilder {

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof Shape) {
            retval = createItem((Shape) obj);
        }
        return retval;
    }
    @Override
    public boolean isDragTarget() {
        return false;
    }

}
