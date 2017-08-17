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
package org.vns.javafx.dock.api.view;

import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableController;
import org.vns.javafx.dock.api.JFXDragManager;

/**
 *
 * @author Valery
 */
public class FloatViewFactory {

    private FloatViewFactory() {

    }
    public static FloatViewFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }
    
    public FloatView getFloatView(Dockable dockable) {
        FloatView v = null;
        DockableController ctrl = dockable.dockableController();
        if ( ctrl.getDragManager() instanceof JFXDragManager) {
            v = new FloatPopupControlView(dockable);
        } else {
            v = new FloatStageView(dockable);
//            v = new FloatPopupControlView(dockable);

        }
        return v;
    }
    private static class SingletonInstance {
        private static final FloatViewFactory INSTANCE = new FloatViewFactory();
    }
}
