/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ravelsoftware.ravtech.dk.ui.animation;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.brashmonkey.spriter.Curve;
import com.brashmonkey.spriter.Curve.Type;
import com.ravelsoftware.ravtech.animation.Animation;
import com.ravelsoftware.ravtech.animation.Key;
import com.ravelsoftware.ravtech.animation.Timeline;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.components.EditableJLabel;
import com.ravelsoftware.ravtech.dk.ui.components.SliderEditableLabel;
import com.ravelsoftware.ravtech.dk.ui.components.ValueChangedListener;
import com.ravelsoftware.ravtech.dk.ui.utils.IconUtil;
import com.ravelsoftware.ravtech.history.ChangeListener;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.Changeable;
import com.ravelsoftware.ravtech.history.ModifyChangeable;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

public class AnimationView extends JPanel {

    class DopeSheet extends JPanel {

        private static final long serialVersionUID = 978423591669137296L;
        protected int camPosX;
        protected int camPosY;
        int currentLine = camPosY + 10;
        Font font;
        int lineSpacing = 20;
        JPopupMenu tooltip;
        int xGrabPosition;
        int yGrabPosition;

        public DopeSheet() {
            super();
            this.setBackground(Color.black);
            font = new Font("sans-serif", Font.PLAIN, 12);
            tooltip = new JPopupMenu();
            new Timer(31, new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent arg0) {
                    repaint();
                }
            }).start();
            this.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked (MouseEvent arg0) {
                    float spacing = (float)DopeSheet.this.getSize().getWidth() / animation.getLength();
                    Timeline timeLine = getLineAt(arg0.getY() - camPosY);
                    if (animation != null) for (int n = 0; n < timeLine.keys.size; n++) {
                        Key key = timeLine.keys.get(n);
                        float tempTime = arg0.getX() / spacing;
                        if ((arg0.getModifiers() & ActionEvent.CTRL_MASK) != ActionEvent.CTRL_MASK
                            && arg0.getButton() != MouseEvent.BUTTON3) selectedKeys.clear();
                        /*
                         * Debug.log( "key.time - tempTime", key.time - tempTime);
                         * System.out.println("--------------------------");
                         */
                        if (Math.abs(key.time - tempTime) < 20) {
                            animation.setTime(key.time);
                            selectedKeys.add(key);
                            break;
                        }
                    }
                    if (arg0.getButton() == MouseEvent.BUTTON3) {
                        final JPopupMenu popup = new JPopupMenu();
                        ActionListener menuListener = new ActionListener() {

                            public void actionPerformed (ActionEvent event) {
                                String command = event.getActionCommand();
                                Curve curve = new Curve();
                                curve.setType(command.equals("Linear") ? Type.Linear
                                    : command.equals("Instant") ? Type.Instant : curve.getType());
                                for (int i = 0; i < selectedKeys.size; i++)
                                    selectedKeys.get(i).curve = curve;
                                if (command.equals("Custom")) RavTechDK.ui.curveEditor.setCurve(curve);
                            }
                        };
                        JMenuItem item;
                        popup.add(item = new JMenuItem("Linear", IconUtil.getIcon("curve_linear")));
                        item.setHorizontalTextPosition(SwingConstants.RIGHT);
                        item.addActionListener(menuListener);
                        popup.add(item = new JMenuItem("Instant", IconUtil.getIcon("curve_instant")));
                        item.setHorizontalTextPosition(SwingConstants.RIGHT);
                        item.addActionListener(menuListener);
                        popup.add(item = new JMenuItem("Custom", IconUtil.getIcon("curve_custom")));
                        item.setHorizontalTextPosition(SwingConstants.RIGHT);
                        item.addActionListener(menuListener);
                        popup.setLabel("Test");
                        popup.setBorder(BorderFactory.createEmptyBorder());
                        popup.show(DopeSheet.this, arg0.getX(), arg0.getY());
                    }
                }

                @Override
                public void mouseEntered (MouseEvent arg0) {
                }

                @Override
                public void mouseExited (MouseEvent arg0) {
                }

                @Override
                public void mousePressed (MouseEvent arg0) {
                    xGrabPosition = arg0.getX();
                    yGrabPosition = arg0.getY();
                }

