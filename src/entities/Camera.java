package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderEngine.DisplayManager;
import toolBox.Maths;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
  
  private Vector3f position = new Vector3f(0, 0, 0);
  private float pitch = 0;
  private float yaw = 0;
  private float roll = 0;
  private float currentSpeed = 0;
  
  private static final float MAX_SPEED = 95;
  private static final float MAX_PITCH = 87;
  private static final float SENSITIVITY = 0.3f;
  
  private float FOV;
  private float NEAR_PLANE;
  private float FAR_PLANE;
  
  private Matrix4f projectionMatrix;
  
  public Camera(float FOV, float NEAR_PLANE, float FAR_PLANE) {
    this.FOV = FOV;
    this.NEAR_PLANE = NEAR_PLANE;
    this.FAR_PLANE = FAR_PLANE;
    this.projectionMatrix = Maths.createProjectionMatrix(FOV, NEAR_PLANE, FAR_PLANE);
  }
  
  public void checkForUserInput() {
    pitch += (DisplayManager.getDeltaCursor().y * SENSITIVITY);
    pitch = Float.min(Float.max(pitch, -MAX_PITCH), MAX_PITCH);
    yaw += (DisplayManager.getDeltaCursor().x * SENSITIVITY);
    
    Vector3f travel = new Vector3f(0, 0, 0);
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_W)) {
      travel.add(new Vector3f(1, 0, 0));
      currentSpeed = MAX_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_S)) {
      travel.add(new Vector3f(-1, 0, 0));
      currentSpeed = MAX_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_D)) {
      travel.add(new Vector3f(0, 0, 1));
      currentSpeed = MAX_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_A)) {
      travel.add(new Vector3f(0, 0, -1));
      currentSpeed = MAX_SPEED;
    }
    
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_SPACE)) {
      travel.add(new Vector3f(0, 1, 0));
      currentSpeed = MAX_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
      travel.add(new Vector3f(0, -1, 0));
      currentSpeed = MAX_SPEED;
    }
    
    if (travel.length() != 0) {
      calculateMovement(travel);
    }
  }
  
  private void calculateMovement(Vector3f travel) {
    float speed = currentSpeed * (float)DisplayManager.getDeltaTime();
    
    Vector3f forward = new Vector3f();
    forward.x = (float)(speed * Math.sin(Math.toRadians(yaw)));
    forward.z = (float)(speed * Math.cos(Math.toRadians(yaw)));
    
    Vector3f horizontal = new Vector3f();
    horizontal.x = (float)(speed * Math.sin(Math.toRadians(yaw + 90)));
    horizontal.z = (float)(speed * Math.cos(Math.toRadians(yaw + 90)));
    
    Vector3f deltaPos = new Vector3f(0, 0, 0);
    deltaPos.x = (forward.x * travel.x) + (horizontal.x * travel.z);
    deltaPos.z = -((forward.z * travel.x) + (horizontal.z * travel.z));
    deltaPos.y = (speed * travel.y);
    deltaPos.normalize(speed, deltaPos);
    
    position.add(deltaPos);
    
    currentSpeed = 0;
  }
  
  public Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }
  
  public void setFOV(float FOV) {
    this.FOV = FOV;
    this.projectionMatrix = Maths.createProjectionMatrix(FOV, NEAR_PLANE, FAR_PLANE);
  }
  
  public float getFOV() {
    return this.FOV;
  }
  
  public void setPosition(Vector3f newPos) {
    this.position = newPos;
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
  
  public void setPitch(float pitch) {
    this.pitch = pitch;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public void setRoll(float roll) {
    this.roll = roll;
  }
  
}
