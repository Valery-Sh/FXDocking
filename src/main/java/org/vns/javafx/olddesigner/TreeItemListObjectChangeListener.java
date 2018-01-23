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
package org.vns.javafx.olddesigner;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public class TreeItemListObjectChangeListener implements ListChangeListener {

    private final TreeItemEx treeItem;

    public TreeItemListObjectChangeListener(TreeItemEx treeItem) {
        this.treeItem = treeItem;
    }

    @Override
    public void onChanged(Change change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List list = change.getRemoved();
                if (!list.isEmpty()) {
                }
                for (Object elem : list) {
                    TreeItemEx toRemove = null;
                    for (TreeItem<ItemValue> it : treeItem.getChildren()) {
                        if (it.getValue().getTreeItemObject() == elem) {
                            toRemove = (TreeItemEx) it;
                            break;
                        }
                    }
                    treeItem.getChildren().remove(toRemove);
                }

            }
            if (change.wasAdded()) {
                List list = change.getAddedSubList();
                List itemList = new ArrayList();
                if (!list.isEmpty()) {
                }
                for (Object elem : list) {
                    TreeItem it = new TreeItemBuilder().build(elem);
                    itemList.add(it);
                }
                treeItem.getChildren().addAll(change.getFrom(), itemList);
            }
        }//while
    }
}