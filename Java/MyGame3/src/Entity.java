import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

//contains physics body and fixture info, position,
//weak reference to world object
public abstract class Entity {

    public abstract float radius();
    public abstract Vec2[] vertices();

    protected BodyDef bd;
    private Body body;
    private FixtureDef fd;

    public Entity(float x, float y) throws Exception {
        if((x < 0.0f) || (y < 0.0f)) {
            throw new IndexOutOfBoundsException("Entity XY co-ordinates must be greater than 0.");
        }
        if(Float.isInfinite(x) || Float.isInfinite(y)) {
            throw new NumberFormatException("Infinite value for Entity XY co-ordinate.");
        }
        if(Float.isNaN(x) || Float.isNaN(y)) {
            throw new NumberFormatException("NaN value for Entity XY co-ordinate.");
        }

        bd = new BodyDef();
        bd.position.set(x, y);
    }

    //setup entity properties
    protected void setFixture(Shape s, float dens, float fric, float rest) {
        fd = new FixtureDef();
        fd.shape = s;
        fd.density = dens;
        fd.friction = fric;
        fd.restitution = rest;
    }

    protected void addToWorld(World world) {
        body = world.createBody(bd);
        body.createFixture(fd);
    }

    public Body body() { return body; }
    public Shape shape() { return fd.shape; }
    public float density() { return fd.density; }
    public float friction() { return fd.friction; }
    public float restitution() { return fd.restitution; }

}
