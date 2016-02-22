package com.ravelsoftware.ravtech.dk.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.ui.utils.TreeUtil;

class GameObjectNode extends DefaultMutableTreeNode implements Transferable {

    /**
     *
     */
    private static final long serialVersionUID = 8148326929965975032L;
    public static DataFlavor GAME_OBJECT_FLAVOR = new DataFlavor(GameObject.class, "GameObject");
    DataFlavor flavors[] = {GAME_OBJECT_FLAVOR};
    String name = "";
    GameObject object;

    public GameObjectNode() {
        this.name = "Root";
    }

    public GameObjectNode(GameObject object) {
        this.name = object.getName();
        this.object = object;
    }

    @Override
    public TreeNode getChildAt (int childIndex) {
        if (object != null)
            return new GameObjectNode(object.getGameObjectsInChildren().get(childIndex));
        else
            return new GameObjectNode(RavTech.currentScene.gameObjects.get(childIndex));
    }

    public int getChildCount () {
        if (object != null)
            return object.getGameObjectsInChildren().size;
        else if (children == null)
            return 0;
        else if (children.size() < RavTech.currentScene.gameObjects.size)
            return children.size();
        else
            return RavTech.currentScene.gameObjects.size;
    }

    public int getIndex (TreeNode node) {
        GameObjectNode gon = (GameObjectNode)node;
        for (int i = 0; i < (object != null ? object.getGameObjectsInChildren().size
            : RavTech.currentScene.gameObjects.size); i++)
            if (gon.object.equals(object != null ? object.getGameObjectsInChildren() : RavTech.currentScene.gameObjects))
                return i;
        return -1;
    }

    public TreeNode getParent () {
        return object != null && object.getParent() != null ? new GameObjectNode(object.getParent()) : null;
    }

    @Override
    public Object getTransferData (DataFlavor arg0) throws UnsupportedFlavorException, IOException {
        return object;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors () {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported (DataFlavor flavor) {
        return flavor.getRepresentationClass() == GameObject.class;
    }

    @Override
    public String toString () {
        return this.name;
    }
}

public class GameObjectTreePanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -1535851276244646835L;
    JTree tree;

    public GameObjectTreePanel() {
        GameObjectNode root = new GameObjectNode();
        for (GameObject object : RavTech.currentScene.gameObjects) {
            GameObjectNode node = new GameObjectNode(object);
            root.add(node);
        }
        tree = new JTree(root);
        tree.setBackground(Color.WHITE);
        tree.setEditable(true);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GameObjectTreePanel.this.add(tree, constraints);
        new Timer(500, new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                if (!tree.isEditing()) GameObjectTreePanel.this.reload();
            }
        }).start();
        MouseListener listener = tree.getMouseListeners()[0];
        tree.removeMouseListener(tree.getMouseListeners()[0]);
        tree.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked (MouseEvent e) {
            }

            @Override
            public void mouseEntered (MouseEvent e) {
            }

            @Override
            public void mouseExited (MouseEvent e) {
            }

            @Override
            public void mousePressed (MouseEvent e) {
                if ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                    GameObjectNode tempnode = (GameObjectNode)tree.getClosestPathForLocation(e.getX(), e.getY())
                        .getLastPathComponent();
                    if (!RavTechDKUtil.selectedObjects.contains(tempnode.object, false))
                        RavTechDKUtil.selectedObjects.add(tempnode.object);
                    e.consume();
                } else if ((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
                    RavTechDKUtil.selectedObjects.clear();
                    int initialselectionrow = tree.getSelectionRows()[0];
                    int endselectionrow = tree.getClosestRowForLocation(e.getX(), e.getY());
                    int lower = initialselectionrow < endselectionrow ? initialselectionrow : endselectionrow;
                    int higher = initialselectionrow > endselectionrow ? initialselectionrow : endselectionrow;
                    higher++;
                    for (int i = lower; i < higher; i++) {
                        GameObjectNode tempnode = (GameObjectNode)tree.getPathForRow(i).getLastPathComponent();
                        if (tempnode.object == null) break;
                        RavTechDKUtil.selectedObjects.add(tempnode.object);
                    }
                    e.consume();
                } else {
                    RavTechDKUtil.selectedObjects.clear();
                    GameObjectNode tempnode = (GameObjectNode)tree.getClosestPathForLocation(e.getX(), e.getY())
                        .getLastPathComponent();
                    if (tempnode.object != null) RavTechDKUtil.selectedObjects.add(tempnode.object);
                }
                GameObjectTreePanel.this.reload();
                RavTechDKUtil.inspectorChanged();
            }

