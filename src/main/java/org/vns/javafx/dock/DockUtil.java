package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public class DockUtil {

    public static DockSplitPane getParentSplitPane(DockSplitPane root, Node childNode) {
        DockSplitPane retval = null;
        DockSplitPane split = root;
        Stack<DockSplitPane> stack = new Stack<>();
        stack.push(split);

        while (!stack.empty()) {
            split = stack.pop();
            if (split.getItems().contains(childNode)) {
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

    public static void clearEmptySplitPanes(DockSplitPane root, DockSplitPane empty) {
        if (root == null || !empty.getItems().isEmpty()) {
            return;
        }
        List<DockSplitPane> list = new ArrayList<>();

        DockSplitPane dsp = empty;
        while (true) {
            dsp = getParentSplitPane(root, dsp);
            if (dsp == null) {
                break;
            }
            list.add(dsp);
        }
        list.add(0, empty);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getItems().isEmpty()) {
                break;
            }
            if (i < list.size() - 1) {
                list.get(i + 1).getItems().remove(list.get(i));
            }
        }
    }

    public static Side sideValue(String dockPos) {
        Side retval = null;
        if (dockPos == null) {
            retval = Side.BOTTOM;
        } else {
            switch (dockPos) {
                case "TOP":
                    retval = Side.TOP;
                    break;
                case "BOTTOM":
                    retval = Side.BOTTOM;
                    break;
                case "LEFT":
                    retval = Side.LEFT;
                    break;
                case "RIGHT":
                    retval = Side.RIGHT;
                    break;
            }
        }
        return retval;
    }

    public static ObservableList<Dockable> getAllDockable(Region root) {
        ObservableList<Dockable> retval = FXCollections.observableArrayList();
/*        if (!(root instanceof DockTarget)) {
            return retval;
        }
*/
        //31.12List<Dockable> list = findNodes(root, p -> {return (p instanceof Dockable);});
        List<Dockable> list = findNodes(root, p -> {
            return (DockRegistry.isDockable(p));
        });
        retval.addAll(list.toArray(new Dockable[0]));
        list.forEach(d -> {
            //((DockTarget)root).dock(root, d.getDockState().getDockPos());
        });
        return retval;
    }

    public static ObservableList<Dockable> initialize(Region root) {
        ObservableList<Dockable> retval = FXCollections.observableArrayList();
        if (!(root instanceof DockPaneTarget)) {
            return retval;
        }

        List<Dockable> list = findNodes(root, p -> {
            return (DockRegistry.isDockable(p));
        });
        retval.addAll(list.toArray(new Dockable[0]));
        list.forEach(d -> {
            //((DockTarget)root).dock(root, d.getDockState().getDockPos());
            // !!!! измкнить ((DockTarget)root).dock((Node)d, d.nodeHandler().getDockPos());

        });
        return retval;
    }

    public static List findNodes(Parent root, Predicate<Node> predicate) {
        List retval = new ArrayList();
        for (Node node : root.getChildrenUnmodifiable()) {
            if (predicate.test(node)) {
                retval.add(node);
            }
            if (node instanceof Parent) {
                retval.addAll(findNodes((Parent) node, predicate));
            }
        }
        return retval;
    }

    public static Node findNode(Parent root, Predicate<Node> predicate) {
        List<Node> ls = findNodes(root, predicate);
        if (ls.isEmpty()) {
            return null;
        }
        return ls.get(0);
    }

    public static Node findDockable(Parent root, double screenX, double screenY) {

        Predicate<Node> predicate = (node) -> {
            Point2D p = node.localToScreen(0, 0);
            boolean b = false;
//31.12            if ( node instanceof Dockable ) {
            if (DockRegistry.isDockable(node)) {
                DockPaneHandler pd = DockRegistry.dockable(node).nodeHandler().getPaneHandler();
                DockNodeHandler st = DockRegistry.dockable(node).nodeHandler();
                if (pd == null) {
                    b = false;
                } else {
                    b = pd.isUsedAsDockTarget() && pd.zorder() == 0 && st.isUsedAsDockTarget();
                }
            }
            return b && !((screenX < p.getX()
                    || screenX > p.getX() + ((Region) node).getWidth()
                    || screenY < p.getY() || screenY > p.getY() + ((Region) node).getHeight()));
        };

        return findNode(root, predicate);
    }

    public static Node findTopDockPane(Parent root, double screenX, double screenY) {
        List<Node> list = new ArrayList<>();
        Parent p = root;
        while (true) {
            Node node = findDockPane(p, screenX, screenY);
            if (node == null) {
                break;
            }
            list.add(node);
            p = (Parent) node;
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static Node findDockPane(Parent root, double screenX, double screenY) {
        Predicate<Node> predicate = (node) -> {
            Point2D p = node.localToScreen(0, 0);
            boolean b = false;
            if (node instanceof DockPaneTarget) {
                DockPaneHandler pd = ((DockPaneTarget) node).paneHandler();
                if (pd == null) {
                    b = false;
                } else {
                    b = pd.isUsedAsDockTarget() && pd.zorder() == 0;
                }
                System.err.println("------- findDockPane  b = " + b);
                System.err.println("------- findDockPane  root = " + b);
                //b = b && node != root;
            }

            /*            return b && !( (screenX < p.getX() 
                      || screenX > p.getX() + ((Region)node).getWidth()
                      || screenY < p.getY() || screenY > p.getY() + ((Region)node).getHeight()
                    ));
            };
             */
            return b
                    && ((screenX >= p.getX() && screenX <= p.getX() + ((Region) node).getWidth()
                    && screenY >= p.getY() && screenY <= p.getY() + ((Region) node).getHeight()));

        };
        return findNode(root, predicate);
    }

    /**
     *
     * @param root
     * @param toSearch
     * @return
     */
    public static Node findNode(Parent root, Node toSearch) {
        if (toSearch == null) {
            return null;
        }
        Node retval = null;
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node == toSearch) {
                retval = node;
            } else if (node instanceof Parent) {
                retval = findNode((Parent) node, toSearch);
            }
            if (retval != null) {
                break;
            }
        }
        return retval;

    }

    /*    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendents((Parent) node, nodes);
            }
        }
    }

    public static void addAllDockable(Parent parent, List<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Dockable) {
                nodes.add(node);
            }
            if (node instanceof Parent) {
                addAllDockable((Parent) node, nodes);
            }
        }
    }
     */
    public static void print(Parent root) {
        print(root, 1, " ", p -> {
            return ((p instanceof Control) || (p instanceof Pane))
                    && !(p.getClass().getName().startsWith("com.sun.javafx"));
        });
    }

    public static boolean contains(Region node, double x, double y) {
        Point2D p = node.localToScreen(0, 0);
        return ((x >= p.getX() && x <= p.getX() + node.getWidth()
                && y >= p.getY() && y <= p.getY() + node.getHeight()));
    }

    public static boolean contains(Window w, double x, double y) {
        return ((x >= w.getX() && x <= w.getX() + w.getWidth()
                && y >= w.getY() && y <= w.getY() + w.getHeight()));
    }

    public static Node findNode(List<Node> list, double x, double y) {
        Node retval = null;
        for (Node node : list) {
            if (!(node instanceof Region)) {
                continue;
            }
            if (contains((Region) node, x, y)) {
                retval = node;
                break;
            }
        }
        return retval;
    }

    public static void print(Parent root, int level, String indent, Predicate<Node> predicate) {
        StringBuilder sb = new StringBuilder();
        print(sb, root, level, indent, predicate);
        System.out.println("=======================================");
        System.out.println(sb);
        System.out.println("=======================================");

    }

    public static void print(StringBuilder sb, Node node, int level, String indent, Predicate<Node> predicate) {
        String id = node.getId() == null ? " " : node.getId() + " ";
        String ln = level + "." + id;
        String ind = new String(new char[level]).replace("\0", indent);
        if (predicate.test(node)) {
            sb.append(ind)
                    .append(ln)
                    .append(" : ")
                    .append(node.getClass().getName())
                    .append(System.lineSeparator());
        }
        if (node instanceof Parent) {
            List<Node> list = ((Parent) node).getChildrenUnmodifiable();
            for (Node n : list) {
                int newLevel = level;
                if (predicate.test(n)) {
                    newLevel++;
                }
                print(sb, n, newLevel, indent, predicate);
            }
        }
    }

    public static List<Parent> getParentChain(Parent root, Node child, Predicate<Parent> predicate) {
        List<Parent> retval = new ArrayList<>();
        Node p = child;
        while (true) {
            Parent p1 = getImmediateParent(root, p);
            if (p1 != null) {
                p = p1;
                if (predicate.test(p1)) {
                    retval.add(0, p1);
                }
            } else {
                break;
            }
        }
        return retval;
    }

    public static Parent getImmediateParent(Parent root, Node child) {
        Parent retval = null;
        List<Node> list = root.getChildrenUnmodifiable();
        for (int i = 0; i < list.size(); i++) {
            Node r = list.get(i);
            if (r == child) {
                retval = root;
                break;
            }
            if (r instanceof Parent) {
                retval = getImmediateParent((Parent) r, child);
                if (retval != null) {
                    break;
                }
            }
        }
        return retval;
    }

    public static Parent getImmediateParent(Parent root, Node child, Predicate<Parent> predicate) {
        List<Parent> chain = getParentChain(root, child, predicate);
        Parent retval = null;
        if (!chain.isEmpty() && root != chain.get(chain.size() - 1)) {
            retval = chain.get(chain.size() - 1);
        }
        return retval;
    }

    public static Parent getImmediateParent(Node child, Predicate<Parent> predicate) {
        if (child == null || child.getScene() == null || child.getScene().getRoot() == null) {
            return null;
        }
        Parent root = child.getScene().getRoot();
        Parent retval = null;
        Node p = child;
        while (true) {
            Parent p1 = getImmediateParent(root, p);
            if (p1 != null) {
                p = p1;
                if (predicate.test(p1)) {
                    //retval.add(0, p1);
                    retval = p1;
                }
            } else {
                break;
            }
        }
        if (retval == root) {
            root = null;
        }
        return retval;
    }

}
