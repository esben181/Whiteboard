import org.newdawn.slick.Graphics;
import org.newdawn.slick.GameContainer;

public abstract class State {


    public abstract void onInit(GameContainer container);

    public abstract void onUpdate(GameContainer container, int delta);

    public abstract void onRender(GameContainer container, Graphics g);

    public abstract void onCleanUp(GameContainer container);

    public abstract void mouseWheelMove(int d);
    

}
