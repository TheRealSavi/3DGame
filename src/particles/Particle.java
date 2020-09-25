package particles;


import org.joml.Vector3f;
import renderEngine.DisplayManager;

public class Particle {
  
  private Vector3f position;
  private Vector3f velocity;
  private float gravity;
  private float rotation;
  private float scale;
  
  private float lifeTime = 0;
  private final float lifeLength;
  private boolean isAlive = true;
  
  public Particle(Vector3f position, Vector3f velocity, float gravity, float rotation, float scale, float lifeLength) {
    this.position = position;
    this.velocity = velocity;
    this.gravity = gravity;
    this.rotation = rotation;
    this.scale = scale;
    this.lifeLength = lifeLength;
  }
  
  public void update() {
    velocity.add(0, -gravity, 0);
    
    Vector3f deltaPos = new Vector3f();
    velocity.mul((float)DisplayManager.getDeltaTime(), deltaPos);
    position.add(deltaPos);
    
    lifeTime += DisplayManager.getDeltaTime();
    this.isAlive = lifeTime < lifeLength;
  }
  
  public boolean isAlive() {
    return isAlive;
  }
  
  public Vector3f getPosition() {
    return position;
  }
  
  public float getRotation() {
    return rotation;
  }
  
  public float getScale() {
    return scale;
  }
  
  public float getLifeTime() {
    return lifeTime;
  }
  
  public float getLifeLength() {
    return lifeLength;
  }
}
