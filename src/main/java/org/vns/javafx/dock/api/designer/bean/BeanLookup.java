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
package org.vns.javafx.dock.api.designer.bean1;

import java.util.List;
import org.vns.javafx.dock.api.ApplicationContext;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.DefaultContextLookup;

/**
 *
 * @author Valery
 */
public class BeanLookup { // implements ContextLookup {
    
    private final ContextLookup lookup;
    
    protected BeanLookup() {
        lookup = new DefaultContextLookup();
        init();
    }
    private void init() {
    }
    private static BeanLookup getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public static <T> T lookup(Class<T> clazz) {
        return getInstance().lookup.lookup(clazz);
    }

    
    public static <T> List<? extends T> lookupAll(Class<T> clazz) {
        return getInstance().lookup.lookupAll(clazz);
    }

    
    public static <T> void add(T obj) {
        getInstance().lookup.add(obj);
    }

  
    public static <T> void remove(T obj) {
        getInstance().lookup.remove(obj);
    }

    
    public static <T> void putUnique(Class key, T obj) {
        getInstance().lookup.putUnique(key, obj);
    }

    
    public static <T> void remove(Class key, T obj) {
        getInstance().lookup.remove(key, obj);
    }
    private static class SingletonInstance {

        private static final BeanLookup INSTANCE = new BeanLookup();
    }
    
    public static class DesignerApplicationContext implements ApplicationContext {

        @Override
        public boolean isDesignerContext() {
            return true;
        }
        
    }
}
