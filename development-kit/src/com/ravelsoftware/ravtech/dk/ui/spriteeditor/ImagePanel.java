package com.ravelsoftware.ravtech.dk.ui.spriteeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.dk.RavTechDK;

public class ImagePanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 6962699070040079428L;
    BufferedImage background;
    BufferedImage image;
    SpriteRenderer renderer;
    double scale;
    int selectionNX;
    int selectionNY;
    int selectionSX;
    int selectionSY;

    public ImagePanel(SpriteRenderer renderer) {
        this.renderer = renderer;
        String renderpath = renderer.texturePath;
        System.out.println("userdir =" + System.getProperty("user.dir") + "/" + "resources/patterns/checker.png");
        loadImage(new File(RavTechDK.projectHandle.path() + "/" + renderpath));
        try {
            background = ImageIO.read(new File(System.getProperty("user.dir") + "/" + "resources/patterns/checker.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scale = 1.0;
        setBackground(Color.black);
    }

    /** For the scroll pane. */
    public Dimension getPreferredSize () {
        int w = (int)(scale * image.getWidth());
        int h = (int)(scale * image.getHeight());
        return new Dimension(w, h);
    }

    private void loadImage (File file) {
        // String fileName = "resources/patters/test.png";
        try {
            // URL url = getClass().getResource(fileName);
            System.out.println("Absolute path: " + file.getAbsolutePath());
            image = ImageIO.read(file);
        } catch (MalformedURLException mue) {
            System.out.println("URL trouble: " + mue.getMessage());
        } catch (IOException ioe) {
            System.out.println("read trouble: " + ioe.getMessage());
        }
    }

    public void mouseDragged (MouseEvent e) {
        selectionNX = e.getX();
        selectionNY = e.getY();
        repaint();
    }

    public void mousePressed (MouseEvent e) {
        selectionSX = e.getX();
        selectionSY = e.getY();
    }

    public void mouseReleased (MouseEvent e) {
        renderer.srcX = selectionSX;
        renderer.srcY = selectionSY;
        renderer.srcWidth = selectionNX - selectionSX;
        renderer.srcHeight = selectionNY - selectionSY;
    }

    protected void paintComponent (Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        int w = getWidth();
        int h = getHeight();
        image.getWidth();
        image.getHeight();
        AffineTransform at = AffineTransform.getTranslateInstance(getX(), getY());
        at.scale(scale, scale);
        Paint paint = g2.getPaint();
        TexturePaint tp = new TexturePaint(background, new Rectangle(0, 0, 10, 10));
        g2.setPaint(tp);
        g2.fillRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setPaint(paint);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawRenderedImage(image, at);
        g2.setColor(new Color(0, 32, 255, 128));
        g2.drawRect((int)(selectionSX * scale + getX()), (int)(selectionSY * scale + getY()),
            (int)((selectionNX - selectionSX) * scale), (int)((selectionNY - selectionSY) * scale));
        System.out.println("selectionSX" + selectionSX);
        System.out.println("selectionSY" + selectionSY);
        System.out.println("SelectionNX" + selectionNX);
        System.out.println("SelectionNY" + selectionNY);
    }

    public void setScale (double s) {
        scale = s;
        // revalidate(); // update the scroll pane
        repaint();
    }
}
