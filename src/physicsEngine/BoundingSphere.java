package physicsEngine;

import org.joml.Vector3f;

public class BoundingSphere {
  private final Vector3f origin;
  private final float radius;
  
  public BoundingSphere(Vector3f origin, float radius) {
    this.origin = origin;
    this.radius = radius;
  }
  
  public IntersectData isIntersectingSphere(BoundingSphere other) {
    float radiusDistance = this.radius + other.getRadius();
    float originDistance = (other.getOrigin().sub(this.origin)).length();
    
    if (originDistance < radiusDistance) {
      return new IntersectData(true, originDistance - radiusDistance);
    } else {
      return new IntersectData(false, originDistance - radiusDistance);
    }
  }
  
  public Vector3f getOrigin() {
    return origin;
  }
  
  public float getRadius() {
    return radius;
  }
}