            @Override
            public void mouseReleased (MouseEvent e) {
            }
        });
        tree.addMouseListener(listener);
        tree.setModel(new DefaultTreeModel(null) {

            /**
             *
             */
            private static final long serialVersionUID = 9183522647198388190L;

            @Override
            public void valueForPathChanged (TreePath path, Object value) {
                ((GameObjectNode)path.getLastPathComponent()).object.getName().equals(value);
                ((GameObjectNode)path.getLastPathComponent()).name = (String)value;
            }
        });
        new TreeDragSource(tree, DnDConstants.ACTION_COPY_OR_MOVE);
        new TreeDropTarget(GameObjectTreePanel.this);
        tree.setToggleClickCount(0);
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged (TreeSelectionEvent e) {
                /*
                 * GameObjectNode node = (GameObjectNode) tree.getLastSelectedPathComponent(); if (node == null || node.object ==
                 * null) return; RavTechDKUtil.selectedObjects.add(node.object); if(!restoringSelection)
                 * GameObjectTreePanel.this.reload();
                 */
            }
        });
    }

    @Override
    public Dimension getPreferredSize () {
        return new Dimension(300, tree != null ? tree.getRowCount() * 16 + 12 : 0);
    }

    public void reload () {
        tree.getSelectionModel().getSelectionRows();
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        String expstate = TreeUtil.getExpansionState(tree, 0);
        GameObjectNode root = new GameObjectNode();
// RavTechDKUtil.selectedObjects.clear();
        for (GameObject object : RavTech.currentScene.gameObjects) {
            GameObjectNode node = new GameObjectNode(object);
            root.add(node);
        }
        model.setRoot(root);
        TreeUtil.restoreExpanstionState(tree, 0, expstate);
        for (int i = 0; i < tree.getRowCount(); i++) {
            GameObjectNode node = (GameObjectNode)tree.getPathForRow(i).getLastPathComponent();
            if (node.object != null) if (RavTechDKUtil.selectedObjects.contains(node.object, false)) {
                tree.getSelectionModel().addSelectionPath(tree.getPathForRow(i));
                RavTechDKUtil.selectedObjects.add(node.object);
            }
        }
    }
}

class TreeDragSource implements DragSourceListener, DragGestureListener {

    GameObjectNode oldNode;
    DragGestureRecognizer recognizer;
    DragSource source;
    JTree sourceTree;
    GameObjectNode transferable;

    public TreeDragSource(JTree tree, int actions) {
        sourceTree = tree;
        source = new DragSource();
        recognizer = source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
    }

    public void dragDropEnd (DragSourceDropEvent dsde) {
        /*
         * to support move or copy, we have to check which occurred:
         */
        System.out.println("Drop Action: " + dsde.getDropAction());
        /*
         * if (dsde.getDropSuccess() && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) { ((DefaultTreeModel)
         * sourceTree.getModel()) .removeNodeFromParent(oldNode); }
         */
    }

    /*
     * Drag Event Handlers
     */
    public void dragEnter (DragSourceDragEvent dsde) {
    }

    public void dragExit (DragSourceEvent dse) {
    }

    /*
     * Drag Gesture Handler
     */
    public void dragGestureRecognized (DragGestureEvent dge) {
        TreePath path = sourceTree.getSelectionPath();
        oldNode = (GameObjectNode)path.getLastPathComponent();
        transferable = new GameObjectNode(oldNode.object);
        source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);
        // If you support dropping the node anywhere, you should probably
        // start with a valid move cursor:
        // source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
        // this);
    }

    public void dragOver (DragSourceDragEvent dsde) {
    }

    public void dropActionChanged (DragSourceDragEvent dsde) {
        System.out.println("Action: " + dsde.getDropAction());
        System.out.println("Target Action: " + dsde.getTargetActions());
        System.out.println("User Action: " + dsde.getUserAction());
    }
}

class TreeDropTarget implements DropTargetListener {

    DropTarget target;
    GameObjectTreePanel targetTree;

    public TreeDropTarget(GameObjectTreePanel tree) {
        targetTree = tree;
        target = new DropTarget(targetTree.tree, this);
    }

    public void dragEnter (DropTargetDragEvent dtde) {
        TreeNode node = getNodeForEvent(dtde);
        if (node.isLeaf())
            dtde.rejectDrag();
        else
            // start by supporting move operations
            // dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            dtde.acceptDrag(dtde.getDropAction());
    }

    public void dragExit (DropTargetEvent dte) {
    }

    public void dragOver (DropTargetDragEvent dtde) {
        TreeNode node = getNodeForEvent(dtde);
        if (node.isLeaf())
            dtde.rejectDrag();
        else
            dtde.acceptDrag(dtde.getDropAction());
    }

    public void drop (DropTargetDropEvent dtde) {
        Point pt = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree)dtc.getComponent();
        TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
        GameObjectNode parent = (GameObjectNode)parentpath.getLastPathComponent();
        System.err.println("parent" + parent);
        if (parent.isLeaf()) {
            dtde.rejectDrop();
            return;
        }
        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++)
                if (tr.isDataFlavorSupported(flavors[i])) {
                    dtde.acceptDrop(dtde.getDropAction());
                    GameObject object = (GameObject)tr.getTransferData(flavors[i]);
                    if (object.getParent() != null)
                        object.getParent().getComponents().removeValue(object, true);
                    else
                        RavTech.currentScene.gameObjects.removeValue(object, true);
                    if (parent.object != null) {
                        parent.object.addComponent(object);
                        object.setParent(parent.object);
                    } else {
                        RavTech.currentScene.gameObjects.add(object);
                        object.setParent(null);
                    }
                    targetTree.reload();
// TreeUtil.restoreExpanstionState(tree, 0, expstate);
                    dtde.dropComplete(true);
                    return;
                }
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }

    public void dropActionChanged (DropTargetDragEvent dtde) {
    }

    private TreeNode getNodeForEvent (DropTargetDragEvent dtde) {
        Point p = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree)dtc.getComponent();
        TreePath path = tree.getClosestPathForLocation(p.x, p.y);
        return (TreeNode)path.getLastPathComponent();
    }
}
