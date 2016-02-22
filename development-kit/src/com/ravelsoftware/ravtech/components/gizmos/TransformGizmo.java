package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class TransformGizmo extends Gizmo {

    Transform transform;
    private float oldX, oldY;
    private float oldRotation;
    private static int xaxis = 1, yaxis = 2, raxis = 3, xyaxis = 4; // for drawing
                                                                    // gizmos
    private Vector2 grabPosition = null;
    private boolean isGrabbed = false;
    private int grabbedAxis = 0;
    float currentDst = 0;
    public boolean moveGrab;

    public TransformGizmo(Transform transform) {
        this.transform = transform;
    }

    @Override
    public void draw (ShapeRenderer renderer, SpriteBatch batch, boolean selected) {
        int selectedaxis;
        if (!isGrabbed)
            selectedaxis = getSelectedAxis();
        else
            selectedaxis = grabbedAxis;
        float zoom = RavTech.sceneHandler.worldCamera.zoom;
        Vector2 endpoint_x = new Vector2(transform.getPosition().add(new Vector2(50f * zoom, 0)));
        Vector2 endpoint_y = new Vector2(transform.getPosition().add(new Vector2(0, 50f * zoom)));
        Vector2 endpoint_r = new Vector2(
            transform.getPosition().add(new Vector2((float)Math.cos(Math.toRadians(transform.getRotation())) * 30f * zoom,
                (float)Math.sin(Math.toRadians(transform.getRotation())) * 30f * zoom)));
        renderer.setColor(selectedaxis != xaxis && selectedaxis != xyaxis || !selected ? Color.RED : Color.YELLOW);
        renderer.line(transform.getPosition(), endpoint_x);
        renderer.line(endpoint_x, new Vector2(endpoint_x.x - (float)Math.cos(Math.toRadians(25)) * 0.2f * 50 * zoom,
            endpoint_x.y - (float)Math.sin(Math.toRadians(25)) * 0.2f * 50 * zoom));
        renderer.line(endpoint_x, new Vector2(endpoint_x.x - (float)Math.cos(Math.toRadians(-25)) * 0.2f * 50 * zoom,
            endpoint_x.y - (float)Math.sin(Math.toRadians(-25)) * 0.2f * 50 * zoom));
        renderer.setColor(selectedaxis != yaxis && selectedaxis != xyaxis || !selected ? Color.GREEN : Color.YELLOW);
        renderer.line(transform.getPosition(), endpoint_y);
        renderer.line(endpoint_y, new Vector2(endpoint_y.x - (float)Math.cos(Math.toRadians(115)) * 0.2f * 50 * zoom,
            endpoint_y.y - (float)Math.sin(Math.toRadians(115)) * 0.2f * 50 * zoom));
        renderer.line(endpoint_y, new Vector2(endpoint_y.x - (float)Math.cos(Math.toRadians(65)) * 0.2f * 50 * zoom,
            endpoint_y.y - (float)Math.sin(Math.toRadians(65)) * 0.2f * 50 * zoom));
        renderer.setColor(selectedaxis != raxis || !selected ? Color.BLUE : Color.YELLOW);
        renderer.line(transform.getPosition(), endpoint_r);
        renderer.line(endpoint_r,
            new Vector2(endpoint_r.x - (float)Math.cos(Math.toRadians(transform.getRotation() + 25)) * 0.2f * 50 * zoom,
                endpoint_r.y - (float)Math.sin(Math.toRadians(transform.getRotation() + 25)) * 0.2f * 50 * zoom));
        renderer.line(endpoint_r,
            new Vector2(endpoint_r.x - (float)Math.cos(Math.toRadians(transform.getRotation() - 25)) * 0.2f * 50 * zoom,
                endpoint_r.y - (float)Math.sin(Math.toRadians(transform.getRotation() - 25)) * 0.2f * 50 * zoom));
        /*
         * if(!RavTech.assetManager.isLoaded("ui/Arrow.png")) { RavTech.assetManager.load("ui/Arrow.png", Texture.class);
         * RavTech.assetManager.finishLoading(); } renderer.end(); batch.begin();
         * batch.draw(RavTech.assetManager.get("ui/Arrow.png", Texture.class), getParent().transform.getPosition().x,
         * getParent().transform.getPosition().y, 15f*zoom, 15*zoom); batch.end(); renderer.begin(ShapeType.Line);
         */
    }

    @Override
    public float input (int button, int eventtype) {
        int selectedaxis = 0;
        if (!isGrabbed)
            selectedaxis = getSelectedAxis();
        else
            selectedaxis = grabbedAxis;
        if (moveGrab) selectedaxis = xyaxis;
        if (selectedaxis != 0) {
            switch (eventtype) {
                case EventType.MouseDown:
                    grabPosition = transform.getPosition().sub(RavTech.input.getWorldPosition());
                    oldX = this.transform.getLocalPosition().cpy().x;
                    oldY = this.transform.getLocalPosition().cpy().y;
                    oldRotation = this.transform.getLocalRotation();
                    this.isGrabbed = true;
                    this.grabbedAxis = selectedaxis;
                    break;
                case EventType.MouseDrag:
                    if (!isGrabbed) return currentDst;
                    Vector2 currentposition = transform.getPosition();
                    Vector2 mouseposition = RavTech.input.getWorldPosition();
                    if (grabPosition != null && (grabbedAxis == xaxis || grabbedAxis == yaxis)) {
                        this.transform
                            .setPosition(new Vector2(selectedaxis == xaxis ? mouseposition.x + grabPosition.x : currentposition.x,
                                selectedaxis == yaxis ? mouseposition.y + grabPosition.y : currentposition.y));
                        ModifyChangeable changeable = grabbedAxis == xaxis
                            ? new ModifyChangeable(this.transform, "", "x", oldX, this.transform.getLocalPosition().cpy().x)
                            : new ModifyChangeable(this.transform, "", "y", oldY, this.transform.getLocalPosition().cpy().y);
                        changeable.isDummy = true;
                        ChangeManager.addChangeable(changeable);
                    } else if (grabbedAxis == raxis) {
                        transform.setRotation(mouseposition.sub(transform.getPosition()).angle());
                        ModifyChangeable changeable = new ModifyChangeable(this.transform, "", "rotation", oldRotation,
                            this.transform.getLocalRotation() + (transform.getParent().getParent() != null
                                ? transform.getParent().getParent().transform.getRotation() : 0));
                        changeable.isDummy = true;
                        ChangeManager.addChangeable(changeable);
                    } else if (grabPosition != null && grabbedAxis == xyaxis) {
                        this.transform.setPosition(mouseposition.x + grabPosition.x, mouseposition.y + grabPosition.y);
                        ModifyChangeable changeableX = new ModifyChangeable(this.transform, "", "x", oldX,
                            this.transform.getLocalPosition().cpy().x);
                        changeableX.isDummy = true;
                        ChangeManager.addChangeable(changeableX);
                        ModifyChangeable changeableY = new ModifyChangeable(this.transform, "", "y", oldY,
                            this.transform.getLocalPosition().cpy().y);
                        changeableY.isDummy = true;
                        ChangeManager.addChangeable(changeableY);
                    }
                    break;
                case EventType.MouseUp:
                    this.isGrabbed = false;
                    ModifyChangeable changeable = null;
                    if (grabPosition != null && grabbedAxis == xaxis || grabbedAxis == xyaxis) {
                        changeable = new ModifyChangeable(this.transform,
                            "Set Transform X:" + this.transform.getLocalPosition().cpy().x, "x", oldX,
                            this.transform.getLocalPosition().cpy().x);
                        ChangeManager.addChangeable(changeable);
                    }
                    if (grabPosition != null && grabbedAxis == yaxis || grabbedAxis == xyaxis) {
                        changeable = new ModifyChangeable(this.transform,
                            "Set Transform Y:" + this.transform.getLocalPosition().cpy().y, "y", oldY,
                            this.transform.getLocalPosition().cpy().y);
                        if (grabbedAxis == xyaxis) changeable.previousConnected = true;
                        ChangeManager.addChangeable(changeable);
                    }
                    if (grabbedAxis == raxis) {
                        changeable = new ModifyChangeable(this.transform,
                            "Set Transform Rotation:" + this.transform.getLocalRotation(), "rotation", oldRotation,
                            this.transform.getLocalRotation());
                        ChangeManager.addChangeable(changeable);
                    }
                    grabbedAxis = 0;
                    break;
            }
            return currentDst;
        }
        return -1f;
    }

    private int getSelectedAxis () {
        float zoom = RavTech.sceneHandler.worldCamera.zoom;
        float selectiondst = 0.2f * 20f * zoom;
        int selectedaxis = 0;
        Vector2 endpoint_x = new Vector2(transform.getLocalPosition().cpy().add(new Vector2(50 * zoom, 0)));
        Vector2 endpoint_y = new Vector2(transform.getLocalPosition().cpy().add(new Vector2(0, 50 * zoom)));
        Vector2 endpoint_r = new Vector2(transform.getLocalPosition().cpy()
            .add(new Vector2((float)Math.cos(Math.toRadians(transform.getRotation())) * 30f * zoom,
                (float)Math.sin(Math.toRadians(transform.getRotation())) * 30f * zoom)));
        float xaxisdst = GeometryUtils.dstFromLine(transform.getLocalPosition().cpy(), endpoint_x,
            RavTech.input.getWorldPosition());
        boolean ispointnearxaxis = GeometryUtils.isPointNearLine(transform.getLocalPosition().cpy(), endpoint_x,
            RavTech.input.getWorldPosition(), selectiondst);
        float yaxisdst = GeometryUtils.dstFromLine(transform.getLocalPosition().cpy(), endpoint_y,
            RavTech.input.getWorldPosition());
        boolean ispointnearyaxis = GeometryUtils.isPointNearLine(transform.getLocalPosition().cpy(), endpoint_y,
            RavTech.input.getWorldPosition(), selectiondst);
        float raxisdst = GeometryUtils.dstFromLine(transform.getLocalPosition().cpy(), endpoint_r,
            RavTech.input.getWorldPosition());
        boolean ispointnearraxis = GeometryUtils.isPointNearLine(transform.getLocalPosition().cpy(), endpoint_r,
            RavTech.input.getWorldPosition(), selectiondst);
        if (ispointnearraxis && raxisdst <= xaxisdst && raxisdst <= yaxisdst) {
            selectedaxis = raxis;
            currentDst = raxisdst;
        } else if (ispointnearxaxis && xaxisdst <= yaxisdst && xaxisdst <= raxisdst) {
            selectedaxis = xaxis;
            currentDst = xaxisdst;
        } else if (ispointnearyaxis) {
            selectedaxis = yaxis;
            currentDst = yaxisdst;
        }
        if (RavTech.input.getWorldPosition().dst(this.transform.getPosition()) < 0.3f * 20f * zoom) {
            selectedaxis = xyaxis;
            currentDst = RavTech.input.getWorldPosition().dst(this.transform.getPosition());
        }
        return selectedaxis;
    }

    @Override
    public boolean isInBoundingBox (Vector2 coord) {
        return false;
    }
}
