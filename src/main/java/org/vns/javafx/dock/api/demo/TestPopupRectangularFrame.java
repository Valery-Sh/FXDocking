package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.api.dragging.view.DefaultRectangleFrameFactory;
import org.vns.javafx.dock.api.dragging.view.PopupRectangleFrame;

public class TestPopupRectangularFrame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("TilePane Experiment");

        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button Number 2");
        Button button3 = new Button("Button No 3");
        Button button4 = new Button("Button No 4");
        Button button5 = new Button("Button 5");
        Button button6 = new Button("Button Number 6");
        /*        button1.minWidthProperty().bind(button2.widthProperty());
        button3.minWidthProperty().bind(button2.widthProperty());
        button4.minWidthProperty().bind(button2.widthProperty());        
        button5.minWidthProperty().bind(button2.widthProperty());
        button6.minWidthProperty().bind(button2.widthProperty());
         */
        PopupRectangleFrame r = new PopupRectangleFrame();
        r.setDefaultStyles();
        /*        r.setStroke(Color.AQUA);
        r.setStrokeWidth(2);
        r.setStrokeType(StrokeType.OUTSIDE);
        r.setStrokeLineCap(StrokeLineCap.BUTT);
        r.setStrokeDashOffset(2);
         */
//        r.setStyle("-fx-fill: yellow; -fx-opacity: 0.6;-fx-stroke: red;-fx-stroke-type: outside; -fx-stroke-width: 10;-fx-stroke-dash-array: 2 1 -fx-stroke-line-cap: butt;");
        r.setOnMousePressed(e -> {
            System.err.println("Rect pressed");
        });
        button1.setOnAction(e -> {
            System.err.println("button2.getScaleX() = " + button2.getScaleX());
            if (button2.getScaleX() < 1) {
                button2.setScaleX(1);
            } else {
                button2.setScaleX(0.4);
            }

        });
        button2.setOnAction(e -> {
            r.bind(button2);
            r.show();
            /*            r.setWidth(button2.getBoundsInParent().getWidth());
            r.setHeight(button2.getBoundsInParent().getHeight());
            Bounds bnds = button2.localToScreen(button2.parentToLocal(button2.getBoundsInParent()));
            r.getPopup().setX(bnds.getMinX() - r.getStrokeWidth());
            r.getPopup().setY(bnds.getMinY() - r.getStrokeWidth());
             */
            //r.setX( r.getBoundsInParent().getMinX());
            //r.setY(r.getBoundsInParent().getMinY());
            //r.setX(50);
            //r.setY(50);

        });
        VBox root = new VBox(button1, button2);
        //StackPane root1 = new StackPane(r);

        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        Stage stage = new Stage(StageStyle.TRANSPARENT);

        //Scene scene1 = new Scene(root1);
        //stage.setScene(scene1);
        //root1.setStyle("-fx-background-color: transparent");
        //scene1.setFill(null);
        Scene scene = new Scene(root, 800, 100);
        scene.setFill(null);

        //System.err.println("scene = " + popup.getScene());
        primaryStage.setScene(scene);
        primaryStage.focusedProperty().addListener((v, ov, nv) -> {
            System.err.println(" primaryStage.ifFocused = " + nv);
            if (nv) {
                //Platform.runLater(() -> {r.show();});
                r.show();
            } else {
                r.hide();
            }

        });
        primaryStage.show();
        //popup.show(primaryStage);

        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
