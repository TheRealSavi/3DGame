package entities;

import models.Model;
import org.joml.Vector3f;
import renderEngine.DisplayManager;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
  
  private static final float MAX_PITCH = 87;
  private static final float SENSITIVITY = 0.3f;
  private static final float WALK_SPEED = 95;
  private static final float RUN_MODIFIER = 2.0f;
  
  private final Camera camera;
  private float currentSpeed = 0;
  
  public Player(Model model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(model, position, rotX, rotY, rotZ, scale);
    
    camera = new Camera(90, 1, 3500);
    camera.setPosition(position);
    camera.setPitch(rotX);
    camera.setYaw(rotY);
    camera.setRoll(rotZ);
  }
  
  public void checkForUserInput() {
    camera.setPitch(camera.getPitch() + DisplayManager.getDeltaCursor().y * SENSITIVITY);
    camera.setPitch(Float.min(Float.max(camera.getPitch(), -MAX_PITCH), MAX_PITCH));
    
    camera.setYaw(camera.getYaw() + DisplayManager.getDeltaCursor().x * SENSITIVITY);
    
    Vector3f travel = new Vector3f(0, 0, 0);
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_W)) {
      travel.add(new Vector3f(1, 0, 0));
      currentSpeed = WALK_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_S)) {
      travel.add(new Vector3f(-1, 0, 0));
      currentSpeed = WALK_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_D)) {
      travel.add(new Vector3f(0, 0, 1));
      currentSpeed = WALK_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_A)) {
      travel.add(new Vector3f(0, 0, -1));
      currentSpeed = WALK_SPEED;
    }
    
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_SPACE)) {
      travel.add(new Vector3f(0, 1, 0));
      currentSpeed = WALK_SPEED;
    }
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
      travel.add(new Vector3f(0, -1, 0));
      currentSpeed = WALK_SPEED;
    }
    
    if (DisplayManager.getInput().isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
      currentSpeed *= RUN_MODIFIER;
    }
    
    
    if (travel.length() != 0) {
      calculateMovement(travel);
    }
  }
  
  private void calculateMovement(Vector3f travel) {
    float speed = currentSpeed * (float)DisplayManager.getDeltaTime();
    
    Vector3f forward = new Vector3f();
    forward.x = (float)(speed * Math.sin(Math.toRadians(camera.getYaw())));
    forward.z = (float)(speed * Math.cos(Math.toRadians(camera.getYaw())));
    
    Vector3f horizontal = new Vector3f();
    horizontal.x = (float)(speed * Math.sin(Math.toRadians(camera.getYaw() + 90)));
    horizontal.z = (float)(speed * Math.cos(Math.toRadians(camera.getYaw() + 90)));
    
    Vector3f deltaPos = new Vector3f(0, 0, 0);
    deltaPos.x = (forward.x * travel.x) + (horizontal.x * travel.z);
    deltaPos.z = -((forward.z * travel.x) + (horizontal.z * travel.z));
    deltaPos.y = (speed * travel.y);
    deltaPos.normalize(speed, deltaPos);
    
    super.increasePosition(deltaPos);
    camera.increasePosition(deltaPos);
    
    currentSpeed = 0;
  }
  
  public Camera getCamera() {
    return camera;
  }
}
