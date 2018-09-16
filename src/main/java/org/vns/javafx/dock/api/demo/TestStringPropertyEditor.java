/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.BooleanPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.StringPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestStringPropertyEditor extends Application {

    Stage stage;
    Scene scene;
    IntegerProperty value = new SimpleIntegerProperty();

    @Override
    public void start(Stage stage) {
        Button btn1 = new Button("setText(null)");
        Button btn2 = new Button("setText(Valery)");
        Button btn3 = new Button("setText(\"\")");
        Button btn4 = new Button("setLastValidText(Olga)");
        
        

        long start1 = System.currentTimeMillis();
        Pane p = new Pane();
        long end1 = System.currentTimeMillis();
        System.err.println("DIF0 = " + (end1 - start1));

        Text msg = new Text("JavaFX animation is cool!");
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));
        //Pane root = new Pane(msg);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        //AnchorPane anchor = new AnchorPane(grid);
        //anchor.setStyle("-fx-border-color: red; -fx-border-width: 4 ");
        //grid.setStyle("-fx-border-color: green; -fx-border-width: 2 ");

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");

        lb1.setFont(new Font(13));
        System.err.println("font size lb1.getFont().getSize()= " + lb1.getFont().getSize());
        //SliderEditor tf1 = new SliderPropertyEditor(0,1,1);
        //DecimalTextField tf1 = new DecimalTextField();
        ObjectProperty<Short> ip = new SimpleObjectProperty(0);
        StringProperty boundStr = new SimpleStringProperty(null);
        
        StringPropertyEditor tf1 = new StringPropertyEditor();
/*        tf1.getValidators().add( it -> {
            if ( "5".equals(it) ) {
                return false;
            }
            return true;
        });
*/        
        //tf1.setNullSubstitution("<NULL>");
        //tf1.setEmptySubstitution("<EMPTY>");
        //tf1.setNullable(true);
        System.err.println("1 settext(null) *****************************************************");        
        //btn2.styleProperty().set(null);
        //System.err.println("btn2.styleProperty() = " + btn2.styleProperty().get());
        //tf1.bindBidirectional(btn2.styleProperty());
        //tf1.bindBidirectional(boundStr);
        //tf1.setText("876");
        //tf1.setText(null);
        System.err.println("2 setText(null) ****************************************************");        
        
        
        tf1.setText(null);
        
        //boundStr.set(null);
        //boundStr.set(null);
        //CharacterTextField tf1 = new CharacterTextField();
        // System.err.println("ShortMax = " + Short.MAX_VALUE);
        /// NumberPropertyEditor tf1 = new NumberPropertyEditor();
        //tf1.setFont(new Font(13));
        //tf1.bindBidirectional(btn1.textProperty());
        //tf1.bindBidirectional(btn1.prefWidthProperty());
        //tf1.bindBidirectional(btn1.opacityProperty());
        //tf1.bindBidirectional(ip);
        //tf1.bind(ip);
   
        btn1.setOnAction(e -> {
            tf1.setText(null);
        });

        grid.add(lb1, 0, 0);
        grid.add(tf1, 1, 0);
        grid.add(btn1, 0, 1);
        grid.add(btn3, 0, 2);
        grid.add(btn4, 0, 3);
        
        btn2.setOnAction(e -> {
            System.err.println("#### setText(\"javaFX\")");
            tf1.setText("javaFX");
            //btn2.setPrefWidth(200.56);
        });
        btn3.setOnAction(e -> {
            System.err.println("#### setText('')");
            tf1.setText("");
            //btn2.setPrefWidth(200.56);
        });
        btn4.setOnAction(e -> {
            System.err.println("#### setLastValidText(\"java script\");");
            //tf1.setLastValidText("java script");
            tf1.setText("java script");
            //btn2.setPrefWidth(200.56);
        });

        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc20 = new ColumnConstraints();

        cc0.setPercentWidth(35);
        cc1.setPercentWidth(65);
        //cc20.setPercentWidth(100);

        //grid.getColumnConstraints().addAll(cc0,cc1, cc20);        
        grid.getColumnConstraints().addAll(cc0, cc1);
        //GridPane.setHalignment(tf1, HPos.RIGHT);
        //GridPane.setHalignment(tf1, HPos.LEFT);
        //GridPane.setFillWidth(tf1, true);
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Stage stage1 = new Stage();
        stage1.initOwner(stage);

        VBox vbox = new VBox(btn1, btn2);
        VBox propPane = new VBox();
        TilePane tilePane = new TilePane();
        propPane.setStyle("-fx-border-width: 2; -fx-border-color: green");
        vbox.getChildren().add(propPane);
        propPane.getChildren().add(tilePane);
        StackPane contentPane = new StackPane();
        propPane.getChildren().add(contentPane);
        contentPane.setStyle("-fx-border-width: 2; -fx-border-color: blue");
        Button propBtn = new Button("Properties");
        Button layoutBtn = new Button("Layout");
        Button codeBtn = new Button("Code");
        tilePane.getChildren().addAll(propBtn, layoutBtn, codeBtn);
        //
        // Properties Category
        //
        TitledPane propTitledPane1 = new TitledPane();
        propTitledPane1.setText("Node");

        TitledPane propTitledPane2 = new TitledPane();
        propTitledPane2.setText("JavaFx CSS");
        TitledPane propTitledPane3 = new TitledPane();
        propTitledPane3.setText("Extras");
        VBox propSecBox = new VBox(propTitledPane1, propTitledPane2, propTitledPane3);
        contentPane.getChildren().add(propSecBox);

        TitledPane layoutTitledPane1 = new TitledPane();
        layoutTitledPane1.setText("Content");
        TitledPane layoutTitledPane2 = new TitledPane();
        layoutTitledPane2.setText("Internals");
        VBox layoutSecBox = new VBox(layoutTitledPane1, layoutTitledPane2);
        contentPane.getChildren().add(layoutSecBox);
        layoutSecBox.setVisible(false);

        TitledPane codeTitledPane1 = new TitledPane();
        codeTitledPane1.setText("onAction");
        VBox codeSecBox = new VBox(codeTitledPane1);
        contentPane.getChildren().add(codeSecBox);
        codeSecBox.setVisible(false);

        propBtn.setDisable(true);

        propBtn.setOnAction(e -> {
            propBtn.setDisable(true);
            propSecBox.setVisible(true);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        layoutBtn.setOnAction(e -> {
            layoutBtn.setDisable(true);
            layoutSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        codeBtn.setOnAction(e -> {
            codeBtn.setDisable(true);
            codeSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
        });

        Scene scene1 = new Scene(vbox);
        stage1.setScene(scene1);

        stage1.show();

        VBox vbox2 = new VBox(btn2);
        PopupControl pc = new PopupControl();
        pc.getScene().setRoot(vbox2);
        pc.show(stage, 20, 2);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
