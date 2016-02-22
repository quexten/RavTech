package com.ravelsoftware.ravtech.dk.ui.spriteeditor;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class DragScrollPane extends JScrollPane {

    class ViewportDragScrollListener extends MouseAdapter implements HierarchyListener {

        private static final int DELAY = 10;
        private static final int SPEED = 4;
        private boolean autoScroll = false;
        private final Cursor dc;
        private final Cursor hc = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        private final JComponent label;
        private final Point move = new Point();
        private final javax.swing.Timer scroller;
        private final Point startPt = new Point();

        public ViewportDragScrollListener(JComponent comp, boolean autoScroll) {
            this.label = comp;
            this.autoScroll = autoScroll;
            this.dc = comp.getCursor();
            this.scroller = new javax.swing.Timer(DELAY, new ActionListener() {

                public void actionPerformed (ActionEvent e) {
                    JViewport vport = (JViewport)label.getParent();
                    Point vp = vport.getViewPosition();
                    vp.translate(move.x, move.y);
                    label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
                }
            });
        }

        public void hierarchyChanged (HierarchyEvent e) {
            JComponent c = (JComponent)e.getSource();
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && !c.isDisplayable() && autoScroll)
                scroller.stop();
        }

        @Override
        public void mouseDragged (MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                JViewport vport = (JViewport)e.getSource();
                Point pt = e.getPoint();
                int dx = startPt.x - pt.x;
                int dy = startPt.y - pt.y;
                Point vp = vport.getViewPosition();
                vp.translate(dx, dy);
                label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
                move.setLocation(SPEED * dx, SPEED * dy);
                startPt.setLocation(pt);
                vport.getView().repaint();
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                JViewport vport = (JViewport)e.getSource();
                ((ImagePanel)vport.getView()).mouseDragged(e);
            }
        }

        @Override
        public void mouseExited (MouseEvent e) {
            ((JComponent)e.getSource()).setCursor(dc);
            move.setLocation(0, 0);
            if (autoScroll) scroller.stop();
        }

        @Override
        public void mousePressed (MouseEvent e) {
            ((JComponent)e.getSource()).setCursor(hc);
            startPt.setLocation(e.getPoint());
            move.setLocation(0, 0);
            if (autoScroll) scroller.stop();
            if (SwingUtilities.isLeftMouseButton(e)) {
                JViewport vport = (JViewport)e.getSource();
                ((ImagePanel)vport.getView()).mousePressed(e);
            }
        }

        @Override
        public void mouseReleased (MouseEvent e) {
            ((JComponent)e.getSource()).setCursor(dc);
            if (autoScroll) scroller.start();
            if (SwingUtilities.isLeftMouseButton(e)) {
                JViewport vport = (JViewport)e.getSource();
                ((ImagePanel)vport.getView()).mouseReleased(e);
            }
        }
    }

    private static final long serialVersionUID = 1L;

    public DragScrollPane(JComponent objectToMove) {
        super(objectToMove);
        ViewportDragScrollListener l = new ViewportDragScrollListener(objectToMove, false);
        final JViewport gridScrollPaneViewport = getViewport();
        gridScrollPaneViewport.addMouseMotionListener(l);
        gridScrollPaneViewport.addMouseListener(l);
        gridScrollPaneViewport.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved (MouseWheelEvent arg0) {
                System.out.println(arg0.getWheelRotation());
                ((ImagePanel)gridScrollPaneViewport.getView()).scale += arg0.getWheelRotation() * 0.1;
                gridScrollPaneViewport.getView().repaint();
            }
        });
        gridScrollPaneViewport.addHierarchyListener(l);
    }
}
