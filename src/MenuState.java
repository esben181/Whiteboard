import org.newdawn.slick.*;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;


enum Tool
{
    PENCIL,
    SELECTION,
}

public class MenuState  extends State {
 

    private final double POINT_OPTIMIZATION_RADIUS = 5.0;
    private final float CAMERA_ZOOM_STEP = 0.1f;
    private final float CAMERA_PAN_SPEED = 0.05f;

    private final int CONSECUTIVE_SKETCH_THRESHOLD = 16*60*2;

    int time_since_last_sketch;
    boolean combine;

    private ArrayList<Float> assembly_vertices;
    private ArrayList<SketchSelection> groups;


    private boolean drawing;

    private SketchSelection selection;

    private int hover_group_index;

    private Camera camera;

    Circle eraser;

    private boolean dragging;
    private float drag_start_x;
    private float drag_start_y;

    private int prev_mouse_x;
    private int prev_mouse_y;


    private Color[] color_palette = { 
        Color.black,
        Color.red,
        Color.blue,
        Color.yellow,
        Color.green,
    };
    private Color brush_color;
    private Tool tool;
    

    public void onInit(GameContainer container)
    {
        groups = new ArrayList<SketchSelection>(100);
        hover_group_index = -1;

        assembly_vertices = new ArrayList<Float>(1000);


        drawing = false;
        camera = new Camera(0, 0, 1.0f);
        dragging = false;

        eraser = new Circle(0, 0, 10.0f);
        time_since_last_sketch = 0;
        combine = false;
        brush_color = Color.black;
        tool = Tool.PENCIL;
    }

    public  void onUpdate(GameContainer container, int delta)
    {
        Input input = container.getInput();

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
        {
            
            if (dragging == false)
            {
                dragging = true;
                drag_start_x = camera.screenToWorldX(input.getMouseX());
                drag_start_y = camera.screenToWorldY(input.getMouseY());
            }
        }
        else
        {
            dragging = false;
        }
       
    
        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
        {
            if (input.isKeyDown(Input.KEY_SPACE) && dragging)
            {
                float dx = drag_start_x - camera.screenToWorldX(input.getMouseX());
                float dy = drag_start_y - camera.screenToWorldY(input.getMouseY());
                camera.move(dx*CAMERA_PAN_SPEED*delta, dy*CAMERA_PAN_SPEED*delta);
            }
            else if (selection != null && dragging)
            {
                selection.move(camera, camera.screenToWorldX(input.getMouseX()) - camera.screenToWorldX(prev_mouse_x), camera.screenToWorldY(input.getMouseY()) - camera.screenToWorldY(prev_mouse_y));
            }
            else if (tool == Tool.PENCIL)
            {
                drawing = true;

                float x_coord = camera.screenToWorldX(input.getMouseX());
                float y_coord = camera.screenToWorldY(input.getMouseY());
                
                assembly_vertices.add(x_coord);
                assembly_vertices.add(y_coord);

                if (time_since_last_sketch <= CONSECUTIVE_SKETCH_THRESHOLD && groups.size() > 0)
                {
                    combine = true;
                }

            }
        }
        else if (drawing == true)
        {


            optimizeVertices(assembly_vertices);

            Sketch new_sketch = new Sketch(assembly_vertices, brush_color);
            if (combine)
            {
                groups.get(groups.size()-1).addSketch(new_sketch);
            }
            else
            {
                groups.add(new SketchSelection(new_sketch));
            }

            assembly_vertices.clear();

            drawing = false;

            time_since_last_sketch = 0;
            combine = false;
        }


        if (input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON))
        {
           for (int i = groups.size()-1; i >= 0; --i)
           {
               if (groups.get(i).boxScreenCollideWithXY(camera, input.getMouseX(), input.getMouseY()))
               {
                    int index = groups.get(i).lineScreenCollideWithCircle(camera, input.getMouseX(), input.getMouseY(), eraser.radius);
                    if (index != -1)
                    {
                        groups.get(i).removeSketch(index);
                        if (groups.get(i).getSketchCount() <= 0)
                        {
                           if (i == hover_group_index) hover_group_index = -1;
                           groups.remove(i);
                        }
                    }
               }
           }
        }

