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
package com.ravelsoftware.ravtech.dk.ui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

import org.fife.ui.AboutDialog;
import org.fife.ui.OS;
import org.fife.ui.app.osxadapter.NativeMacApp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.SceneHandler;
import com.ravelsoftware.ravtech.components.Animator;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.actions.AddComponentAction;
import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.dk.ui.animation.AnimationView;
import com.ravelsoftware.ravtech.dk.ui.docking.DockFactory;
import com.ravelsoftware.ravtech.dk.ui.docking.DockFrameUtil;
import com.ravelsoftware.ravtech.dk.ui.editor.Inspector.InspectableType;
import com.ravelsoftware.ravtech.dk.ui.options.OpenOptionsDialogAction;
import com.ravelsoftware.ravtech.dk.ui.utils.IconUtil;
import com.ravelsoftware.ravtech.dk.zerobrane.ZeroBraneUtil;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.CreateChangeable;
import com.ravelsoftware.ravtech.settings.RavSettings;
import com.ravelsoftware.ravtech.util.Debug;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;

public class RavTechDKFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -2245300059272207529L;
    public RavTech ravtech;
    public LwjglAWTCanvas canvas;
    public AnimationView animationView;
    public FileView view;

    public RavTechDKFrame(RavTech ravtech) {
        super();
        this.setTitle(getFullTitle());
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        possibleMacOSXRegistration();
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.put("Component.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        this.getContentPane().setLayout(new BorderLayout());
        canvas = new LwjglAWTCanvas(ravtech);
        Gdx.graphics.setVSync(true);
        CControl control = DockFactory.dockFrame(this);
        add(control.getContentArea());
        CGrid grid = new CGrid(control);
        Inspector inspector = new Inspector();
        final JPanel inspectorpanel = new JPanel() {

            /**
             *
             */
            private static final long serialVersionUID = -5095094329714850816L;

            @Override
            public Dimension getPreferredSize () {
                return new Dimension(100, (int)super.getPreferredSize().getHeight() + 30);
            }
        };
        inspectorpanel.add(inspector, BorderLayout.NORTH);
        final JButton addComponent_button = new JButton("add Component");
        addComponent_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                new AddComponentAction(addComponent_button).run();
            }
        });
        inspectorpanel.add(addComponent_button, BorderLayout.SOUTH);
        JScrollPane pane = new JScrollPane(inspectorpanel);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        SingleCDockable dockable = DockFactory.create("inspector", "Inspector", pane,
            new ImageIcon("resources//icons//zoom.png"));
        new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        final ImageIcon iconPlay = new ImageIcon("resources//icons//control_play_blue.png");
        final ImageIcon iconPlayPressed = new ImageIcon("resources//icons//control_play.png");
        final ImageIcon iconStop = new ImageIcon("resources//icons//control_stop_blue.png");
        final ImageIcon iconStopPressed = new ImageIcon("resources//icons//control_stop.png");
        final JButton buttonPlay = new JButton(iconPlay) {

            /**
             *
             */
            private static final long serialVersionUID = -6038131501545587591L;

            @Override
            public void paintComponent (Graphics g) {
                g.setColor(Color.WHITE);
                if (this.isBorderPainted()) g.drawRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
                super.paintComponent(g);
            }
        };
        buttonPlay.addMouseListener(new java.awt.event.MouseAdapter() {

            String sceneState;

            public void mouseEntered (java.awt.event.MouseEvent evt) {
                buttonPlay.setBorderPainted(true);
            }

            public void mouseExited (java.awt.event.MouseEvent evt) {
                buttonPlay.setBorderPainted(false);
            }

            public void mousePressed (MouseEvent evt) {
                if (RavTech.sceneHandler.paused)
                    buttonPlay.setIcon(iconPlayPressed);
                else
                    buttonPlay.setIcon(iconStopPressed);
            }

            public void mouseReleased (MouseEvent evt) {
                if (RavTech.sceneHandler.paused) {
                    animationView.delayRender = 10000;
                    /*
                     * Packet_PlayingState packet = new Packet_PlayingState(); packet.playing = true;
                     * RavTech.net.sendToAll(packet, false);
                     */
                    RavTechDKUtil.selectedObject = RavTechDKUtil.selectedObjects.size > 0
                        ? GameObjectTraverseUtil.pathFromGameComponent(RavTechDKUtil.selectedObjects.get(0)) : "";
                    sceneState = RavTech.files.storeState();
                    RavTech.sceneHandler.paused = false;
                    buttonPlay.setIcon(iconStop);
                    canvas.getCanvas().requestFocus();
                    RavTech.sceneHandler.reloadScripts();
                    RavTech.sceneHandler.initScripts();
                } else {
                    animationView.delayRender = 10000;
                    /*
                     * Packet_PlayingState packet = new Packet_PlayingState(); packet.playing = false;
                     * RavTech.net.sendToAll(packet, false);
                     */
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run () {
                            RavTech.sceneHandler.dispose();
                            RavTech.sceneHandler = new SceneHandler();
                            RavTech.sceneHandler.load();
                            RavTech.files.loadState(sceneState);
                            RavTech.sceneHandler.paused = true;
                        }
                    });
                    buttonPlay.setIcon(iconPlay);
                    canvas.getCanvas().requestFocus();
                    if (animationView.animatorComponent != null) animationView.setAnimation(((Animator)GameObjectTraverseUtil
                        .gameComponentFromPath(animationView.animatorComponent)).currentAnimation);
                    Array<GameObject> selectedObjects = new Array<GameObject>();
                    selectedObjects.add((GameObject)GameObjectTraverseUtil.gameComponentFromPath(RavTechDKUtil.selectedObject));
                    RavTechDKUtil.setSelectedObjects(new Array<GameObject>());
                    RavTechDKUtil.setSelectedObjects(selectedObjects);
                }
            }
        });
        buttonPlay.setBorder(new EmptyBorder(0, 3, 0, 3));
        buttonPlay.setBorderPainted(false);
        buttonPlay.setMargin(new Insets(0, 0, 0, 0));
        buttonPlay.setFocusPainted(false);
        buttonPlay.setContentAreaFilled(false);
        buttonPlay.setBackground(new Color(0, 0, 0, 0));
        // buttonPlay.setContentAreaFilled(false);
        // panelTitle.add(buttonPlay);
        grid.add(0, 0, 1, 0.5, dockable);
        view = new FileView();
        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 0;
        constraints.weightx = 2;
        panel.add(view.breadcrumbBar, constraints);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.gridy++;
        JScrollPane scrollpane = new JScrollPane(view.component);
        panel.add(scrollpane, constraints);
        grid.add(0, 1, 1, 0.5,
            DockFactory.create("gameobjects", "GameObject", new JScrollPane(new GameObjectTreePanel()),
                new ImageIcon("resources//icons//folder.png")),
            DockFactory.create("assets", "Assets", panel, new ImageIcon("resources//icons//folder.png")));
        grid.add(1, 0, 4.625, 1, DockFrameUtil.getDockable("editor", "Editor", canvas.getCanvas(),
            new ImageIcon("resources//icons//world.png"), control, buttonPlay));
        final JTextPane textField = new JTextPane();
        textField.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed (KeyEvent arg0) {
                /*
                 * if (arg0.getKeyChar() == '\n') try { new LuaScript(textField.getText().substring(textField.getText().
                 * lastIndexOf("\n"))).executeFunction("init", null); } catch (Exception e) { }
                 */
            }

            @Override
            public void keyReleased (KeyEvent arg0) {
            }

            @Override
            public void keyTyped (KeyEvent arg0) {
            }
        });
        JScrollPane scrollPane = new JScrollPane(textField);
        MessageConsole console = new MessageConsole(textField);
        console.setMessageLines(400);
        console.redirectErr(Color.RED, System.err);
        console.redirectOut(Color.BLACK, System.out);
        textField.setEditable(true);
        animationView = new AnimationView();
        grid.add(1, 1, 4.625, 0.5,
            DockFactory.create("animationView", "AnimationView", RavTechDKFrame.this.animationView,
                new ImageIcon("resources//icons//timeline_marker.png")),
            DockFactory.create("console", "Console", scrollPane, new ImageIcon("resources//icons//terminal.png")));
        grid.add(4, 1, 0.5, 0.5,
            DockFactory.create("historyview", "HistoryView", new HistoryView(), new ImageIcon("resources//icons//time.png")));
        // CanvasDropTarget canvastarget = new
        // CanvasDropTarget(canvas.getCanvas());
        control.getContentArea().deploy(grid);
        this.setJMenuBar(createMenuBar());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1600, 900);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        try {
            this.setIconImage(ImageIO.read(new File("resources//icons//rav128.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
        RavTech.settings = new RavSettings();
        AdbManager.onBoot();
    }

    private JMenuBar createMenuBar () {
        JMenuBar bar = new JMenuBar() {

            /**
             *
             */
            private static final long serialVersionUID = -8517292180132539118L;
            Color bgColor = Color.LIGHT_GRAY.brighter();

            @Override
            protected void paintComponent (Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor(bgColor);
                g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        bar.setBorder(null);
        JMenu menu;
        JMenuItem entry;
        { // File Menu
            menu = new JMenu("File");
            { // New Scene Entry
                entry = new JMenuItem("New Scene");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        RavTech.currentScene.dispose();
                        RavTech.currentScene = new Scene();
                    }
                });
                entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
                entry.setIcon(IconUtil.getIcon("page_white_add"));
                menu.add(entry);
            }
            { // Save Scene
                entry = new JMenuItem("Save");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent e) {
                        RavTech.files.getAsset(RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene));
                        Debug.log("Saved Scene",
                            "[" + RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene) + "]");
                    }
                });
                entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
                entry.setIcon(IconUtil.getIcon("disk"));
                menu.add(entry);
            }
            { // Save Scene
                entry = new JMenuItem("Save As");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent e) {
                        final JFileChooser fileChooser = new JFileChooser();
                        if (fileChooser.showSaveDialog(canvas.getCanvas()) == JFileChooser.APPROVE_OPTION)
                            Gdx.app.postRunnable(new Runnable() {

                            @Override
                            public void run () {
                                RavTechDK.saveScene(Gdx.files.absolute(fileChooser.getSelectedFile().getPath()));
                                Debug.log("Saved Scene", "[" + fileChooser.getSelectedFile().getPath() + "]");
                            }
                        });
                    }
                });
                entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
                entry.setIcon(IconUtil.getIcon("disk"));
                menu.add(entry);
            }
            { // Load Scene
                entry = new JMenuItem("Load Scene");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent e) {
                        final JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setCurrentDirectory(new File(RavTechDK.projectHandle.path()));
                        if (fileChooser.showOpenDialog(canvas.getCanvas()) == JFileChooser.APPROVE_OPTION)
                            Gdx.app.postRunnable(new Runnable() {

                            @Override
                            public void run () {
                            }
                        });
                    }
                });
                entry.setIcon(IconUtil.getIcon("folder_up"));
                menu.add(entry);
            }
            menu.addSeparator();
            { // Build
                entry = new JMenuItem("Build");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent e) {
                        RavTechDK.ui.buildWizard.setVisible(true);
                    }
                });
                entry.setIcon(IconUtil.getIcon("package_go"));
                menu.add(entry);
            }
            { // New Project Entry
            }
            { // Import Project Entry
            }
            { // Export Project Entry
            }
            bar.add(menu);
        }
        { // Network Menu
            JMenu menuNetwork = new JMenu("Network");
            final JMenuItem itemHost = new JMenuItem("Host", IconUtil.getIcon("transmit"));
            final JMenuItem itemConnect = new JMenuItem("Connect", IconUtil.getIcon("transmit_go"));
            itemHost.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent arg0) {
                    /*
                     * if (!RavTech.net.isInLobby()) { RavTech.net.createLobby(54555, 54554, 4);
                     * itemHost.setIcon(IconUtil.getIcon("transmit_blue_delete") ); } else { RavTech.net.leaveLobby();
                     * itemHost.setIcon(IconUtil.getIcon("transmit")); }
                     */
                    itemConnect.setEnabled(!itemConnect.isEnabled());
                }
            });
            menuNetwork.add(itemHost);
            itemConnect.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent arg0) {
                    /*
                     * if (!RavTech.net.isInLobby()) { RavTech.net.joinLobby("localhost", 54555, 54554);
                     * itemConnect.setIcon(IconUtil.getIcon( "transmit_blue_delete")); } else { RavTech.net.leaveLobby();
                     * itemConnect.setIcon(IconUtil.getIcon("transmit_go")); } itemHost.setEnabled(!itemHost.isEnabled());
                     */
                }
            });
            menuNetwork.add(itemConnect);
            bar.add(menuNetwork);
        }
        { // Components Menu
            menu = new JMenu("Components");
            { // Components
                entry = new JMenuItem("Add GameObject");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        CreateChangeable changeable = new CreateChangeable(null, "Added GameObject",
                            "{\"componentType\":\"GameObject\",\"name\":\"DEFAULT\",\"components\":[{\"componentType\":\"Transform\",\"x\":"
                                + RavTech.sceneHandler.worldCamera.position.x + ",\"y\":"
                                + RavTech.sceneHandler.worldCamera.position.y + ",\"rotation\":0,\"scale\":1}]}");
                        ChangeManager.addChangeable(changeable);
                        RavTechDKUtil.setSelectedObject(RavTech.currentScene.gameObjects.peek());
                    }
                });
                menu.add(entry);
            }
            bar.add(menu);
        }
        { // Window Menu
            menu = new JMenu("Project");
            { // Components
                entry = new JMenuItem("Component Settings");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        RavTechDKUtil.inspectableType = InspectableType.GameComponents;
                        RavTechDKUtil.inspectorChanged();
                    }
                });
                menu.add(entry);
            }
            { // RenderSettings
                entry = new JMenuItem("Render Settings");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        // RavTechDKUtil.inspectableType =
                        // InspectableType.RenderSettings;
                        RavTechDKUtil.inspectorChanged();
                    }
                });
                menu.add(entry);
            }
            bar.add(menu);
        }
        { // Window Menu
            menu = new JMenu("Window");
            { // About
                entry = new JMenuItem("Preferences");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        new OpenOptionsDialogAction().perform();
                    }
                });
                entry.setIcon(IconUtil.getIcon("page_white_gear"));
                menu.add(entry);
            }
            bar.add(menu);
        }
        { // Help
            menu = new JMenu("Help");
            { // About
                entry = new JMenuItem("About");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        AboutDialog aboutDialog = new AboutDialog(RavTechDK.ui.ravtechDKFrame, "RavTech Development Kit") {

                            /**
                             *
                             */
                            private static final long serialVersionUID = -6619618188330021708L;

                            protected JDialog createLicenseDialog () {
                                LicenseDialog dialog = new LicenseDialog(this, Gdx.files.local("License.txt").readString());
                                return dialog;
                            }
                        };
                        aboutDialog.setSize(400, 500);
                        aboutDialog.setLocationRelativeTo(null);
                        aboutDialog.setVisible(true);
                    }
                });
                entry.setIcon(IconUtil.getIcon("rav16"));
                menu.add(entry);
            }
            { // ZeroBrane
                entry = new JMenuItem("Check for ZeroBrane Updates");
                entry.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed (ActionEvent arg0) {
                        ZeroBraneUtil.checkForUpdates();
                    }
                });
                menu.add(entry);
            }
            bar.add(menu);
        }
        bar.add(Box.createHorizontalGlue());
        return bar;
    }

    /** returns the title of the application including Version Number and path
     *
     * @return the Title of the application */
    public String getFullTitle () {
        return "RavTech Development Kit - Version " + RavTech.majorVersion + "." + RavTech.minorVersion + "."
            + RavTech.microVersion
            + (RavTechDK.projectHandle == null ? ""
                : " - " + RavTechDK.projectHandle.path().replace('\\', '/') + " - "
                    + RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene));
    }

    /*
     * Enables MacOSX specific functions when on said OS taken from Fife-Common library
     */
    private void possibleMacOSXRegistration () {
        if (OS.get() == OS.MAC_OS_X) try {
            Class<?> osxAdapter = Class.forName("org.fife.ui.app.osxadapter.OSXAdapter");
            Class<?>[] defArgs = {NativeMacApp.class};
            Method registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", defArgs);
            if (registerMethod != null) {
                Object[] args = {this};
                registerMethod.invoke(osxAdapter, args);
            }
            // This is slightly gross. to reflectively access methods
            // with boolean args, use "boolean.class", then pass a
            // Boolean object in as the arg, which apparently gets
            // converted for you by the reflection system.
            defArgs[0] = boolean.class;
            Method prefsEnableMethod = osxAdapter.getDeclaredMethod("enablePrefs", defArgs);
            if (prefsEnableMethod != null) {
                Object args[] = {Boolean.TRUE};
                prefsEnableMethod.invoke(osxAdapter, args);
            }
        } catch (NoClassDefFoundError e) {
            // This will be thrown first if the OSXAdapter is loaded on
            // a system without the EAWT because OSXAdapter extends
            // ApplicationAdapter in its def
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // This shouldn't be reached; if there's a problem with the
            // OSXAdapter we should get the above NoClassDefFoundError
            // first.
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
