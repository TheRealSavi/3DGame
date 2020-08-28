package models;

public class Model {
  
  private final int textureID;
  private final RawModel rawModel;
  
  private float shineDamper = 1;
  private float reflectivity = 0;
  
  private boolean hasTransparency = false;
  private boolean useFakeLighting = false;
  
  public Model(int textureID, RawModel rawModel) {
    this.textureID = textureID;
    this.rawModel = rawModel;
  }
  
  public int getTextureID() {
    return this.textureID;
  }
  
  public RawModel getRawModel() {
    return this.rawModel;
  }
  
  public boolean isUsingFakeLighting() {
    return useFakeLighting;
  }
  
  public void setUseFakeLighting(boolean f) {
    useFakeLighting = f;
  }
  
  public boolean isTransparent() {
    return hasTransparency;
  }
  
  public void setTransparency(boolean t) {
    this.hasTransparency = t;
  }
  
  public float getShineDamper() {
    return shineDamper;
  }
  
  public void setShineDamper(float shineDamper) {
    this.shineDamper = shineDamper;
  }
  
  public float getReflectivity() {
    return reflectivity;
  }
  
  public void setReflectivity(float reflectivity) {
    this.reflectivity = reflectivity;
  }
}
