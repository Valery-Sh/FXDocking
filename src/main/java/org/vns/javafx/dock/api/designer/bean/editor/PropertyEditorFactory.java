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
package org.vns.javafx.dock.api.designer.bean.editor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public abstract class PropertyEditorFactory {

    public static PropertyEditorFactory getDefault() {
        return DefaultEditorFactory.getInstance();
    }

    /**
     *
     * @param propertyType the type of the property
     * @param bean the bean the property belongs to
     * @param propertyName the name of the property 
     * @return the new property editor instance for the specified parameters
     */
    public abstract PropertyEditor getEditor(Class<?> propertyType, Object bean, String propertyName);// {
//        return null;
//    }

    /**
     *
     * @param propertyType the type of the property
     * @param propertyName the name of the property 
     * @return the object of type {@code PropertyEditor }
     */
    public abstract PropertyEditor getEditor(Class<?> propertyType, String propertyName);// {

    public abstract PropertyEditor getEditor(String propertyType, String propertyName);// {    
//        return null;
//    }  

    public static class DefaultEditorFactory extends PropertyEditorFactory {

        @Override
        public PropertyEditor getEditor(Class<?> propertyType, Object bean, String propertyName) {
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor retval = null;
            for (PropertyEditorFactory f : list) {
                retval = f.getEditor(propertyType, bean, propertyName);
                if (retval != null) {
                    break;
                }
            }
            if (retval != null) {
                return retval;
            }

            if (propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)) {
                return new BooleanPropertyEditor();
            } else if (propertyType.equals(Character.class) || propertyType.equals(char.class)) {
                return new Character2PropertyEditor();
            } else if (propertyType.equals(Byte.class) || propertyType.equals(byte.class)) {
                return new Byte2PropertyEditor();
            } else if (propertyType.equals(Short.class) || propertyType.equals(short.class)) {
                return new Short2PropertyEditor();
            } else if (propertyType.equals(Integer.class) || propertyType.equals(int.class)) {
                return new Integer2PropertyEditor();
            } else if (propertyType.equals(Long.class) || propertyType.equals(long.class)) {
                return new Long2PropertyEditor();
            } else if (propertyType.equals(Float.class) || propertyType.equals(float.class)) {
                return new FloatPropertyEditor();
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class) || propertyType.equals(double.class)))) {
                return new SliderPropertyEditor(0, 1, 1);
            } else if (propertyType.equals(Double.class) || propertyType.equals(double.class)) {
                return new Double2PropertyEditor();
            } else if (propertyType.equals(String.class)) {
                return new SimpleStringPropertyEditor();
            } else if (propertyType.isEnum()) {
                return new EnumPropertyEditor(propertyType);
            } else if (propertyType.equals(Insets.class)) {
                return new InsetsPropertyEditor();
            } else if (propertyType.equals(Bounds.class)) {
                return new BoundsPropertyEditor();
            }
            return retval;
        }

        @Override
        public PropertyEditor getEditor(Class<?> propertyType, String propertyName) {
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor retval = null;
            for (PropertyEditorFactory f : list) {
                retval = f.getEditor(propertyType, propertyName);
                if (retval != null) {
                    break;
                }
            }
            if (retval != null) {
                return retval;
            }

            if (propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)) {
                return new BooleanPropertyEditor();
            } else if (propertyType.equals(Character.class) || propertyType.equals(char.class)) {
                return new Character2PropertyEditor();
            } else if (propertyType.equals(Byte.class) || propertyType.equals(byte.class)) {
                return new Byte2PropertyEditor();
            } else if (propertyType.equals(Short.class) || propertyType.equals(short.class)) {
                return new Short2PropertyEditor();
            } else if (propertyType.equals(Integer.class) || propertyType.equals(int.class)) {
                return new Integer2PropertyEditor();
            } else if (propertyType.equals(Long.class) || propertyType.equals(long.class)) {
                return new Long2PropertyEditor();
            } else if (propertyType.equals(Float.class) || propertyType.equals(float.class)) {
                return new FloatPropertyEditor();
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class) || propertyType.equals(double.class)))) {
                return new SliderPropertyEditor(0, 1, 1);
            } else if (propertyType.equals(Double.class) || propertyType.equals(double.class)) {
                return new Double2PropertyEditor();
            } else if (propertyType.equals(String.class)) {
                return new SimpleStringPropertyEditor();
            } else if (propertyType.isEnum()) {
                return new EnumPropertyEditor(propertyType);
            } else if (propertyType.equals(Insets.class)) {
                return new InsetsPropertyEditor();
            } else if (propertyType.equals(Bounds.class)) {
                return new BoundsPropertyEditor();
            }
            return retval;
        }

        public static final PropertyEditorFactory getInstance() {
            return SingletonInstance.INSTANCE;
        }

        @Override
        public PropertyEditor getEditor(String propertyType, String propertyName) {
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor editor = null;
            for (PropertyEditorFactory f : list) {
                editor = f.getEditor(propertyType, propertyName);
                if (editor != null) {
                    break;
                }
            }
            if (editor != null) {
                return editor;
            }

            if (propertyType.equals(Boolean.class.getName()) || propertyType.equals(boolean.class.getName())) {
                return new BooleanPropertyEditor();
            } else if (propertyType.equals(Character.class.getName()) || propertyType.equals(char.class.getName())) {
                return new Character2PropertyEditor();
            } else if (propertyType.equals(Byte.class.getName()) || propertyType.equals(byte.class.getName())) {
                return new Byte2PropertyEditor();
            } else if (propertyType.equals(Short.class.getName()) || propertyType.equals(short.class.getName())) {
                return new Short2PropertyEditor();
            } else if (propertyType.equals(Integer.class.getName()) || propertyType.equals(int.class.getName())) {
                return new Integer2PropertyEditor();
            } else if (propertyType.equals(Long.class.getName()) || propertyType.equals(long.class.getName())) {
                return new Long2PropertyEditor();
            } else if (propertyType.equals(Float.class.getName()) || propertyType.equals(float.class.getName())) {
                return new FloatPropertyEditor();
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class.getName()) || propertyType.equals(double.class.getName())))) {
                return new SliderPropertyEditor(0, 1, 1);
            } else if (propertyType.equals(Double.class.getName()) || propertyType.equals(double.class.getName())) {
                return new Double2PropertyEditor();
            } else if (propertyType.equals(String.class.getName())) {
                return new SimpleStringPropertyEditor();
            } else if (propertyType.equals(Insets.class.getName())) {
                return new InsetsPropertyEditor();
            } else if (propertyType.equals(Bounds.class.getName())) {
                return new BoundsPropertyEditor();
            } else {
                try {
                    Class clazz = Class.forName(propertyType);
                    if (clazz.isEnum()) {
                        return new EnumPropertyEditor(clazz);
                    }
                } catch (ClassNotFoundException ex) {
                    //Logger.getLogger(PropertyEditorFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return editor;
        }
        

        

        private static class SingletonInstance {

            private static final PropertyEditorFactory INSTANCE = new DefaultEditorFactory();
        }
    }
}