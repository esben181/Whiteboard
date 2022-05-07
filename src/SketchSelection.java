import java.util.ArrayList;
import org.newdawn.slick.*;

public class SketchSelection {

    private ArrayList<Sketch> sketches;

    private float smallest_x;
    private float smallest_y;
    private float biggest_x;
    private float biggest_y;


    private void calculateSelectionDimensions()
    {
        if (sketches.size() <= 0) return;

        float small_x = sketches.get(0).getSmallestX();
        float small_y = sketches.get(0).getSmallestY();
        float big_x = sketches.get(0).getBiggestX();
        float big_y = sketches.get(0).getBiggestY();
        for (int i = 1; i < sketches.size(); ++i)
        {
           if (sketches.get(i).getSmallestX() < small_x) 
           {
               small_x = sketches.get(i).getSmallestX();
           }
           if (sketches.get(i).getSmallestY() < small_y)
           {
               small_y = sketches.get(i).getSmallestY();
           }
           if (sketches.get(i).getBiggestX() > big_x)
           {
               big_x = sketches.get(i).getBiggestX();
           }
           if (sketches.get(i).getBiggestY() > big_y)
           {
               big_y = sketches.get(i).getBiggestY();
           }
        }

        smallest_x = small_x;
        smallest_y = small_y;
        biggest_x = big_x;
        biggest_y = big_y;


    }

    public SketchSelection(Sketch first_sketch)
    {
        sketches = new ArrayList<Sketch>(100);

        sketches.add(first_sketch);

        calculateSelectionDimensions();
    }

    public void addSketch(Sketch sk)
    {
        sketches.add(sk);
        calculateSelectionDimensions();
    }

    public void removeSketch(int index)
    {
        sketches.remove(index);
        calculateSelectionDimensions();
    }

    public int getSketchCount()
    {
        return sketches.size();
    }
    public Sketch getSketch(int index)
    {
        return sketches.get(index);
    }


    public boolean boxScreenCollideWithXY(Camera camera, float x, float y)
    {
        if (x >= getXScreen(camera) && x <= getXScreen(camera) + getWidthScreen(camera) && y >= getYScreen(camera) && y <= getYScreen(camera) + getHeightScreen(camera))
        {
            return true;
        }
        return false;
    }

    public int lineScreenCollideWithCircle(Camera camera, float x, float y, float radius)
    {
        for (int i = 0; i < sketches.size(); ++i)
        {
            if (sketches.get(i).lineScreenCollideWithCircle(camera, x, y, radius))
            {
                return i;
            }
        }
        
        return -1;
        
    }

    public float getXScreen(Camera camera)
    {
        return camera.worldToScreenX(smallest_x);
    }
    public float getYScreen(Camera camera)
    {
        return camera.worldToScreenY(smallest_y);
    }
    public float getWidthScreen(Camera camera)
    {
        return camera.worldToScreenX(biggest_x) - camera.worldToScreenX(smallest_x);
    }
    public float getHeightScreen(Camera camera)
    {
        return camera.worldToScreenY(biggest_y) - camera.worldToScreenY(smallest_y);
    }

   public void move(Camera camera, float dx, float dy)
    {
        for (Sketch s : sketches)
        {
            s.move(dx, dy);
        }

        calculateSelectionDimensions();
    }

    public void draw(Graphics g, Camera camera)
    {
        for (Sketch s : sketches)
        {
            s.draw(g, camera);
        }

    }

}
