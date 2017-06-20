package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.SideIndicator.PaneSideIndicator;

public class DockPaneController extends DockTargetController {

    //private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
    private DockDelegate dockDelegate;
    private DockExecutor dockExecutor;

    /*    private SideIndicatorTransformer.PaneIndicatorTransformer paneTransformer;
    private SideIndicatorTransformer.NodeIndicatorTransformer nodeTransformer;
     */
    private SideIndicator.NodeSideIndicator nodeIndicator;

    public DockPaneController(Region dockPane) {
        super(dockPane);
        init();
    }

    private void init() {
        dockDelegate = new DockDelegate((DockSplitPane) getTargetNode(), this);
    }

    public SideIndicator.NodeSideIndicator getNodeIndicator() {
        if (nodeIndicator == null) {
            nodeIndicator = createNodeIndicator();
        }
        return nodeIndicator;
    }

    @Override
    protected IndicatorPopup createIndicatorPopup() {
        return new DragPopup(this);
    }

    protected SideIndicator.NodeSideIndicator createNodeIndicator() {
        return new SideIndicator.NodeSideIndicator(this);
    }

    @Override
    protected PositionIndicator createPositionIndicator() {
        return new PaneSideIndicator(this);
    }

    //////////////////////
    protected DockExecutor getDockExecutor() {
        if (dockExecutor == null) {
            dockExecutor = new DockExecutor(this);
        }
        return dockExecutor;
    }

