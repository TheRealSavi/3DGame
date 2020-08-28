package entities;

import models.Model;
import org.joml.Vector3f;
import renderEngine.DisplayManager;

public class Player extends Entity {
  
  private static final float RUN_SPEED = 20;
  private static final float TURN_SPEED = 160;
  
  private float currentSpeed = 0;
  private float currentTurnSpeed = 0;
  
  public Player(Model model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    super(model, position, rotX, rotY, rotZ, scale);
  }
  
  public void move() {
    super.increaseRotation(0, currentTurnSpeed * (float)DisplayManager.getDeltaTime(), 0);
    float distance = currentSpeed * (float)DisplayManager.getDeltaTime();
    float dx = (float)(distance * Math.sin(Math.toRadians(super.getRotY())));
    float dz = (float)(distance * Math.cos(Math.toRadians(super.getRotY())));
    super.increasePosition(dx, 0, dz);
  }
}
