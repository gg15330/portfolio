/**
 * Created by george on 13/04/16.
 */
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;
import javax.swing.*;

//creates display and physics world,
//sets timer tasks for repainting
public class Display extends JPanel implements Observer {

    private final float SCALE = 10.0f;
    private final int REPAINT_SPEED = 1000 / 60;

    private Physics physics;
    private TimerTask painttask;

    public Display(int width, int height, int num_balls) throws Exception {
        RunningFlag running = new RunningFlag(true);
        running.addObserver(this);
        physics = new Physics(toWorld(width), toWorld(height), num_balls, running);

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.DARK_GRAY);
        setRepaintTask();
        addMouseMotionListener(new Mouser());
    }

    //observes the RunningFlag -
    //stops painttask and shows score if false
    @Override
    public void update(Observable o, Object arg) {
        System.out.println(SwingUtilities.isEventDispatchThread());
        if(arg.equals(false)) {
            painttask.cancel();
            gameOver(physics.score());
        }
    }

    //display score on game over
    private void gameOver(int score) {
        JLabel scorelabel = new JLabel("Score: " + score + "    Thanks for playing!!!");
        scorelabel.setForeground(Color.CYAN);
        add(scorelabel);
        setBorder(BorderFactory.createEmptyBorder(getHeight()/2, 0, 0, 0));
        scorelabel.setHorizontalTextPosition(SwingConstants.CENTER);
        scorelabel.setVerticalAlignment(SwingConstants.CENTER);
        scorelabel.setVisible(true);
    }

    private void setRepaintTask() {
        //repaint at 60fps
        painttask = new PaintTask();
        java.util.Timer painttimer = new java.util.Timer();
        painttimer.scheduleAtFixedRate(painttask, 0, REPAINT_SPEED);
    }

    //initially tried downcasting list of entities
    //but this was too expensive
    @Override
    public void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setColor(Color.ORANGE);

        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        //could have used while(world.next()) {}
        for(Wall p : physics.walls()) { polygon(g, p); }
        for(Ball b : physics.balls()) { ball(g, b); }
        polygon(g, physics.platform());
    }

    private void ball(Graphics2D g, Ball b) {
        Vec2 pos = b.body().getPosition();
        int x = toDisplay(pos.x);
        int y = toDisplay(pos.y);
        int rad = toDisplay(b.radius());
        g.fillOval(x-rad, y-rad, rad*2, rad*2);
        Toolkit.getDefaultToolkit().sync();
    }

    private void polygon(Graphics2D g, Wall w) {
        int[] xcoords = positionToCoords(w, 'x');
        int[] ycoords = positionToCoords(w, 'y');
        g.fillPolygon(xcoords, ycoords, xcoords.length);
        Toolkit.getDefaultToolkit().sync();
    }

    //convert entity position to co-ordinate array for drawing
    private int[] positionToCoords(Entity e, char c) {
        Vec2 pos = e.body().getPosition();
        Vec2[] vertices = e.vertices();
        int[] coords = new int[e.vertices().length];

        if(c == 'x') {
            for(int i = 0; i < vertices.length; i++) {
                coords[i] = Math.round(toDisplay(vertices[i].x + pos.x));
            }
        }
        else if(c == 'y') {
            for(int i = 0; i < vertices.length; i++) {
                coords[i] = Math.round(toDisplay(vertices[i].y + pos.y));
            }
        }
        else { throw new Error("Unrecognised co-ordinate - not x or y."); }

        return coords;
    }

    //helper functions
    private float toWorld(int a) { return a/SCALE; }
    private int toDisplay(float a) { return Math.round(a*SCALE); }


    private class PaintTask extends TimerTask {
        @Override
        public void run() {
            repaint();
        }
    }

    //detects mouse movement and sets the platform position
    private class Mouser implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                physics.setPlatformPos(e.getX(), SCALE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            try {
                physics.setPlatformPos(e.getX(), SCALE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    //Tests
    private static int tests;
    private static Display testdisplay;
    private static Wall testwall;

    public static void main(String[] args) {
        setup();
        test_constructor();
        System.out.println("Tests passed: " + tests);
    }

    private static void setup() {
        try {
            testdisplay = new Display(480, 640, 3);
            testwall = new Wall(new World(new Vec2(0.0f, 10.0f)),
                    5.0f, 5.0f, 1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test_constructor() {
        is(new Dimension(480, 640), testdisplay.getPreferredSize());
        is(Color.DARK_GRAY, testdisplay.getBackground());
        is(1, testdisplay.getMouseMotionListeners().length);

        is(60, testdisplay.positionToCoords(testwall, 'x')[0]);
        is(60, testdisplay.positionToCoords(testwall, 'x')[1]);
        is(50, testdisplay.positionToCoords(testwall, 'x')[2]);
        is(50, testdisplay.positionToCoords(testwall, 'x')[3]);

        is(50, testdisplay.positionToCoords(testwall, 'y')[0]);
        is(60, testdisplay.positionToCoords(testwall, 'y')[1]);
        is(60, testdisplay.positionToCoords(testwall, 'y')[2]);
        is(50, testdisplay.positionToCoords(testwall, 'y')[3]);

        is(3.4f, testdisplay.toWorld(34));
        is(0.0f, testdisplay.toWorld(0));
        is(9384294.0f, testdisplay.toWorld(93842934));
        is(43, testdisplay.toDisplay(4.3f));
        is(-43, testdisplay.toDisplay(-4.3f));
    }

    private static void is(Object x, Object y) {
        tests++;
        if (x == y) return;
        if (x != null && x.equals(y)) return;
        throw new Error("Test failed: " + x + ", " + y);
    }

}
