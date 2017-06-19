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
package org.vns.javafx.dock.api;

import java.util.Map;
import javafx.collections.FXCollections;

/**
 *
 * @author Valery
 */
public class PreferencesItem {
    private Object itemObject;
    private Map<String,String> properties = FXCollections.observableHashMap();

    public PreferencesItem(Object itemObject) {
        this.itemObject = itemObject;
    }

    public Object getItemObject() {
        return itemObject;
    }

    public void setItemObject(Object itemObject) {
        this.itemObject = itemObject;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    
    @Override
    public String toString() {
        String retval = "entryName/" + itemObject.getClass().getSimpleName();
        return retval;
    }
}
