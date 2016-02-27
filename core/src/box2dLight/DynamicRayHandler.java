package box2dLight;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Rayhandler that is able to dynamically allocate lightmaps for use with multiple cameras.
 * @author Quexten
 *
 */
public class DynamicRayHandler extends RayHandler {
    
    public DynamicRayHandler(World world) {
        super(world);
    }
    
    public void setLightMap(LightMap lightMap) {
        this.lightMap = lightMap;
    }
    
    public DynamicLightMap createLightMap(int initialWidth, int initialHeight) {
        DynamicLightMap map = new DynamicLightMap(this, initialWidth, initialHeight);
        map.lightMapDrawingDisabled = true;
        return map;
    }
    
}
