import org.newdawn.slick.*;

import java.util.ArrayList;

public class Sketch {

    private ArrayList<Float> vertices;

    private float smallest_x;
    private float smallest_y;
    private float biggest_x;
    private float biggest_y;

    private Color color;

    private void calculateBoundaries()
    {
        float small_x = vertices.get(0);
        float small_y = vertices.get(1);
        float big_x = vertices.get(0);
        float big_y = vertices.get(1);

        // x values
        for (int i = 2; i < vertices.size(); i+=2)
        {
            float val = vertices.get(i);
            if (val > big_x)
            {
                big_x = val;
            }
            if (val < small_x)
            {
               small_x = val; 
            }
        }


        // y values
        for (int i  = 3; i < vertices.size(); i+=2)
        {
            float val = vertices.get(i);
            if (val > big_y)
            {
                big_y = val;
            }
            if (val < small_y)
            {
                small_y = val;
            }
        }

        smallest_x = small_x;
        smallest_y = small_y;
        biggest_x = big_x;
        biggest_y = big_y;

    }

    Sketch(ArrayList<Float> optimized_vertices, Color clr)
    {
        vertices = new ArrayList<Float>(optimized_vertices);
        color = clr;

        calculateBoundaries();
    } 

    public float getSmallestX()
    {
        return smallest_x;
    }
    public float getSmallestY()
    {
        return smallest_y;
    }
    public float getBiggestX()
    {
        return biggest_x;
    }
    public float getBiggestY()
    {
        return biggest_y;
    }


    public float getScreenX(Camera camera) 
    { 

        return camera.worldToScreenX(smallest_x);
    }
    public float getScreenY(Camera camera) 
    {
        return camera.worldToScreenY(smallest_y);
    }
    public float getScreenWidth(Camera camera)
    {
        return camera.worldToScreenX(biggest_x) - camera.worldToScreenX(smallest_x);
    }
    public float getScreenHeight(Camera camera) 
    {
        return camera.worldToScreenY(biggest_y) - camera.worldToScreenY(smallest_y);
        
    }

    public void move(float dx, float dy)
    {
        for (int i = 0; i < vertices.size(); i+=2)
        {
        
            vertices.set(i, vertices.get(i)+dx);
        }
        for (int i = 1; i < vertices.size(); i+=2)
        {
           vertices.set(i, vertices.get(i)+dy);
            
        }
        calculateBoundaries();
    }

    public boolean boxScreenCollidesWithXY(Camera camera, float x, float y)
    {

        return (x >= getScreenX(camera) && x <= getScreenX(camera) + getScreenWidth(camera) && y >= getScreenY(camera) && y <= getScreenY(camera) + getScreenHeight(camera));
    }

    private float lengthSquared(float x1, float y1, float x2, float y2)
    {
        return (float)Math.pow(x1 - x2, 2) + (float)Math.pow(y1 - y2, 2);
    }
    // Algorithm from https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment?page=1&tab=scoredesc#tab-top 
    public boolean lineScreenCollideWithCircle(Camera camera, float x, float y, float radius)
    {
        for (int i = 0; i < vertices.size()-2; i+=2)            
        {
            float x1 =  camera.worldToScreenX(vertices.get(i));
            float y1 = camera.worldToScreenY(vertices.get(i+1));
            float x2 = camera.worldToScreenX(vertices.get(i+2));
            float y2 = camera.worldToScreenY(vertices.get(i+3));

            float distance_squared;

            float l2 = lengthSquared(x1, y1, x2, y2);
            if (l2 == 0)
            {
                distance_squared = lengthSquared(x, y, x1, y1);
            }
            else
            {

                float t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / l2;
                t = Math.max(0, Math.min(1, t));

                distance_squared = lengthSquared(x, y, (x1 + t * (x2-x1)), (y1 + t * (y2-y1)));

            }

            if (Math.sqrt(distance_squared) <= radius+5*camera.getZoomLevel())
            {
                return true;
            }

        }

        return false;

    }

    public void draw(Graphics g, Camera camera)
    {
        for (int i = 0; i < vertices.size()-2; i+=2)
        {
            float first_x = vertices.get(i);
            float first_y = vertices.get(i+1);
            float second_x = vertices.get(i+2);
            float second_y = vertices.get(i+3);

            g.setLineWidth(5*camera.getZoomLevel());
            g.setColor(color);
            g.drawLine(camera.worldToScreenX(first_x), camera.worldToScreenY(first_y), camera.worldToScreenX(second_x), camera.worldToScreenY(second_y));
            
        }
       
    }

}
