import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

//can be refactored and combined with Box class -
//pass body type as parameter to constructor
class Wall extends Entity {

    private PolygonShape ps;

    Wall(World world, float x, float y, float width, float height) throws Exception {
        super(x, y);

        if(width <= 0.0f || height <= 0.0f) {
            throw new IndexOutOfBoundsException("Wall dimensions must be greater than 0.");
        }
        if(Float.isInfinite(width) || Float.isNaN(height)
        || Float.isInfinite(width) || Float.isNaN(height)) {
            throw new Exception("Invalid wall dimensions.");
        }

        bd.type = BodyType.STATIC;
        ps = new PolygonShape();

        Vec2[] vertices = {
            new Vec2(width, 0.0f),
            new Vec2(width, height),
            new Vec2(0.0f, height),
            new Vec2(0.0f, 0.0f)
        };

        ps.set(vertices, vertices.length);
        setFixture(ps, 1.0f, 0.0f, 0.0f);

        addToWorld(world);
    }

    @Override
    public Vec2[] vertices() { return ps.getVertices(); }

    //workaround - there is a getter for the body's local center but
    //because I am setting the vertices manually only the shape object
    //has a center. All the fields are public but best to use getter/setter methods anyway
    public Vec2 center() { return ps.m_centroid; }

    @Override
    public float radius() { throw new Error("Attempting to get radius for non-Ball object."); }

    //Tests
    private static int tests;
    private static Wall testwall;
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
            testwall = new Wall(testworld, 9.0f, 18.0f, 1.453f, 4.2389f);
            is(BodyType.STATIC, testwall.body().getType());
            is(ShapeType.POLYGON, testwall.shape().getType());
            is(1.0f, testwall.density());
            is(0.0f, testwall.friction());
            is(0.0f, testwall.restitution());
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
