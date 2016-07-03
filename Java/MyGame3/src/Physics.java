/**
 * Created by george on 18/04/16.
 */

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.*;

public class Physics {

    private final float IMPULSE_MULTIPLIER = 30.0f;
    private final int MAXTRIES = 50;
    private final float HORIZONTAL_GRAV = 0.0f;
    private final float VERTICAL_GRAV = 10.0f;
    private final int UPDATE_SPEED = 2;
    private final int SCORE_INTERVAL = 1000 / UPDATE_SPEED;
    private final float WORLD_STEP = 1.0f / 240.0f;
    private final int VEL_ITERATIONS = 8;
    private final int POS_ITERATIONS = 3;

    private float BALL_HEIGHT;
    private float worldheight, worldwidth;
    private int score, updatecount;

    private World world;

    //I think it is less computationally expensive/simpler to have separate lists for
    //each entity type than use instanceof with a general list of entities
    private List<Ball> balls;
    private List<Box> boxes;
    private List<Wall> walls;
    private Wall platform;
    private RunningFlag running;

    public Physics(float worldwidth, float worldheight, int num_balls, RunningFlag running) throws Exception {
        if(worldwidth <= 0.0f || worldheight <= 0.0f) {
            throw new IndexOutOfBoundsException("World dimensions must be greater than 0.");
        }

        this.running = running;
        this.worldwidth = worldwidth;
        this.worldheight = worldheight;
        score = 0;
        updatecount = 0;
        BALL_HEIGHT = worldheight/4;
        balls = new ArrayList<Ball>();
        boxes = new ArrayList<Box>();
        walls = new ArrayList<Wall>();
        world = new World(new Vec2(HORIZONTAL_GRAV, VERTICAL_GRAV));
        addEntities(worldwidth, worldheight, num_balls);

        //how fast do we want the simulation to run?
        TimerTask updatetask = new UpdateTask();
        java.util.Timer updatetimer = new java.util.Timer();
        updatetimer.scheduleAtFixedRate(updatetask, 0, UPDATE_SPEED);
    }

    //populate the world with entities
    private void addEntities(float worldwidth, float worldheight, int num_balls) throws Exception {
        Ball ball;
        for(int i = 0; i < num_balls; i++) {
            ball = new Ball(world, (i+1)*10, BALL_HEIGHT, 1);
            Vec2 impulse = randomVec2();
            ball.body().applyLinearImpulse(impulse, ball.body().getWorldCenter());
            balls.add(ball);
        }

        Wall wall = new Wall(world, 0, 0, 1, worldheight-5);
        walls.add(wall);
        wall = new Wall(world, worldwidth-1, 0, 1, worldheight-5);
        walls.add(wall);
        wall = new Wall(world, 1, 0, worldwidth-2, 1);
        walls.add(wall);

        platform = new Wall(world, (worldwidth/2)-(worldwidth/10), worldheight-3, worldwidth/5, 1);

        System.out.println("Entity count: " + world.getBodyCount());
    }

    //create a random starting vector for a ball.
    //x coordinate can be positive or negative,
    //y must be negative
    private Vec2 randomVec2() throws Exception {
        int maxtries = 0;
        while(maxtries < MAXTRIES) {
            Random r = new Random();
            float x = ((r.nextFloat()*2) - 1)*IMPULSE_MULTIPLIER;
            float y = r.nextFloat()*IMPULSE_MULTIPLIER*(-1);
            if(!validFloat(x) || !validFloat(y)) {
                maxtries++;
                System.out.println("Regenerating float...");
            }
            else {
                return new Vec2(x, y);
            }
        }
        throw new Error("Unable to generate valid float.");
    }

