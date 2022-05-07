import org.newdawn.slick.Input;

public class Camera {
   
    private float pos_x;
    private float pos_y;

    private float zoom_level;
    private final float MIN_ZOOM = 0.01f;
    private final float MAX_ZOOM = 8.0f;

    public Camera(float x, float y, float zoom)
    {
        pos_x = x;
        pos_y = y;
        zoom_level = zoom;
    }

    public float getX() { return pos_x; }
    public float getY() { return pos_y; }

    float getZoomLevel() { return zoom_level; }

    public void setX(float x) { pos_x = x; }
    public void setY(float y) { pos_y = y; }

    public void setZoomLevel(float zoom) 
    {
         float z = zoom;
         if (zoom < MIN_ZOOM) z = MIN_ZOOM;
         if (zoom > MAX_ZOOM) z = MAX_ZOOM;

         zoom_level = z; 
    }

    public void zoom(float focus_x, float focus_y, float d)
    {

        float before_mouse_x = screenToWorldX(focus_x);
        float before_mouse_y = screenToWorldY(focus_y);

        setZoomLevel(getZoomLevel()+d);

        float dx = before_mouse_x-screenToWorldX(focus_x);
        float dy = before_mouse_y-screenToWorldY(focus_y);

        move(dx, dy);

    }
    public void move(float dx, float dy)
    {
        pos_x += dx;
        pos_y += dy;
    }

    public float screenToWorldX(float x_coord)
    {
        return x_coord*(1/getZoomLevel()) + getX();
    }
    public float screenToWorldY(float y_coord)
    {
        return y_coord*(1/getZoomLevel()) + getY();
    }

    float worldToScreenX(float x_coord)
    {
        return (x_coord-getX())*getZoomLevel();
    }

    float worldToScreenY(float y_coord)
    {
        return (y_coord-getY())*getZoomLevel();
    }


}
