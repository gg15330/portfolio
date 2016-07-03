import java.util.Observable;

/**
 * Created by george on 04/05/16.
 */
public class RunningFlag extends Observable {
    private boolean running;

    public RunningFlag(boolean running) {
        this.running = running;
    }

    public void setValue(boolean running) {
        this.running = running;
        setChanged();
        notifyObservers(running);
    }
}