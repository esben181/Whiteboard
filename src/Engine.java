import java.awt.Dimension;
import java.awt.Toolkit;

import org.newdawn.slick.Color;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;


public class Engine extends BasicGame
{
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static private State current_state;
    
    private static AppGameContainer app;

    public static AppGameContainer getContainer() { return app; }
    
    public Engine()
    {
        super("Whiteboard");
    }

    public static Dimension getScreenSize()
    {
        return screenSize;
    }

    public static void main(String[] arguments)
    {
        try
        {
            app = new AppGameContainer(new Engine());
            // app.setDisplayMode(screenSize.width, screenSize.height, true); => Full screen
            app.setDisplayMode(1280, 720, false);
            app.setShowFPS(false); // true for display the numbers of FPS
            app.setVSync(true); // false for disable the FPS synchronize
            app.start();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseWheelMoved(int change)
    {
       current_state.mouseWheelMove(change); 
    }

    static public void changeState(GameContainer container, State new_state)
    {
        current_state.onCleanUp(container);
        current_state = new_state;
        current_state.onInit(container);
    }

    public void init(GameContainer container) throws SlickException
    {
       current_state = new MenuState(); 
       current_state.onInit(container);

    }

    public void update(GameContainer container, int delta) throws SlickException
    {
        current_state.onUpdate(container, delta);
    }


    public void render(GameContainer container, Graphics g) throws SlickException
    {
        g.setBackground(Color.white);
        current_state.onRender(container, g);
    }
}