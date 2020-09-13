package lights;

import org.joml.Vector3f;

public class PointLight {
  
  private Vector3f position;
  private Vector3f color;
  private Vector3f attenuation;
  
  public PointLight(Vector3f position, Vector3f color) {
    this.position = position;
    this.color = color;
    this.attenuation = new Vector3f(1, 0, 0);
  }
  
  public PointLight(Vector3f position, Vector3f color, Vector3f attenuation) {
    this.position = position;
    this.color = color;
    this.attenuation = attenuation;
  }
  
  public Vector3f getAttenuation() {
    return attenuation;
  }
  
  public Vector3f getPosition() {
    return position;
  }
  
  public void setPosition(Vector3f position) {
    this.position = position;
  }
  
  public Vector3f getColor() {
    return color;
  }
  
  public void setColor(Vector3f color) {
    this.color = color;
  }
}
