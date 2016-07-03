import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

class Ball extends Entity {

    private CircleShape cs;

    Ball(World world, float x, float y, float radius) throws Exception {
        super(x, y);

        if(radius <= 0.0f) {
            throw new IndexOutOfBoundsException("Ball radius must be greater than 0.");
        }
        if(Float.isInfinite(radius) || Float.isNaN(radius)) {
            throw new Exception("Invalid value for ball radius.");
        }

        bd.type = BodyType.DYNAMIC;

        //fixture
        cs = new CircleShape();
        cs.setRadius(radius);
        setFixture(cs, 0.6f, 0.3f, 1.0f);

        addToWorld(world);
    }

    @Override
    public float radius() { return cs.getRadius(); }

    @Override
    public Vec2[] vertices() { throw new Error("Attempting to fetch vertices for non-polygon object"); }

    //Tests
    private static int tests;
    private static Ball testball;
    private static World testworld;

    public static void main(String[] args) {
        setup();
        test_constructor();
        System.out.println("Tests passed: " + tests);
    }

    private static void setup() {
        tests = 0;
        testworld = new World(new Vec2(0.0f, 10.0f));
    }

    private static void test_constructor() {
        try {
            testball = new Ball(testworld, 9.0f, 18.0f, 1.453f);
            is(BodyType.DYNAMIC, testball.body().getType());
            is(ShapeType.CIRCLE, testball.shape().getType());
            is(0.6f, testball.density());
            is(0.3f, testball.friction());
            is(1.0f, testball.restitution());
            System.out.println(testball.radius());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void is(Object x, Object y) {
        tests++;
        if (x == y) return;
        if (x != null && x.equals(y)) return;
        throw new Error("Test failed: " + x + ", " + y);
    }
}
