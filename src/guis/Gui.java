package guis;

import org.joml.Vector2f;

public class Gui {
  private final int textureID;
  private Vector2f position;
  private Vector2f scale;
  
  public Gui(int textureID, Vector2f position, Vector2f scale) {
    this.textureID = textureID;
    this.position = position;
    this.scale = scale;
  }
  
  public int getTextureID() {
    return textureID;
  }
  
  public Vector2f getPosition() {
    return position;
  }
  
  public void setPosition(Vector2f position) {
    this.position = position;
  }
  
  public Vector2f getScale() {
    return scale;
  }
  
  public void setScale(Vector2f scale) {
    this.scale = scale;
  }
}
