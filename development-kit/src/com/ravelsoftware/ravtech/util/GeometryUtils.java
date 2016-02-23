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
package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GeometryUtils {

    public static float dstFromLine (Vector2 linestart, Vector2 lineend, Vector2 point) {
        double abs = Math
            .abs((lineend.x - linestart.x) * (linestart.y - point.y) - (linestart.x - point.x) * (lineend.y - linestart.y));
        double sqrt = Math.sqrt(Math.pow(lineend.x - linestart.x, 2) + Math.pow(lineend.y - linestart.y, 2));
        return (float)(abs / sqrt);
    }

    public static boolean isPointNearLine (Vector2 linestart, Vector2 lineend, Vector2 point, float margin) {
        if (dstFromLine(linestart, lineend, point) < margin && isInBoundingBox(linestart, lineend, point, margin))
            return true;
        else
            return false;
    }

    public static boolean isInBoundingBox (Vector2 cornerOne, Vector2 cornerTwo, Vector2 point, float margin) {
        Vector2 topLeft = new Vector2(Math.min(cornerOne.x, cornerTwo.x), Math.min(cornerOne.y, cornerTwo.y));
        Vector2 bottomRight = new Vector2(Math.max(cornerOne.x, cornerTwo.x), Math.max(cornerOne.y, cornerTwo.y));
        float boxX = topLeft.x - margin;
        float boxY = topLeft.y - margin;
        float boxWidth = bottomRight.x + margin - boxX;
        float boxHeight = bottomRight.y + margin - boxY;
        Rectangle box = new Rectangle(boxX, boxY, boxWidth, boxHeight);
        return box.contains(point);
    }

    public static boolean isBetween (float ly, float hy, float y) {
        return hy > ly ? y > ly && y < hy : y > hy && y < ly;
    }
}
