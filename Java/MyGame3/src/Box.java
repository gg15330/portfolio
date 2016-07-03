import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

//Box object -
//I didn't have time to implement this in the end
class Box extends Entity {

    private PolygonShape ps;

    public Box(World world, float x, float y, float width, float height) throws Exception {
        super(x, y);
        bd.type = BodyType.DYNAMIC;
        ps = new PolygonShape();

        //set vertices explicitly to avoid issues with point of origin in Displayold class
        Vec2[] vertices = {
                new Vec2(width, 0.0f),
                new Vec2(width, height),
                new Vec2(0.0f, height),
                new Vec2(0.0f, 0.0f)
        };

        ps.set(vertices, vertices.length);
        setFixture(ps, 0.8f, 0.3f, 0.8f);
        bd.angle = 2.0f;
        addToWorld(world);
    }

    @Override
    public Vec2[] vertices() { return ps.getVertices(); }

    @Override
    public float radius() { throw new Error("Attempting to get radius for non-Ball object."); }

}
