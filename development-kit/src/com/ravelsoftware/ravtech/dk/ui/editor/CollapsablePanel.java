package com.ravelsoftware.ravtech.dk.ui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.ui.editor.Panels.ComponentPanel;
import com.ravelsoftware.ravtech.dk.ui.utils.IconUtil;

class CollapsablePanel extends JPanel {

    private static final long serialVersionUID = 7301407322426292529L;

    private class HeaderPanel extends JPanel implements MouseListener, MouseMotionListener {

        private static final long serialVersionUID = -4772413314504737849L;
        BufferedImage componentIcon;
        Font font;
        boolean hoverRemove;
        final int OFFSET = 30, PAD = 5;
        BufferedImage open, closed, remove, remove_hover;
        String text_;

        public HeaderPanel(String text, GameComponent component) {
            addMouseListener(this);
            addMouseMotionListener(this);
            text_ = text;
            font = new Font("sans-serif", Font.PLAIN, 12);
            setPreferredSize(new Dimension(200, 20));
            open = IconUtil.getBufferedImage("arrow_down");
            closed = IconUtil.getBufferedImage("arrow_right");
            remove = IconUtil.getBufferedImage("close");
            remove_hover = IconUtil.getBufferedImage("close_hover");
            ImageIcon icon = IconUtil.getIconByComponent(component);
            componentIcon = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = componentIcon.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
        }

        public void mouseClicked (MouseEvent e) {
        }

        @Override
        public void mouseDragged (MouseEvent arg0) {
        }

        public void mouseEntered (MouseEvent e) {
            hoveringTitle = true;
            repaint();
        }

        public void mouseExited (MouseEvent e) {
            hoverRemove = false;
            hoveringTitle = false;
            repaint();
        }

        @Override
        public void mouseMoved (MouseEvent arg0) {
            boolean tempHoverRemove = hoverRemove;
            hoverRemove = arg0.getX() > getWidth() - 16;
            if (tempHoverRemove != hoverRemove) repaint();
        }

        public void mousePressed (MouseEvent e) {
            if (e.getX() < getWidth() - PAD - getHeight() / 2)
                toggleSelection();
            else
                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run () {
                        System.out.println(
                            "remObj" + ((ComponentPanel)contentPanel_).object + "_" + ((ComponentPanel)contentPanel_).component);
                        if (((ComponentPanel)contentPanel_).component != null) {
                            if (((ComponentPanel)contentPanel_).component instanceof Transform)
                                ((ComponentPanel)contentPanel_).component.getParent().destroy();
                            else
                                ((ComponentPanel)contentPanel_).object.removeComponent(((ComponentPanel)contentPanel_).component);
                        } else {
                            RavTechDKUtil.setSelectedObject(null);
                            if (((ComponentPanel)contentPanel_).object.getParent() != null)
                                ((ComponentPanel)contentPanel_).object.getParent()
                                    .removeComponent(((ComponentPanel)contentPanel_).object);
                            else
                                ((ComponentPanel)contentPanel_).object.destroy();
                        }
                        RavTechDKUtil.inspectorChanged();
                    }
                });
        }

        public void mouseReleased (MouseEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run () {
                }
            });
        }

        protected void paintComponent (Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            Color titleColor = new Color(196, 196, 196, 255);
            g.setColor(hoveringTitle ? titleColor.darker() : titleColor);
            g.fillRect(0, 0, 500, 19);
            g.setColor(Color.BLACK);
            if (selected)
                g2.drawImage(open, PAD, h / 4, h / 2, h / 2, this);
            else
                g2.drawImage(closed, PAD, h / 4, h / 2, h / 2, this);
            g2.drawImage(hoverRemove ? remove_hover : remove, getWidth() - PAD - h / 2, h / 4, h / 2, h / 2, this);
            g2.drawImage(componentIcon, PAD + h / 2 + 4, 2, 16, 16, this);
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = font.getLineMetrics(text_, frc);
            float height = lm.getAscent() + lm.getDescent();
            float x = OFFSET + 6;
            float y = (h + height) / 2 - lm.getDescent();
            g2.drawString(text_, x, y);
        }
    }

    JPanel contentPanel_;
    HeaderPanel headerPanel_;
    boolean hoveringClose;
    boolean hoveringTitle;
    private boolean selected;

    public CollapsablePanel(String text, JPanel panel) {
        super(new BorderLayout());
        /*
         * GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(1, 3, 0, 3); gbc.weightx = 1.0; gbc.fill =
         * gbc.HORIZONTAL; gbc.gridwidth = gbc.REMAINDER;
         */
        selected = false;
        headerPanel_ = new HeaderPanel(text, ((ComponentPanel)panel).component);
        contentPanel_ = panel;
        add(headerPanel_, BorderLayout.NORTH);
        add(contentPanel_, BorderLayout.CENTER);
        contentPanel_.setVisible(false);
        // JLabel padding = new JLabel();
        // gbc.weighty = 0.0;
        // add(padding, gbc);
    }

    @Override
    public Dimension getPreferredSize () {
        return new Dimension(500, (int)(selected ? 20 + contentPanel_.getPreferredSize().getHeight() : 20));
    }

    public void toggleSelection () {
        selected = !selected;
        if (contentPanel_.isShowing())
            contentPanel_.setVisible(false);
        else
            contentPanel_.setVisible(true);
        validate();
        headerPanel_.repaint();
    }
}
