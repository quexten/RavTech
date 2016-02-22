package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class BoxColliderGizmo extends Gizmo {

    BoxCollider boxCollider;
    boolean isGrabbed = false;
    int grabbedPoint = 0;
    Vector2 oldPosition;
    Vector2 oldBounds;

    public BoxColliderGizmo(BoxCollider boxCollider) {
        this.boxCollider = boxCollider;
    }

    @Override
    public void draw (ShapeRenderer renderer, SpriteBatch batch, boolean selected) {
        if (!boxCollider.canEdit) return;
        renderer.setAutoShapeType(true);
        renderer.setColor(Color.LIGHT_GRAY);
        Gdx.gl.glLineWidth(2.0f);
        float rotation = boxCollider.getParent().transform.getRotation();
        Vector2 middlePosition = boxCollider.getParent().transform.getPosition()
            .add(new Vector2(boxCollider.x, boxCollider.y).rotate(rotation));
        Vector3 unprojectedMouse = RavTech.sceneHandler.worldCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePosition = new Vector2(unprojectedMouse.x, unprojectedMouse.y);
        Vector2 tl = middlePosition.cpy()
            .sub(new Vector2(boxCollider.width, boxCollider.height).rotate(boxCollider.angle + rotation));
        Vector2 tr = middlePosition.cpy()
            .sub(new Vector2(boxCollider.width, -boxCollider.height).rotate(boxCollider.angle + rotation));
        Vector2 br = middlePosition.cpy()
            .add(new Vector2(boxCollider.width, boxCollider.height).rotate(boxCollider.angle + rotation));
        Vector2 bl = middlePosition.cpy()
            .add(new Vector2(boxCollider.width, -boxCollider.height).rotate(boxCollider.angle + rotation));
        Vector2 t = middlePosition.cpy().sub(new Vector2(0, -boxCollider.height).rotate(boxCollider.angle + rotation));
        Vector2 r = middlePosition.cpy().sub(new Vector2(-boxCollider.width, 0).rotate(boxCollider.angle + rotation));
        Vector2 b = middlePosition.cpy().sub(new Vector2(0, boxCollider.height).rotate(boxCollider.angle + rotation));
        Vector2 l = middlePosition.cpy().sub(new Vector2(boxCollider.width, 0).rotate(boxCollider.angle + rotation));
        // renderer.line(tl, br);
        // renderer.line(tr, bl);
        renderer.line(tl, tr);
        renderer.line(tr, br);
        renderer.line(br, bl);
        renderer.line(bl, tl);
        renderer.setColor(RavTech.input.getWorldPosition().dst(middlePosition) < 0.5f ? Color.YELLOW : Color.GRAY);
        renderer.line(middlePosition.cpy().add(new Vector2(0.5f, 0).rotate(boxCollider.angle + rotation)),
            middlePosition.cpy().add(new Vector2(-0.5f, 0).rotate(boxCollider.angle + rotation)));
        renderer.line(middlePosition.cpy().add(new Vector2(0, 0.5f).rotate(boxCollider.angle + rotation)),
            middlePosition.cpy().add(new Vector2(0, -0.5f).rotate(boxCollider.angle + rotation)));
        boolean ispointnearxaxis = GeometryUtils.isPointNearLine(middlePosition, r, RavTech.input.getWorldPosition(), 0.4f);
        renderer.setColor(ispointnearxaxis ? Color.YELLOW : Color.LIGHT_GRAY);
        renderer.line(middlePosition, r);
        float closestDst = Float.MAX_VALUE;
        Array<Vector2> positions = new Array<Vector2>();
        positions.add(tl);
        positions.add(tr);
        positions.add(br);
        positions.add(bl);
        positions.add(t);
        positions.add(r);
        positions.add(b);
        positions.add(l);
        for (int i = 0; i < positions.size; i++)
            if (closestDst > positions.get(i).dst(mousePosition)) closestDst = positions.get(i).dst(mousePosition);
        renderer.set(ShapeType.Filled);
        for (int i = 0; i < positions.size; i++)
            renderCircle(renderer, positions.get(i), mousePosition, positions.get(i).dst(mousePosition) <= closestDst);
        renderer.setColor(Color.GRAY);
        renderer.set(ShapeType.Line);
        Gdx.gl.glLineWidth(1.0f);
    }

    @Override
    public float input (int button, int eventType) {
        if (!boxCollider.canEdit) return -1f;
        float rotation = boxCollider.getParent().transform.getRotation();
        Vector2 middlePosition = oldPosition;
        Vector3 unprojectedMouse = RavTech.sceneHandler.worldCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePosition = new Vector2(unprojectedMouse.x, unprojectedMouse.y);
        switch (eventType) {
            case EventType.MouseDown:
                oldPosition = boxCollider.getParent().transform.getPosition()
                    .add(new Vector2(boxCollider.x, boxCollider.y).rotate(rotation));
                middlePosition = oldPosition;
                Vector2 tl = middlePosition.cpy()
                    .sub(new Vector2(boxCollider.width, boxCollider.height).rotate(boxCollider.angle + rotation));
                Vector2 tr = middlePosition.cpy()
                    .sub(new Vector2(boxCollider.width, -boxCollider.height).rotate(boxCollider.angle + rotation));
                Vector2 br = middlePosition.cpy()
                    .add(new Vector2(boxCollider.width, boxCollider.height).rotate(boxCollider.angle + rotation));
                Vector2 bl = middlePosition.cpy()
                    .add(new Vector2(boxCollider.width, -boxCollider.height).rotate(boxCollider.angle + rotation));
                Vector2 t = middlePosition.cpy().sub(new Vector2(0, -boxCollider.height).rotate(boxCollider.angle + rotation));
                Vector2 r = middlePosition.cpy().sub(new Vector2(-boxCollider.width, 0).rotate(boxCollider.angle + rotation));
                Vector2 b = middlePosition.cpy().sub(new Vector2(0, boxCollider.height).rotate(boxCollider.angle + rotation));
                Vector2 l = middlePosition.cpy().sub(new Vector2(boxCollider.width, 0).rotate(boxCollider.angle + rotation));
                float closestDst = Float.MAX_VALUE;
                int closest = -1;
                Array<Vector2> positions = new Array<Vector2>();
                positions.add(tl);
                positions.add(tr);
                positions.add(br);
                positions.add(bl);
                positions.add(t);
                positions.add(r);
                positions.add(b);
                positions.add(l);
                for (int i = 0; i < positions.size; i++)
                    if (closestDst > positions.get(i).dst(mousePosition)) {
                        closestDst = positions.get(i).dst(mousePosition);
                        closest = i;
                    }
                if (closestDst > 1) {
                    boolean ispointnearxaxis = GeometryUtils.isPointNearLine(middlePosition, r, RavTech.input.getWorldPosition(),
                        0.4f);
                    if (RavTech.input.getWorldPosition().dst(middlePosition) < 0.5f)
                        closest = 8;
                    else if (ispointnearxaxis)
                        closest = 9;
                    else
                        return -1f;
                }
                oldPosition = boxCollider.getParent().transform.getPosition()
                    .add(new Vector2(boxCollider.x, boxCollider.y).rotate(rotation));
                oldBounds = new Vector2(boxCollider.width, boxCollider.height);
                isGrabbed = true;
                grabbedPoint = closest;
                return -1f;
            case EventType.MouseDrag:
                if (isGrabbed) switch (grabbedPoint) {
                    case 0: // tl
                        changeBounds(
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).x,
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).y,
                            false, false);
                        break;
                    case 1: // tr
                        changeBounds(
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).x,
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).y,
                            false, true);
                        break;
                    case 2: // br
                        changeBounds(
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).x,
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).y,
                            true, true);
                        break;
                    case 3: // bl
                        changeBounds(
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).x,
                            mousePosition.cpy().sub(middlePosition)
                                .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).y,
                            true, false);
                        break;
                    case 4: // t
                        changeHeight(mousePosition.sub(middlePosition)
                            .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).y, true);
                        break;
                    case 5: // r
                        changeWidth(mousePosition.sub(middlePosition)
                            .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).x, true);
                        break;
                    case 6: // b
                        changeHeight(mousePosition.sub(middlePosition)
                            .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).y, false);
                        break;
                    case 7: // l
                        changeWidth(mousePosition.sub(middlePosition)
                            .rotate(-boxCollider.getParent().transform.getRotation() - boxCollider.angle).x, false);
                        break;
                    case 8:
                        Vector2 subPosition = mousePosition.sub(boxCollider.getParent().transform.getPosition());
                        boxCollider.x = subPosition.x;
                        boxCollider.y = subPosition.y;
                        break;
                    case 9:
                        boxCollider.angle = mousePosition.sub(middlePosition).angle();
                        break;
                }
                return -1f;
            case EventType.MouseUp:
                isGrabbed = false;
                boxCollider.apply();
                break;
        }
        return -1f;
    }

    private void changeWidth (float width, boolean changeRight) {
        width = (changeRight ? 0.5f : -0.5f) * (width - oldBounds.x) + oldBounds.x * (changeRight ? 1 : 0);
        Vector2 addPosition = new Vector2(changeRight ? width + -oldBounds.x : oldBounds.x - width, 0)
            .rotate(boxCollider.getParent().transform.getRotation() + boxCollider.angle);
        boxCollider.x = oldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).x;
        boxCollider.y = oldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).y;
        boxCollider.width = width;
    }

    private void changeHeight (float height, boolean changeTop) {
        height = (changeTop ? 0.5f : -0.5f) * (height - oldBounds.y) + oldBounds.y * (changeTop ? 1 : 0);
        Vector2 addPosition = new Vector2(0, changeTop ? height + -oldBounds.y : oldBounds.y - height)
            .rotate(boxCollider.getParent().transform.getRotation() + boxCollider.angle);
        boxCollider.x = oldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).x;
        boxCollider.y = oldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).y;
        boxCollider.height = height;
    }

    private void changeBounds (float width, float height, boolean changeRight, boolean changeTop) {
        width = (changeRight ? 0.5f : -0.5f) * (width - oldBounds.x) + oldBounds.x * (changeRight ? 1 : 0);
        Vector2 addPosition = new Vector2(changeRight ? width + -oldBounds.x : oldBounds.x - width, 0)
            .rotate(boxCollider.getParent().transform.getRotation() + boxCollider.angle);
        boxCollider.x = oldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).x;
        boxCollider.y = oldPosition.cpy().add(addPosition).sub(boxCollider.getParent().transform.getPosition()).y;
        height = (changeTop ? 0.5f : -0.5f) * (height - oldBounds.y) + oldBounds.y * (changeTop ? 1 : 0);
        Vector2 addPosition2 = new Vector2(0, changeTop ? height + -oldBounds.y : oldBounds.y - height)
            .rotate(boxCollider.getParent().transform.getRotation() + boxCollider.angle);
        Vector2 newPosition = new Vector2(boxCollider.x, boxCollider.y);
        boxCollider.x = newPosition.cpy().add(addPosition2).x;
        boxCollider.y = newPosition.cpy().add(addPosition2).y;
        boxCollider.width = width;
        boxCollider.height = height;
    }

    private void renderCircle (ShapeRenderer renderer, Vector2 position, Vector2 mousePosition, boolean isClosest) {
        boolean hoverable = (Gdx.input.isButtonPressed(Buttons.LEFT) && isGrabbed || !Gdx.input.isButtonPressed(Buttons.LEFT))
            && isClosest;
        renderer
            .setColor(isGrabbed || position.dst(mousePosition.x, mousePosition.y) < 1 && hoverable ? Color.YELLOW : Color.GRAY);
        renderer.circle(position.x, position.y, 1 * 0.15f, 20);
    }

    @Override
    public boolean isInBoundingBox (Vector2 coord) {
        return false;
    }
}