    protected DockDelegate getDockDelegate() {
        return dockDelegate;
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            retval = DockUtil.getParentSplitPane((DockSplitPane) getTargetNode(), node) != null;
        }
        return retval;
    }

    @Override
    protected void dock(Point2D mousePos, Dockable dockable) {
        //07.05getDockExecutor().dock(mousePos, dockable);
        getDockExecutor().dock(dockable);
    }

    /**
     * The method does nothing.
     *
     * @param mousePos the mouse pos
     * @param node the node to be docked
     * @return true if the method execution was successful
     */
    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //@Override
    public void dock(Dockable dockable, Object pos) {
        getDockExecutor().dock(dockable, pos);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        getDockExecutor().dock(dockNode, side, target);
    }

    @Override
    public void remove(Node dockNode) {
        DockSplitPane dsp = getParentSplitPane((DockSplitPane) getTargetNode(), dockNode);
        if (dsp != null) {
            DockTargetController ph = DockRegistry.dockable(dockNode).dockableController().getTargetController();
            dsp.getItems().remove(dockNode);
            DockRegistry.dockable(dockNode).dockableController().setTargetController(ph);
            clearEmptySplitPanes((DockSplitPane) getTargetNode(), dsp);
        }
    }

    @Override
    public PreferencesBuilder getPreferencesBuilder() {
        return new DockPanePreferencesBuilder();
    }

    public static class DockDelegate {

        private DockSplitPane root;
        private final DockPaneController paneController;

        public DockDelegate(DockSplitPane root, DockPaneController paneController) {
            this.root = root;
            this.paneController = paneController;
        }

        public DockSplitPane getRoot() {
            return root;
        }

        public void dock(Dockable dockable, Side dockPos) {
            dock(dockable.node(), dockPos);
        }

        private void dock(Node node, Side dockPos) {

            DockSplitPane rootSplitPane = root;

            if (rootSplitPane == null) {
                rootSplitPane = new DockSplitPane();
                root = rootSplitPane;
                rootSplitPane.getItems().add(node);
                return;
            }
            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = root.getOrientation();

            if (newOrientation != oldOrientation) {
                DockSplitPane dp = null;
                if (newOrientation == Orientation.HORIZONTAL) {
                    dp = new HPane();
                } else {
                    dp = new VPane();
                }
                dp.getItems().addAll(root.getItems());

                root.getItems().clear();
                int idx = 0;
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    idx = dp.getItems().size();
                }
                dp.getItems().add(idx, node);
                root.getItems().add(dp);
            } else {
                int idx = 0;
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    idx = root.getItems().size();
                }
                root.getItems().add(idx, node);
            }
        }

        public void dock(Node node, Side dockPos, Dockable target) {
            if (target == null) {
                dock(node, dockPos);
                return;  //added 26.01
            }

            Node targetNode = target.node();

            DockSplitPane parentSplitPane = getTargetSplitPane(targetNode);
            //DockSplitPane targetSplitPane = parentSplitPane;

            if (parentSplitPane == null) {
                return;
            }

            Dockable d = DockRegistry.dockable(node);

            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = parentSplitPane.getOrientation();

            if (newOrientation != oldOrientation) {
                DockSplitPane dp = null;
                if (newOrientation == Orientation.HORIZONTAL) {
                    dp = new HPane();
                } else {
                    dp = new VPane();
                }

                int idx = parentSplitPane.getItems().indexOf((Node) targetNode);

                parentSplitPane.getItems().remove((Node) targetNode);
                if (dockPos == Side.TOP || dockPos == Side.LEFT) {
                    dp.getItems().add(node);
                    dp.getItems().add((Node) targetNode);
                } else {
                    dp.getItems().add((Node) targetNode);
                    dp.getItems().add(node);
                }
                parentSplitPane.getItems().add(idx, dp);
                //07.05targetSplitPane = p;
            } else {
                int idx = parentSplitPane.getItems().indexOf(targetNode);
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    ++idx;
                }
                parentSplitPane.getItems().add(idx, node);
            }
        }

        protected DockSplitPane getTargetSplitPane(Node target) {
            DockSplitPane retval = null;
            DockSplitPane split = root;
            Stack<DockSplitPane> stack = new Stack<>();
            stack.push(split);

            while (!stack.empty()) {
                split = stack.pop();
                if (split.getItems().contains(target)) {
                    retval = split;
                    break;
                }
                for (Node n : split.getItems()) {
                    if (n instanceof DockSplitPane) {
                        stack.push((DockSplitPane) n);
                    }
                }
            }
            return retval;

        }

    }//class DockDelegate

    public static class DockExecutor {

        private DockPaneController paneController;

        public DockExecutor(DockPaneController paneController) {
            this.paneController = paneController;
        }

        protected Dockable dock(Node node, Side nodeDockPos, Side paneDockPos, Node target) {
            Dockable retval = null;
            if (paneDockPos != null) {
                dock(DockRegistry.dockable(node), paneDockPos);
            } else if (nodeDockPos != null) {
                Dockable t = target == null ? null : DockRegistry.dockable(target);
                dock(DockRegistry.dockable(node), nodeDockPos, t);
            }
            return retval;
        }

        protected void dock(Dockable dockable) {
            IndicatorPopup popup = paneController.getIndicatorPopup();
            Node node = dockable.node();
            if (!(popup instanceof DragPopup)) {
                return;
            }
            DragPopup dp = (DragPopup) popup;
            Dockable d = DockRegistry.dockable(node);
            if (d.dockableController().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
                //Dockable retval = null;
                if (dp.getTargetPaneSidePos() != null) {
                    dock(DockRegistry.dockable(node), dp.getTargetPaneSidePos());
                } else if (dp.getTargetNodeSidePos() != null) {
                    Dockable t = dp.getDragTarget() == null ? null : DockRegistry.dockable(dp.getDragTarget());
                    dock(DockRegistry.dockable(node), dp.getTargetNodeSidePos(), t);
                }
            }

        }

        public void dock(Dockable dockable, Object pos) {
            if (pos instanceof Side) {
                dock(dockable, (Side) pos);
            }
        }

        protected Dockable dock(Dockable dockable, Side dockPos) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }

            if (doDock(dockable.node(), dockPos)) {
                dockable.dockableController().setFloating(false);
            }
            return dockable;
        }

        protected Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }
            if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
                DockRegistry.getDockables().put(dockable.node(), dockable);
            }
            dockable.dockableController().setFloating(false);

            doDock(dockable.node(), dockPos, target);

            return dockable;
        }

        protected boolean doDock(Node node, Side dockPos) {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            paneController.getDockDelegate().dock(DockRegistry.dockable(node), dockPos);

            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
            }

            return true;
        }

        protected boolean doDock(Node node, Side dockPos, Dockable targetDockable) {
            if (paneController.getDockDelegate() == null) {
                return false;
            }
            if (paneController.isDocked(node)) {
                return false;
            }
            if (targetDockable == null) {
                dock(DockRegistry.dockable(node), dockPos);
            } else {
                if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                    ((Stage) node.getScene().getWindow()).close();
                }
                if (DockRegistry.isDockable(node)) {
                    DockRegistry.dockable(node).dockableController().setFloating(false);
                }
                paneController.getDockDelegate().dock(node, dockPos, targetDockable);
            }
            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
            }
            return true;
        }

    }//DockExcecutor

    public static class DockExecutor_OLD {

        private DockPaneController paneController;

        public DockExecutor_OLD(DockPaneController paneController) {
            this.paneController = paneController;
        }

        protected Dockable dock(Point2D mousePos, Node node, Side nodeDockPos, Side paneDockPos, Node target) {
            Dockable retval = null;
            if (paneDockPos != null) {
                dock(mousePos, DockRegistry.dockable(node), paneDockPos);
            } else if (nodeDockPos != null) {
                Dockable t = target == null ? null : DockRegistry.dockable(target);
                dock(mousePos, DockRegistry.dockable(node), nodeDockPos, t);
            }
            return retval;
        }

        protected void dock(Point2D mousePos, Dockable dockable) {
            IndicatorPopup popup = paneController.getIndicatorPopup();
            Node node = dockable.node();
            if (!(popup instanceof DragPopup)) {
                return;
            }
            DragPopup dp = (DragPopup) popup;
            Dockable d = DockRegistry.dockable(node);
            if (d.dockableController().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
                //Dockable retval = null;
                if (dp.getTargetPaneSidePos() != null) {
                    dock(mousePos, DockRegistry.dockable(node), dp.getTargetPaneSidePos());
                } else if (dp.getTargetNodeSidePos() != null) {
                    Dockable t = dp.getDragTarget() == null ? null : DockRegistry.dockable(dp.getDragTarget());
                    dock(mousePos, DockRegistry.dockable(node), dp.getTargetNodeSidePos(), t);
                }

                //dock(mousePos, node, p.getTargetNodeSidePos(), p.getTargetPaneSidePos(), p.getDragTarget());
            }

        }

        public void dock(Dockable dockable, Object pos) {
            if (pos instanceof Side) {
                dock(null, dockable, (Side) pos);
            }
        }

        protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }

            if (doDock(mousePos, dockable.node(), dockPos)) {
                dockable.dockableController().setFloating(false);
            }
            return dockable;
        }

        public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
            return dock(null, dockable, dockPos, target);
        }

        protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos, Dockable target) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }
            if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
                DockRegistry.getDockables().put(dockable.node(), dockable);
            }
            dockable.dockableController().setFloating(false);

            doDock(mousePos, dockable.node(), dockPos, target);
            //09.02changeDockedState(dockable, true);
            return dockable;
        }

        protected boolean doDock(Point2D mousePos, Node node, Side dockPos) {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            paneController.getDockDelegate().dock(DockRegistry.dockable(node), dockPos);

            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
                //nodeController.setTargetController(paneController); //06.05.2017
            }

            return true;
        }

        protected boolean doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
            if (paneController.getDockDelegate() == null) {
                return false;
            }
            if (paneController.isDocked(node)) {
                return false;
            }
            if (targetDockable == null) {
                dock(DockRegistry.dockable(node), dockPos);
            } else {
                if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                    ((Stage) node.getScene().getWindow()).close();
                }
                if (DockRegistry.isDockable(node)) {
                    DockRegistry.dockable(node).dockableController().setFloating(false);
                }
                paneController.getDockDelegate().dock(node, dockPos, targetDockable);
            }
            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
            }
            return true;
        }

    }//DockExcecutor OLD

    public class DockPanePreferencesBuilder implements PreferencesBuilder {

        public static final String DIVIDER_POSITIONS = "dividerPositions";
        public static final String ORIENTATION = "orientation";

        @Override
        public TreeItem<PreferencesItem> build(DockTarget dockTarget) {
            TreeItem<PreferencesItem> retval = new TreeItem<>();
            DockPane pane = (DockPane) dockTarget;
            PreferencesItem it = new PreferencesItem(pane);
            retval.setValue(it);
            setProperties(it);
            for (int i = 0; i < pane.getItems().size(); i++) {
                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    it = new PreferencesItem(pane.getItems().get(i));
                    TreeItem ti = new TreeItem();
                    ti.setValue(it);
                    retval.getChildren().add(ti);
                    ti.setExpanded(true);
                    setProperties(it);
                    buildPane((SplitPane) pane.getItems().get(i), retval, ti);
                } else if (DockRegistry.isDockable(pane.getItems().get(i))) {
                    it = new PreferencesItem(pane.getItems().get(i));
                    TreeItem ti = new TreeItem();
                    ti.setValue(it);
                    retval.getChildren().add(ti);
                    ti.setExpanded(true);
                }
            }
            return retval;
        }

        protected void buildPane(SplitPane pane, TreeItem<PreferencesItem> root, TreeItem<PreferencesItem> parent) {
            for (int i = 0; i < pane.getItems().size(); i++) {
                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    PreferencesItem it = new PreferencesItem(pane.getItems().get(i));
                    TreeItem ti = new TreeItem();
                    ti.setValue(it);
                    parent.getChildren().add(ti);
                    ti.setExpanded(true);
                    setProperties(it);
                    buildPane((SplitPane) pane.getItems().get(i), root, ti);
                } else if (DockRegistry.isDockable(pane.getItems().get(i))) {
                    PreferencesItem it = new PreferencesItem(pane.getItems().get(i));
                    TreeItem ti = new TreeItem();
                    ti.setValue(it);
                    parent.getChildren().add(ti);
                    ti.setExpanded(true);
                }
            }
        }

        private void setProperties(PreferencesItem it) {
            SplitPane sp = (SplitPane) it.getItemObject();
            String[] strDp = new String[sp.getDividerPositions().length];

            it.getProperties().put(ORIENTATION, sp.getOrientation().toString());
            for (int i = 0; i < sp.getDividerPositions().length; i++) {
                strDp[i] = String.valueOf(sp.getDividerPositions()[i]);
            }
            if (strDp.length > 0) {
                it.getProperties().put(DIVIDER_POSITIONS, String.join(",", strDp));
            }
        }

        @Override
        public Map<String, String> getProperties(Object node) {
            Map<String, String> props = FXCollections.observableHashMap();
            if (node instanceof SplitPane) {
                SplitPane sp = (SplitPane) node;
                props.put(ORIENTATION, sp.getOrientation().toString());
                if (sp.getDividerPositions().length != 0) {
                    String[] s = new String[sp.getDividerPositions().length];
                    Arrays.setAll(s, (idx) -> {
                        return String.valueOf(sp.getDividerPositions()[idx]);
                    });
                    String dp = String.join(",", s);
                    props.put(DIVIDER_POSITIONS, dp);
                }
            }
            return props;
        }

        @Override
        public void setProperties(Object node, Map<String, String> prefProps) {
            if (node instanceof SplitPane) {
                SplitPane sp = (SplitPane) node;
                if (prefProps.get(ORIENTATION) != null) {
                    if (prefProps.get(ORIENTATION).equals("VERTICAL")) {
                        sp.setOrientation(Orientation.VERTICAL);
                    } else {
                        sp.setOrientation(Orientation.HORIZONTAL);
                    }
                }
                if (prefProps.get(DIVIDER_POSITIONS) != null) {
                    String[] s = prefProps.get(DIVIDER_POSITIONS).split(",");
                    if (s.length > 0) {
                        double[] d = new double[s.length];
                        Arrays.setAll(d, (idx) -> {
                            return Double.parseDouble(s[idx]);
                        });
                        sp.setDividerPositions(d);
                    }
                }
            }

        }

        @Override
        public void restoreFrom(TreeItem<PreferencesItem> targetRoot) {
            PreferencesItem pit = targetRoot.getValue();
            if (!(pit.getItemObject() instanceof DockPane)) {
                return;
            }
            DockPane pane = (DockPane) pit.getItemObject();
            addDividersListener(pane, pane);
            pane.getItems().clear();

            String dp = pit.getProperties().get(DIVIDER_POSITIONS);
            if (dp != null && !dp.trim().isEmpty()) {
                String[] dps = dp.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                pane.setDividerPositions(dpd);
            }
            for (TreeItem<PreferencesItem> item : targetRoot.getChildren()) {
                pane.getItems().add(buildFrom(item));
            }
        }

        protected Node buildFrom(TreeItem<PreferencesItem> targetRoot) {
            PreferencesItem pit = targetRoot.getValue();
            if (pit.getItemObject() instanceof Node) {
                return (Node) pit.getItemObject();
            }
            //
            // We consider that itemObject ia a string
            //
            String clazz = (String) pit.getItemObject();
            DockSplitPane pane = new DockSplitPane();
            addDividersListener((DockPane) DockPaneController.this.getTargetNode(), pane);

            String p = pit.getProperties().get(ORIENTATION);
            if (p == null || "HORIZONTAL".equals(p)) {
                pane.setOrientation(Orientation.HORIZONTAL);
            } else {
                pane.setOrientation(Orientation.VERTICAL);
            }

            p = pit.getProperties().get(DIVIDER_POSITIONS);
            if (p != null && !p.trim().isEmpty()) {
                String[] dps = p.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                pane.setDividerPositions(dpd);
            }
            for (TreeItem<PreferencesItem> item : targetRoot.getChildren()) {
                pane.getItems().add(buildFrom(item));
            }
            return pane;
        }

        private void addDividersListener(DockPane dockPane, SplitPane splitPane) {

            ListChangeListener<Node> itemsListener = (ListChangeListener.Change<? extends Node> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends Node> list = change.getRemoved();
                        for (Node node : list) {
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        List<? extends Node> list = change.getAddedSubList();
                        for (Node node : list) {
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };
            
            ChangeListener<Number> posListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                getDockLoader().layoutChanged(dockPane);
            };

            ListChangeListener<SplitPane.Divider> divListListener = (ListChangeListener.Change<? extends SplitPane.Divider> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends SplitPane.Divider> list = change.getRemoved();
                        if (!list.isEmpty()) {
                        }
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().removeListener(posListener);
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        List<? extends SplitPane.Divider> list = change.getAddedSubList();
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().addListener(posListener);
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };
            splitPane.getDividers().addListener(divListListener);
            splitPane.getItems().addListener(itemsListener);
        }
    }
}//class DockPaneController
