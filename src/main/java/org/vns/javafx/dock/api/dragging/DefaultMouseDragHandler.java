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
package org.vns.javafx.dock.api.dragging;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.dragging.view.FloatView;

/**
 *
 * @author Valery Shyshkin
 */
public class DefaultMouseDragHandler extends MouseDragHandler {

    public DefaultMouseDragHandler(DockableContext context) {
        super(context);
    }

    @Override
    public void mouseDragDetected(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        Dockable dockable = getContext().dockable();
        if (!getContext().isDraggable()) {
            ev.consume();
            return;
        }

        DragManager dm = getContext().getDragManager();

        if (!dockable.getDockableContext().isFloating()) {
            dm.mouseDragDetected(ev, getStartMousePos());
        } else {
            DragContainer dc = dockable.getDockableContext().getDragContainer();
Node n = dockable.node();
            if (dc != null && Dockable.of(dc.getGraphic()) != null ) {
                Dockable.of(dc.getGraphic()).getDockableContext().getDragManager().mouseDragDetected(ev, getStartMousePos());
            } else {
                dm.mouseDragDetected(ev, getStartMousePos());
            }
        }

    }
    
    public void mouseDragDetected1(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        Dockable dockable = getContext().dockable();
        if (!getContext().isDraggable()) {
            ev.consume();
            return;
        }

        DragManager dm = getContext().getDragManager();

        if (!dockable.getDockableContext().isFloating()) {
            dm.mouseDragDetected(ev, getStartMousePos());
        } else {
            Object value = dockable.getDockableContext().getDragContainer().getValue();
            if (value != null && Dockable.of(value) != null ) {
                Dockable.of(value).getDockableContext().getDragManager().mouseDragDetected(ev, getStartMousePos());
            } else {
                dm.mouseDragDetected(ev, getStartMousePos());
            }
        }

    }

}