        if (tool == Tool.SELECTION)
        {
            for (int i = groups.size()-1; i >= 0; --i)
            {
                if (groups.get(i).boxScreenCollideWithXY(camera, input.getMouseX(), input.getMouseY()))
                {
                    if (hover_group_index == i && input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
                    {
                        selection = groups.get(i);
                    }                      

                    int index = groups.get(i).lineScreenCollideWithCircle(camera, input.getMouseX(), input.getMouseY(), eraser.radius);
                    if (index != -1)
                    {
                        hover_group_index = i;
                        
                    }
                }
                else if (hover_group_index == i)
                {
                    hover_group_index = -1;
                    if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
                    {
                        selection = null;
                    }
                }
            }

        }


        if (input.isKeyPressed(Input.KEY_P))
        {
            tool = Tool.PENCIL;
            for (int i = 0; i < color_palette.length; ++i) {

                if (input.getMouseX() >= 10 && input.getMouseX() <= 10 + 35 && input.getMouseY() >= 30 + (35 + 8) * i
                        && input.getMouseY() <= 30 + (35 + 8) * i + 35) {
                    brush_color = color_palette[i];
                }

            }

        }
        else if (input.isKeyPressed((Input.KEY_S)))
        {
            tool = Tool.SELECTION;
            time_since_last_sketch = CONSECUTIVE_SKETCH_THRESHOLD;
            selection = null;
        }


        time_since_last_sketch += delta;
        prev_mouse_x = input.getMouseX();
        prev_mouse_y = input.getMouseY();
    }

    public void mouseWheelMove(int d)
    {
        Input input = Engine.getContainer().getInput();

        if (d >= 120) 
        {
            camera.zoom((float)input.getMouseX(), (float)input.getMouseY(), (camera.getZoomLevel())*CAMERA_ZOOM_STEP);
        }
        else
        {
            camera.zoom((float)input.getMouseX(), (float)input.getMouseY(), (camera.getZoomLevel())*-CAMERA_ZOOM_STEP);
        }
    }

    public void onRender(GameContainer container, Graphics g)
    {
        Input input = container.getInput();
        
        for (SketchSelection s : groups)
        {
            s.draw(g, camera);
        }

        if (tool == Tool.SELECTION)
        {
            if (selection != null)
            {
                g.setColor(new Color(0, 0, 255));
                g.draw(new Rectangle(selection.getXScreen(camera), selection.getYScreen(camera), selection.getWidthScreen(camera), selection.getHeightScreen(camera)));
            }
            if (hover_group_index != -1 && !groups.get(hover_group_index).equals(selection) && !dragging)
            {
                g.setColor(Color.black);
                g.draw(new Rectangle(groups.get(hover_group_index).getXScreen(camera), groups.get(hover_group_index).getYScreen(camera), groups.get(hover_group_index).getWidthScreen(camera), groups.get(hover_group_index).getHeightScreen(camera)));
            }
        }

        g.setColor(Color.black);
        g.setLineWidth(1.0f);
        eraser.setCenterX(input.getMouseX());
        eraser.setCenterY(input.getMouseY());
        g.draw(eraser);

        for (int i = 0; i < color_palette.length; ++i)
        {
            
           g.setColor(color_palette[i]);
            g.fill(new Rectangle(10, 30+(8+35)*i, 35, 35));
            if (color_palette[i] == brush_color)
            {
                g.setLineWidth(5.0f);
                g.setColor(Color.gray);
                g.draw(new Rectangle(10, 30+(8+35)*i, 35, 35));
            }
        }

        g.setColor(Color.black);
        g.drawString(String.format("Zoom: %.2f%%", camera.getZoomLevel()*100.0f), 0, 10);

    }

    public void onCleanUp(GameContainer container)
    {
    }

    private void optimizeVertices(ArrayList<Float> vertices)
    {
        if (vertices.size() < 4)
        {
            return;
        }

        int optimized_size = 0;

        vertices.set(optimized_size++, vertices.get(0));
        vertices.set(optimized_size++, vertices.get(1));

        for (int i = 2; i < vertices.size()-2; i+=2)
        {
            float first_x = vertices.get(i);
            float first_y = vertices.get(i+1);
            float avg_x = first_x;
            float avg_y = first_y;
            int count = 1;

            for (int j = i+2; j < vertices.size()-2; j+=2)
            {
                float second_x = vertices.get(j);
                float second_y = vertices.get(j+1);

                if (Math.sqrt(Math.pow(second_x-first_x, 2) + Math.pow(second_y-first_y, 2)) < POINT_OPTIMIZATION_RADIUS)
                {
                    avg_x += second_x;
                    avg_y += second_y;
                    count+=1;
                }
                else
                {
                    vertices.set(optimized_size++, avg_x/count);
                    vertices.set(optimized_size++, avg_y/count);
                    i += (count*2);
                    break;

                }
            }
        }

        vertices.set(optimized_size++, vertices.get(vertices.size()-2));
        vertices.set(optimized_size++, vertices.get(vertices.size()-1));

        for (int i = vertices.size()-1; i >= optimized_size; --i)
        {
            vertices.remove(i);
        }


    }

}
