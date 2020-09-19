package entities;

import models.Model;
import org.joml.Vector3f;

public class Entity {
  private Vector3f position;
  private Model model;
  private float rotX, rotY, rotZ;
  private float scale;
  
  public Entity(Model model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    this.model = model;
    this.position = position;
    this.rotX = rotX;
    this.rotY = rotY;
    this.rotZ = rotZ;
    this.scale = scale;
  }
  
  public void increasePosition(float dx, float dy, float dz) {
    this.position.x += dx;
    this.position.y += dy;
    this.position.z += dz;
  }
  
  public void increasePosition(Vector3f dPos) {
    this.position.add(dPos);
  }
  
  public void increaseRotation(float dx, float dy, float dz) {
    this.rotX += dx;
    this.rotY += dy;
    this.rotZ += dz;
  }
  
  public Vector3f getPosition() {
    return position;
  }
  
  public void setPosition(Vector3f position) {
    this.position = position;
  }
  
  public Model getModel() {
    return this.model;
  }
  
  public void setModel(Model model) {
    this.model = model;
  }
  
  public float getRotX() {
    return rotX;
  }
  
  public void setRotX(float rotX) {
    this.rotX = rotX;
  }
  
  public float getRotY() {
    return rotY;
  }
  
  public void setRotY(float rotY) {
    this.rotY = rotY;
  }
  
  public float getRotZ() {
    return rotZ;
  }
  
  public void setRotZ(float rotZ) {
    this.rotZ = rotZ;
  }
  
  public float getScale() {
    return scale;
  }
  
  public void setScale(float scale) {
    this.scale = scale;
  }
}