    //set new platform position based on mouse position
    public void setPlatformPos(float x, float scale) {
        float centerx = platform.center().x * scale;
        float newx = (x - centerx)/scale;
        float lowerbound = 0.0f - (centerx/scale);
        float upperbound = worldwidth + (centerx/scale);

        //do nothing if platform goes out of bounds
        if((newx <= lowerbound) || (newx >= upperbound)) { return; }

        try {
            float newy = platform.body().getPosition().y;
            platform.body().setTransform(new Vec2(newx, newy), 0.0f);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    //returns false if f is greater than the impulse multiplier,
    //is not a number or is infinite
    private boolean validFloat(float f) {
        return(f < IMPULSE_MULTIPLIER
                && f > (IMPULSE_MULTIPLIER*(-1))
                && !Float.isNaN(f)
                && !Float.isInfinite(f));
    }

    public World world() { return world; }
    public List<Ball> balls() { return balls; }
    public List<Box> boxes() { return boxes; }
    public List<Wall> walls() { return walls; }
    public Wall platform() { return platform; }
    public int score() { return score; }


    private class UpdateTask extends TimerTask {
        //steps through the simulation, checks game state and updates score
        @Override
        public void run() {
            try {
                //world.step() occasionally throws an ArrayIndexOutOfBoundsException -
                //I don't know why
                world.step(WORLD_STEP, VEL_ITERATIONS, POS_ITERATIONS);
                world.clearForces();
            } catch (Exception e) {
                e.printStackTrace();
            }

            checkGameOver();
            updateScore();
        }

        //checks all dynamic body (Ball) positions,
        //stops game if ball falls off screen
        private void checkGameOver() {
            for(Ball b : balls) {
                float y = b.body().getPosition().y;
                if(y > worldheight) {
                    running.setValue(false);
                    this.cancel();
                }
            }
        }

        private void updateScore() {
            updatecount++;
            if(updatecount % SCORE_INTERVAL == 0) {
                score++;
                updatecount = 0;
            }
        }
    }


    //Tests
    private static int tests;
    private static Physics testphysics;
    private static World testworld;
    private static Vec2[] testvec;
    private static Wall testplatform;

    public static void main(String[] args) {
        setup();
        test_constructor();
        System.out.println("Tests passed: " + tests);
    }

    private static void setup() {
        tests = 0;
        testvec = new Vec2[] {
                new Vec2(1.0f, 0.0f),
                new Vec2(1.0f, 64.0f),
                new Vec2(0.0f, 64.0f),
                new Vec2(0.0f, 0.0f)
        };
        testworld = new World(new Vec2(0.0f, 10.0f));

        try {
            testplatform = new Wall(testworld, 5.0f, 4.0f, 48.0f/5.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test_constructor() {
        try {
            testphysics = new Physics(48.0f, 64.0f, 2, new RunningFlag(false));
        } catch (Exception e) {
            e.printStackTrace();
        }

        is(new Vec2(0.0f, 10.0f), testphysics.world().getGravity());

        is(1.0f, testphysics.balls().get(0).radius());
        is(1.0f, testphysics.balls().get(1).radius());

        is(new Vec2(1.0f, 0.0f), testphysics.walls().get(0).vertices()[0]);
        is(new Vec2(1.0f, 59.0f), testphysics.walls().get(0).vertices()[1]);
        is(new Vec2(0.0f, 59.0f), testphysics.walls().get(0).vertices()[2]);
        is(new Vec2(0.0f, 0.0f), testphysics.walls().get(0).vertices()[3]);
        is(3, testphysics.walls().size());

        is(testplatform.vertices()[0], testphysics.platform().vertices()[0]);
        is(testplatform.vertices()[1], testphysics.platform().vertices()[1]);
        is(testplatform.vertices()[2], testphysics.platform().vertices()[2]);
        is(testplatform.vertices()[3], testphysics.platform().vertices()[3]);

        is(true, testphysics.validFloat(0.0f));
        is(true, testphysics.validFloat(10.454f));
        is(false, testphysics.validFloat(31.0f));
        is(false, testphysics.validFloat(-41.0f));
        is(true, testphysics.validFloat(-1.0f));
        is(false, testphysics.validFloat('f'));
        is(true, testphysics.validFloat(2));
    }

    private static void is(Object x, Object y) {
        tests++;
        if (x == y) return;
        if (x != null && x.equals(y)) return;
        throw new Error("Test failed: " + x + ", " + y);
    }

}
