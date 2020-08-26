package entitites;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private Airplane airplane;
    private float distanceFromAirplane = 50;
    private float angleAroundAirplane = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll;


    public Camera(Airplane airplane) {
        this.airplane = airplane;
    }

    public void move() {

        calculateAngleAroundAirplane();
        calculatePitch();
        calculateZoom();
        float horizontialDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(verticalDistance, horizontialDistance);
        this.yaw = 180 - (airplane.getRotY() + angleAroundAirplane);


    }
    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromAirplane -= zoomLevel;
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundAirplane() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundAirplane -= angleChange;
        }
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromAirplane * Math.sin(Math.toRadians(pitch)));
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromAirplane * Math.cos(Math.toRadians(pitch)));
    }

    private void calculateCameraPosition(float verticalDistance, float horizontialDistance) {
        float theta = airplane.getRotY() + angleAroundAirplane;
        float offsetX = (float) (horizontialDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontialDistance * Math.cos(Math.toRadians(theta)));

        position.x = airplane.getPosition().x - offsetX;
        position.z = airplane.getPosition().z - offsetZ;

        position.y = airplane.getPosition().y + verticalDistance;
    }
}
