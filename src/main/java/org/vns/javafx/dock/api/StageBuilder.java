package org.vns.javafx.dock.api;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.DockPane;

/**
 *
 * @author Valery
 */
public class StageBuilder extends FloatStageBuilder {

    public StageBuilder(DockNodeHandler nodeHandler) {
        super(nodeHandler);
    }

    @Override
    protected void makeFloating(Dockable dockable) {

        Node titleBar = dockable.nodeHandler().getTitleBar();
        
        titleBar.setVisible(true);
        titleBar.setManaged(true);
        
        double nodeHeight = dockable.node().getHeight();
        setDefaultCursors();
        
        dockable.nodeHandler().undock();
        BorderPane borderPane = (BorderPane) dockable.node().getScene().getRoot();
        
        borderPane.getStyleClass().add("dock-node-border");    
        dockable.node().setPrefHeight(nodeHeight + 50);
        
        addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
    }
/*    @Override
    protected void addListeners(Stage stage) {
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, getMouseResizeHanler());
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, getMouseResizeHanler());
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, getMouseResizeHanler());
    }
*/
    public Stage createStage(Dockable dockable) {

        Region node = dockable.node();
        Node titleBar = dockable.nodeHandler().getTitleBar();
        titleBar.setVisible(true);
        titleBar.setManaged(true);

        Stage newStage = new Stage();
        DockRegistry.register(newStage);
        stageProperty().set(newStage);

        newStage.setTitle("NEW STAGE");
        Pane lastDockPane = dockable.nodeHandler().getLastDockPane();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }
        newStage.initStyle(getStageStyle());

        setRootPane(new BorderPane());

        Pane dockPane = new DockPane();
        dockPane.getChildren().add(dockable.node()); // we do not apply dock() 

        ((BorderPane) getRootPane()).setCenter(dockPane);

        Scene scene = new Scene(getRootPane());

        node.applyCss();
        getRootPane().applyCss();

        newStage.setResizable(true);
        newStage.setScene(scene);
        if (getStageStyle() == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer(newStage, dockable);
        newStage.sizeToScene();

        return newStage;
    }

}//class StageBuilder
