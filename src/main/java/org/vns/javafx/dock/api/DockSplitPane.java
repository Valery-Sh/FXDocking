package org.vns.javafx.dock.api;

import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class DockSplitPane extends SplitPane implements ListChangeListener {

    private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
    private DockSplitPane root;

    public DockSplitPane() {
        init();
    }

    public DockSplitPane(Node... items) {
        super(items);
        init();
    }

    public DockSplitPane getRoot() {
        return root;
    }

    public void setRoot(DockSplitPane root) {
        this.root = root;
    }

    private void init() {
        System.err.println(" INIT 1111111111111 id=" + this.getId());
        DockPaneTarget dpt = DockUtil.getParentDockPane(this);
        if (dpt != null && getItems().size() > 0) {
            getItems().forEach(it -> {
                if (DockRegistry.isDockable(it)) {
                    DockRegistry.dockable(it).nodeHandler().setPaneHandler(dpt.paneHandler());
                }
            });
        }
        System.err.println(" INIT 1111111111111 dpt=" + dpt);
        if (dpt != null) {
            update();
        }
        getItems().addListener(this);
        dividerPosProperty.addListener(this::dividerPosChanged);
    }

    protected void dividerPosChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        //DockSplitPane dsp = getParentSplitPane(this);
        if (getRoot() != null) {
            update();
        }
    }

    @Override
    public void onChanged(ListChangeListener.Change change) {
        itemsChanged(change);
    }

    protected void itemsChanged(ListChangeListener.Change<? extends Node> change) {
        DockPaneTarget dpt = null;
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Node> list = change.getRemoved();
                if (!list.isEmpty() && dpt == null) {
                    dpt = DockUtil.getParentDockPane(list.get(0));
                }
                for (Node node : list) {
                    if (dpt != null && DockRegistry.isDockable(node)) {
                    } else if (dpt != null && node instanceof DockSplitPane) {
                        splitPaneRemoved((SplitPane) node, dpt);
                    }
                }

            }
            if (change.wasAdded()) {
                System.err.println("=================== wasAdded:  ");
                List<? extends Node> list = change.getAddedSubList();
                if (!list.isEmpty() && dpt == null) {
                    dpt = DockUtil.getParentDockPane(list.get(0));
                }
                for (Node node : list) {
                    System.err.println("   --- added:  node=" + node.getId());
                    System.err.println("   --- added:  split=" + this.getId());

                    if (dpt != null && DockRegistry.isDockable(node)) {
                        DockRegistry.dockable(node).nodeHandler().setPaneHandler(dpt.paneHandler());
                    } else if (dpt != null && node instanceof DockSplitPane) {
                        splitPaneAdded((SplitPane) node, dpt);
                    } else if (node instanceof DockSplitPane) {
                        ((DockSplitPane) node).setRoot(getRoot());
                    }
                    if (this.getRoot() == null) {
                        System.err.println("NNNNNNNNNNNN ULL");
                    }

                }
            }
        }//while
        update();
    }

    protected void update(DockSplitPane split, PaneHandler ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeHandler().setPaneHandler(ph);
                if (i < split.getDividers().size() && d.nodeHandler().getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(d.nodeHandler().getDividerPos());
                }
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(getRoot());
                DockSplitPane sp = (DockSplitPane) node;
                if (i < split.getDividers().size() && sp.getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(sp.getDividerPos());
                }
                update(sp, ph);
            }
        }
    }

    public void update() {
        if (getRoot() != null) {
            DockPaneTarget dpt = (DockPaneTarget) getRoot().getParent();
            update(getRoot(), dpt.paneHandler());
        }
    }

    /**
     * Does nothing. Subclasses can change behavior.
     *
     * @param sp
     * @param dpt
     * @param split
     */
    /*        public void updateDividers() {
            if (getRoot() != null) {
                updateDividers(root);
            }
        }

        protected void updateDividers(DockSplitPane split) {
            for (int i = 0; i < split.getItems().size(); i++) {
                Node node = split.getItems().get(i);
                if (DockRegistry.isDockable(node)) {
                    //System.err.println("      --- is node id=" + node.getId());
                    Dockable d = DockRegistry.dockable(node);
                    if (i < split.getDividers().size() && d.nodeHandler().getDividerPos() >= 0) {
                        split.getDividers().get(i).setPosition(d.nodeHandler().getDividerPos());
                    }
                }
                if (node instanceof DockSplitPane) {
                    DockSplitPane dsp = (DockSplitPane) node;
                    if (i < split.getDividers().size() && dsp.getDividerPos() >= 0) {
                        split.getDividers().get(i).setPosition(dsp.getDividerPos());
                    }
                    updateDividers((DockSplitPane) node);
                }
                //System.err.println("-----------------------------------------");
            }
        }
     */
    protected void splitPaneAdded(SplitPane sp, DockPaneTarget dpt) {
        sp.getItems().forEach((node) -> {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).nodeHandler().setPaneHandler(dpt.paneHandler());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        });
    }

    protected void splitPaneRemoved(SplitPane sp, DockPaneTarget dpt) {
        sp.getItems().forEach((node) -> {
            if (DockRegistry.isDockable(node)) {
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        });
    }

    public DoubleProperty dividerPosProperty() {
        return dividerPosProperty;
    }

    public double getDividerPos() {
        return dividerPosProperty.get();
    }

    public void setDividerPos(double dividerPos) {
        this.dividerPosProperty.set(dividerPos);
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public void setDividerPosition(Node node, Side dockPos) {
        setDividerPosition(node, this, dockPos);
    }

    public static void setDividerPosition(Node node, DockSplitPane split, Side dockPos) {
        if (split.getItems().size() <= 1) {
            return;
        }

        int idx = split.getItems().indexOf(node);
        Dockable d = DockRegistry.dockable(node);
        //System.err.println("1. SplitDelegate: divPos=" + d.nodeHandler().getDividerPos());
/*            if (d.nodeHandler().getDividerPos() >= 0) {
                System.err.println("2. SplitDelegate: divPos=" + d.nodeHandler().getDividerPos());
                split.setDividerPosition(idx, d.nodeHandler().getDividerPos());
                return;
            }
         */
        //d.nodeHandler().updateDividers(idx, split);
        double sizeSum = 0;
        for (int i = 0; i < split.getItems().size(); i++) {
            if (i == idx) {
                continue;
            }
            if (split.getOrientation() == Orientation.HORIZONTAL) {
                sizeSum += split.getItems().get(i).prefWidth(0);
            } else {
                sizeSum += split.getItems().get(i).prefHeight(0);
            }
        }
        if (dockPos == Side.TOP || dockPos == Side.LEFT) {
            if (split.getOrientation() == Orientation.HORIZONTAL) {
                split.setDividerPosition(idx,
                        node.prefWidth(0) / (sizeSum + node.prefWidth(0)));
            } else {
                split.setDividerPosition(idx,
                        node.prefHeight(0) / (sizeSum + node.prefHeight(0)));

            }
        } else {
            if (split.getOrientation() == Orientation.HORIZONTAL) {
                split.setDividerPosition(idx,
                        1 - node.prefWidth(0) / (sizeSum + node.prefWidth(0)));
            } else {
                split.setDividerPosition(idx,
                        1 - node.prefHeight(0) / (sizeSum + node.prefHeight(0)));
            }
        }
    }
}
