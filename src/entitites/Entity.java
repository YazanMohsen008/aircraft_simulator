package entitites;

import Models.TexturedModel;
import Physics.MathVector;
import org.lwjgl.util.vector.Vector3f;

public class Entity {

    private   TexturedModel model;
    private MathVector Position;



    private float rotX, rotY, rotZ;
    private float scale;

    public Entity(TexturedModel model, MathVector position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.Position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public void increasePosition(float dx, float dy, float dz) {
        this.Position.x += dx;
        this.Position.y += dy;
        this.Position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public TexturedModel getModel() {
        return model;
    }

    public MathVector getPosition() {
        return Position;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getScale() {
        return scale;
    }
}

