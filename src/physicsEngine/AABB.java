package physicsEngine;

import org.joml.Vector3f;

public class AABB {
  
  private final Vector3f minExtends;
  private final Vector3f maxExtends;
  
  public AABB(Vector3f minExtends, Vector3f maxExtends) {
    this.minExtends = minExtends;
    this.maxExtends = maxExtends;
  }
  
  public IntersectData IntersectsAABB(AABB other) {
    Vector3f distances1 = other.getMinExtends().sub(this.maxExtends);
    Vector3f distances2 = this.minExtends.sub(other.getMaxExtends());
    Vector3f distances = distances1.max(distances2);
    float maxDistance = distances.maxComponent();
    
    return new IntersectData(maxDistance < 0, maxDistance);
  }
  
  public Vector3f getMinExtends() {
    return minExtends;
  }
  
  public Vector3f getMaxExtends() {
    return maxExtends;
  }
}
