/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.designer;

import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public interface DragAndDropManager {

    DragAndDropManager enableDragAndDrop(Object gestureSourceObject, Node source, ChildrenRemover remover);

    default DragAndDropManager enableDragAndDrop(Object gestureSourceObject, Node source) {
        return enableDragAndDrop(gestureSourceObject, source, null);
    }

    default DragAndDropManager enableDragAndDrop(Node source) {
        return this.enableDragAndDrop(null, source, null);
    }

    default DragAndDropManager enableDragAndDrop(Node source, ChildrenRemover remover) {
        return this.enableDragAndDrop(null, source, remover);
    }

    @FunctionalInterface
    public interface ChildrenRemover {
        boolean remove();
    }

}