                @Override
                public void mouseReleased (MouseEvent arg0) {
                }
            });
            this.addMouseMotionListener(new MouseMotionListener() {

                @Override
                public void mouseDragged (MouseEvent arg0) {
                    if (selectedKeys.size == 0) {
                        float spacing = 100f * (float)DopeSheet.this.getSize().getWidth() / animation.getLength();
                        int mouseTime = Math.round((-camPosX + arg0.getX()) * 100f / spacing);
                        if (animation != null) animation.setTime(mouseTime);
                    } else {
                        for (int i = 0; i < selectedKeys.size; i++)
                            selectedKeys.get(i).time += (arg0.getX() - xGrabPosition) * 5;
                        xGrabPosition = arg0.getX();
                        yGrabPosition = arg0.getY();
                    }
                }

                @Override
                public void mouseMoved (MouseEvent arg0) {
                    Timeline timeLine = getLineAt(arg0.getY() - camPosY);
                    float spacing = (float)DopeSheet.this.getSize().getWidth() / animation.getLength();
                    arg0.getY();
                    boolean isOnKey = false;
                    if (timeLine != null) for (int n = 0; n < timeLine.keys.size; n++) {
                        Key key = timeLine.keys.get(n);
                        float tempTime = arg0.getX() / spacing;
                        if (Math.abs(key.time - tempTime) < 20) {
                            JMenuItem item;
                            tooltip.removeAll();
                            if (key.curve.getType() == Type.Linear)
                                tooltip.add(item = new JMenuItem("Linear", IconUtil.getIcon("curve_linear")));
                            else if (key.curve.getType() == Type.Instant)
                                tooltip.add(item = new JMenuItem("Instant", IconUtil.getIcon("curve_instant")));
                            else
                                tooltip.add(item = new JMenuItem("Custom", IconUtil.getIcon("curve_custom")));
                            item.setHorizontalTextPosition(SwingConstants.RIGHT);
                            tooltip.show(DopeSheet.this, arg0.getX() + 2, arg0.getY() + 2);
                            isOnKey = true;
                        }
                    }
                    if (!isOnKey) tooltip.setVisible(false);
                }
            });
            this.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed (KeyEvent arg0) {
                }

                @Override
                public void keyReleased (KeyEvent arg0) {
                    if (arg0.getKeyChar() == '\b' || arg0.getKeyChar() == '')
                        if (animation != null) animation.removeKeysAtTime(animation.getTime());
                    if (arg0.getKeyCode() == KeyEvent.VK_LEFT) for (int i = 0; i < selectedKeys.size; i++)
                        selectedKeys.get(i).time -= 10;
                    if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) for (int i = 0; i < selectedKeys.size; i++)
                        selectedKeys.get(i).time += 10;
                    if (arg0.getKeyChar() == '' && (arg0.getModifiers() & InputEvent.CTRL_MASK) != 0
                        && (arg0.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
                        String clipBoardContent = "";
                        Json json = new Json();
                        Animation animation = AnimationView.this.animation;
                        AnimationSnapshotHolder holder = new AnimationSnapshotHolder();
                        for (Timeline timeline : animation.timelines) {
                            TimelineSnapshot snapshot = new TimelineSnapshot();
                            String componentPath = GameObjectTraverseUtil.pathFromGameComponent(timeline.component);
                            if (componentPath.length() > 0)
                                componentPath = componentPath.substring(
                                    GameObjectTraverseUtil.pathFromGameComponent(animation.animator.getParent()).length(),
                                    componentPath.length());
                            else
                                componentPath = GameObjectTraverseUtil.pathFromGameComponent(animation.animator.getParent());
                            snapshot.component = componentPath;
                            snapshot.variableId = timeline.variableId;
                            snapshot.key = timeline.getLastKey();
                            holder.timelineSnapshot.add(snapshot);
                        }
                        clipBoardContent = json.prettyPrint(holder);
                        //
                        Gdx.app.getClipboard().setContents(clipBoardContent);
                    }
                    if (arg0.getKeyChar() == '' && (arg0.getModifiers() & InputEvent.CTRL_MASK) != 0
                        && (arg0.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
                        String clipBoardContent = Gdx.app.getClipboard().getContents();
                        Json json = new Json();
                        Animation animation = AnimationView.this.animation;
                        AnimationSnapshotHolder holder = json.fromJson(AnimationSnapshotHolder.class, clipBoardContent);
                        for (int i = 0; i < holder.timelineSnapshot.size; i++) { // 12
                                                                                 // max
                            TimelineSnapshot snapshot = holder.timelineSnapshot.get(i);
                            boolean snapshotAdded = false;
                            for (int n = 0; n < animation.timelines.size; n++) {
                                Timeline timeline = animation.timelines.get(n);
                                String componentPath = GameObjectTraverseUtil.pathFromGameComponent(timeline.component);
                                if (componentPath.length() > 0)
                                    componentPath = componentPath.substring(
                                        GameObjectTraverseUtil.pathFromGameComponent(animation.animator.getParent()).length(),
                                        componentPath.length());
                                else
                                    componentPath = GameObjectTraverseUtil.pathFromGameComponent(animation.animator.getParent());
                                if (componentPath.equals(snapshot.component) && timeline.variableId == snapshot.variableId) {
                                    snapshot.key.time = animation.getTime();
                                    animation.timelines.get(n).addKey(snapshot.key);
                                    snapshotAdded = true;
                                }
                            }
                            if (!snapshotAdded) {
                                //
                                String componentString = GameObjectTraverseUtil
                                    .pathFromGameComponent(animation.animator.getParent()) + snapshot.component;
                                //
                                Timeline timeline = new Timeline(GameObjectTraverseUtil.gameComponentFromPath(componentString),
                                    snapshot.variableId);
                                timeline.animator = animation.animator;
                                timeline.addKey(snapshot.key);
                                animation.timelines.add(timeline);
                            }
                            /*
                             * Timeline timeline = animation.getTimeline(GameObjectTraverseUtil.
                             * gameComponentFromPath(changeable.pathToComponent) , ((ModifyChangeable) changeable).variableID);
                             * if(timeline == null) { timeline = new Timeline(GameObjectTraverseUtil.
                             * gameComponentFromPath(changeable.pathToComponent) , ((ModifyChangeable) changeable).variableID);
                             * timeline.animator = animation.animator; animation.timelines.add(timeline); } Key key = new
                             * Key(animation.getTime(), ((ModifyChangeable) changeable).newValue, timeline, new Curve());
                             * timeline.addKey(key);
                             */
                        }
                    }
                }

                @Override
                public void keyTyped (KeyEvent arg0) {
                }
            });
        }

        void addNodesToArray (Array<AnimationNode> fromArray, Array<AnimationNode> toArray) {
            for (int i = 0; i < fromArray.size; i++) {
                toArray.add(fromArray.get(i));
                addNodesToArray(fromArray.get(i).children, toArray);
            }
        }

        Timeline getLineAt (int y) {
            Array<AnimationNode> tempNodes = new Array<AnimationNode>();
            addNodesToArray(nodes, tempNodes);
            int tempY = 0;
            for (int i = 0; i < tempNodes.size; i++) {
                tempY += lineSpacing;
                if (tempY >= y) break;
            }
            tempY -= lineSpacing;
            if (tempNodes.size > 0 && tempY / 20 < tempNodes.size)
                return tempNodes.get(tempY / 20).timeline;
            else
                return null;
        }

        @Override
        public void paintComponent (Graphics g) {
            super.paintComponent(g);
            currentLine = camPosY + 10;
            // System.out.println("-------------------------");
            g.setColor(new Color(212, 212, 212, 255));
            g.fillRect(0, 0, getWidth(), getHeight());
            float spacing = 100f * (float)this.getSize().getWidth() / animation.getLength();
            if (animation != null) {
                for (float i = 0; i < animation.getLength(); i += spacing) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawLine(Math.round(i), 0, Math.round(i), getHeight());
                }
                for (float i = 0; i < animation.getLength(); i += spacing * 10) {
                    g.setColor(Color.GRAY);
                    g.drawLine(Math.round(i), 0, Math.round(i), getHeight());
                }
                renderNodes(g, nodes);
                g.setColor(Color.GRAY);
                g.drawLine(0, 0, 0, getHeight());
                g.setColor(Color.RED);
                g.drawLine(Math.round((camPosX + animation.getTime()) * spacing / 100f), 0,
                    Math.round((camPosX + animation.getTime()) * spacing / 100f), getHeight());
            }
        }

        void renderNodes (Graphics g, Array<AnimationNode> nodes) {
            float spacing = (float)this.getSize().getWidth() / animation.getLength();
            for (int i = 0; i < nodes.size; i++) {
                g.setColor(Color.GRAY);
                g.drawLine(0, currentLine, getWidth(), currentLine);
                /*
                 * g.setFont(font); g.drawString(animation.timelines.get(i).component. getComponentType() + ":" +
                 * animation.timelines.get(i).component.getVariableNames()[ animation.timelines.get(i).variableId] ,0, 10 +
                 * i*spacing);
                 */
                //
                if (nodes.get(i).timeline != null) for (int n = 0; n < nodes.get(i).timeline.keys.size; n++) {
                    Key key = nodes.get(i).timeline.keys.get(n);
                    g.setColor(new Color(212, 212, 212, 255));
                    g.fillRect((int)(camPosX - 3 + key.time * spacing), currentLine - 5, 6, 12);
                    g.setColor(selectedKeys.contains(key, true) ? Color.ORANGE : Color.BLACK);
                    g.drawRect((int)(camPosX - 3 + key.time * spacing), currentLine - 5, 6, 12);
                }
                currentLine += lineSpacing;
                renderNodes(g, nodes.get(i).children);
            }
        }
    }

    class HeaderPanel extends JPanel {

        private static final long serialVersionUID = 7750795888299847983L;
        public Font font;
        final SliderEditableLabel length = new SliderEditableLabel("Length:", 0);
        String text = "Animation";
        final SliderEditableLabel time = new SliderEditableLabel("Time:", 0);

        public HeaderPanel() {
            font = new Font("sans-serif", Font.PLAIN, 12);
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            time.changeFactor = 1;
            this.add(time.nameLabel);
            this.add(time.pairedComponent);
            ValueChangedListener listener = new ValueChangedListener() {

                @Override
                public void valueChanged (String oldValue, final String newValue, JComponent source) {
                    /*
                     * RavTechDK.addExecutable(new Runnable() {
                     *
                     * @Override public void run() {
                     */
                    if (animation != null) {
                        String value = newValue;
                        value = String.valueOf(Math.round(Float.valueOf(value)));
                        // if(!value.equals("0"))
                        // value = String.valueOf(Integer.valueOf(value) %
                        // 10000);
                        if (Integer.valueOf(value) < 0) value = String.valueOf(Integer.valueOf(value) * -1);
                        animation.setTime(Integer.valueOf(value));
                        ((EditableJLabel)time.pairedComponent).setText(value);
                    }
// }
// });
                }
            };
            ((EditableJLabel)time.pairedComponent).addValueChangedListener(listener);
            ((EditableJLabel)time.pairedComponent).addValueFinallyChangedListener(listener);
            length.changeFactor = 1;
            this.add(length.nameLabel);
            this.add(length.pairedComponent);
            ValueChangedListener lengthListener = new ValueChangedListener() {

                @Override
                public void valueChanged (String oldValue, final String newValue, JComponent source) {
                    if (animation != null) animation.setLength(Integer.valueOf(newValue));
                }
            };
            ((EditableJLabel)length.pairedComponent).addValueChangedListener(lengthListener);
            ((EditableJLabel)length.pairedComponent).addValueFinallyChangedListener(lengthListener);
            new Timer(64, new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent arg0) {
                    if (animation != null) {
                        if (!((EditableJLabel)length.pairedComponent).hasFocus)
                            ((EditableJLabel)length.pairedComponent).setText(String.valueOf(animation.getLength()));
                        if (!((EditableJLabel)time.pairedComponent).hasFocus)
                            ((EditableJLabel)time.pairedComponent).setText(String.valueOf(animation.getTime()));
                    }
                }
            }).start();
        }

        /*
         * @Override public void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
         * g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); int h = getHeight(); Color
         * titleColor = new Color(196,196,196,255); g.setColor(titleColor); g.fillRect(0, 0, getWidth(),19);
         * g.setColor(Color.BLACK); g2.setFont(font); FontRenderContext frc = g2.getFontRenderContext(); LineMetrics lm =
         * font.getLineMetrics(text, frc); float height = lm.getAscent() + lm.getDescent(); float x = 30; float y = (h + height) /
         * 2 - lm.getDescent(); g2.drawString(text, x, y); }
         */
        @Override
        public Dimension getMinimumSize () {
            return new Dimension(500, 18);
        }

        @Override
        public Dimension getPreferredSize () {
            return new Dimension(9999, 10);
        }
    }

    private static final long serialVersionUID = -9190761639322546315L;
    Animation animation;
    public String animatorComponent;
    int currentLines = 10;
    public int delayRender = 1000;
    DopeSheet dopeSheet;
    int lineSpacing = 20;
    Array<AnimationNode> nodes = new Array<AnimationNode>();
    Array<Key> selectedKeys = new Array<Key>();

    public AnimationView() {
        this.setBackground(Color.GRAY);
        this.setLayout(new GridBagLayout());
        dopeSheet = new DopeSheet();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        c.weightx = 0.0075;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        final JScrollBar scrollbar = new JScrollBar();
        scrollbar.setOrientation(Adjustable.VERTICAL);
        scrollbar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged (AdjustmentEvent arg0) {
                dopeSheet.camPosY = arg0.getAdjustable().getValue() * -1;
            }
        });
        this.add(scrollbar, c);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx++;
        c.gridwidth = 2;
        c.gridheight = 1;
        final HeaderPanel headerPanel = new HeaderPanel();
        this.add(headerPanel, c);
        c.gridy = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.2;
        c.gridwidth = 1;
        c.gridheight = 2;
        this.add(new JPanel() {

            private static final long serialVersionUID = 7883104660525816289L;

            {
                new Timer(32, new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent evt) {
                        delayRender -= 30;
                        if (delayRender > 0) return;
                        repaint();
                    }
                }).start();
            }

            @Override
            public void paintComponent (Graphics g) {
                g.setColor(new Color(214, 214, 214, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
                if (animation != null) {
                    buildNodes();
                    renderLines(g, nodes, 0);
                    /*
                     * for(int i = 0; i < animation.timelines.size; i++) { g.setColor(Color.GRAY); g.drawLine(0, 10 + i*spacing,
                     * getWidth(), 10 + i*spacing); g.setFont(headerPanel.font); try {
                     * g.drawString(GameObjectTraverseUtil.pathFromGameComponent (animation.timelines.get(i).component) + ":" +
                     * animation.timelines.get(i).component.getVariableNames()[ animation.timelines.get(i).variableId] ,0, 10 +
                     * i*spacing); } catch (Exception e) { } }
                     */
                }
            }

            public void renderLines (Graphics g, Array<AnimationNode> nodes, int indentation) {
                for (int i = 0; i < nodes.size; i++) {
                    g.setColor(Color.black);
                    g.drawLine(indentation * 10, currentLines, getWidth(), currentLines);
                    g.setFont(headerPanel.font);
                    g.drawString(nodes.get(i).name, indentation * 10, currentLines);
                    currentLines += lineSpacing;
                    renderLines(g, nodes.get(i).children, indentation + 1);
                }
            }
        }, c);
        c.gridx++;
        c.gridwidth = 1;
        c.weightx = 1;
        c.gridheight = 1;
        this.add(dopeSheet, c);
        ChangeManager.addChangeableListener(new ChangeListener() {

            @Override
            public void changed (Changeable changeable) {
                if (changeable instanceof ModifyChangeable)
                    //
                    // Debug.log("pathToAnimatorParent",
                    // GameObjectTraverseUtil.pathFromGameComponent(animation.animator));
                    // String componentPath =
                    // changeable.pathToComponent.substring(GameObjectTraverseUtil.pathFromGameComponent(animation.animator).length()
                    // / 2, changeable.pathToComponent.length());
                    //
                    if (animation != null) {
                    Timeline timeline = animation.getTimeline(
                        GameObjectTraverseUtil.gameComponentFromPath(changeable.pathToComponent),
                        ((ModifyChangeable)changeable).variableID);
                    if (timeline == null) {
                        timeline = new Timeline(GameObjectTraverseUtil.gameComponentFromPath(changeable.pathToComponent),
                            ((ModifyChangeable)changeable).variableID);
                        timeline.animator = animation.animator;
                        animation.timelines.add(timeline);
                    }
                    Key key = new Key(animation.getTime(), ((ModifyChangeable)changeable).newValue, timeline, new Curve());
                    timeline.addKey(key);
                }
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        c.weighty = 0;
        c.gridx = 2;
        final Scrollbar scrollBar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 5000, 0, 5100);
        this.add(scrollBar, c);
        scrollBar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged (AdjustmentEvent arg0) {
                dopeSheet.camPosX = arg0.getAdjustable().getValue() / -5;
            }
        });
        new Timer(32, new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                if (delayRender > 0) return;
                scrollBar.setVisibleAmount((int)(scrollBar.getWidth() / 5000.0f * scrollBar.getWidth()) * 5);
                if (animation != null) {
                    Array<AnimationNode> tempNodes = new Array<AnimationNode>();
                    addNodesToArray(nodes, tempNodes);
                    scrollbar.setMaximum(tempNodes.size * lineSpacing - dopeSheet.getHeight());
                }
                scrollbar.setMinimum(0);
                scrollbar.setVisibleAmount(5);
            }
        }).start();
    }

    void addNode (Array<String> path, int variableId, GameComponent component, Timeline timeline) {
        Array<AnimationNode> tempNodes = nodes;
        //
        for (int i = 0; i < path.size; i++) {
            // System.out.print(path.get(i)+"|");
            boolean hasFound = false;
            for (int n = 0; n < tempNodes.size; n++)
                if (tempNodes.get(n).name.equals(path.get(i))) {
                    sortNodes(tempNodes.get(n).children);
                    tempNodes = tempNodes.get(n).children;
                    hasFound = true;
                }
            if (!hasFound) {
                tempNodes.add(new AnimationNode(path.get(i)));
                if (i == path.size - 1) {
                    tempNodes.peek().variableId = variableId;
                    tempNodes.peek().component = component;
                    tempNodes.peek().timeline = timeline;
                    // Debug.log(tempNodes.peek().name, "variableId:"+
                    // variableId);
                }
                sortNodes(tempNodes);
                tempNodes = tempNodes.peek().children;
            }
        }
        // System.out.println();
    }

    void addNodesToArray (Array<AnimationNode> fromArray, Array<AnimationNode> toArray) {
        for (int i = 0; i < fromArray.size; i++) {
            toArray.add(fromArray.get(i));
            addNodesToArray(fromArray.get(i).children, toArray);
        }
    }

    void buildNodes () {
        try {
            currentLines = dopeSheet.camPosY + 10;
            nodes = new Array<AnimationNode>();
            for (int i = 0; i < animation.timelines.size; i++) {
                String[] pathNodes = GameObjectTraverseUtil.pathFromGameComponent(animation.timelines.get(i).component)
                    .split("/");
                Array<String> pathNodeArray = new Array<String>(pathNodes);
                try {
                    String variablename = animation.timelines.get(i).component
                        .getVariableNames()[animation.timelines.get(i).variableId];
                    pathNodeArray.removeIndex(0);
                    pathNodeArray.removeIndex(0);
                    pathNodeArray.add(variablename);
                    addNode(pathNodeArray, animation.timelines.get(i).variableId, animation.timelines.get(i).component,
                        animation.timelines.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setAnimation (Animation animation) {
        this.animation = animation;
        animatorComponent = GameObjectTraverseUtil.pathFromGameComponent(animation.animator);
    }

    void sortNodes (Array<AnimationNode> nodes) {
        nodes.sort(new Comparator<AnimationNode>() {

            @Override
            public int compare (AnimationNode arg0, AnimationNode arg1) {
                return arg0.variableId == arg1.variableId ? 0 : arg0.variableId > arg1.variableId ? 1 : -1;
            }
        });
    }
}
