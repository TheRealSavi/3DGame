package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import postProcessing.Fbo;
import postProcessing.PostProcessor;
import renderEngine.DisplayManager;
import toolBox.Maths;

public class Camera {
  
  private Vector3f position = new Vector3f(0, 0, 0);
  private float pitch = 0;
  private float yaw = 0;
  private float roll = 0;
  
  private float FOV;
  private float NEAR_PLANE;
  private float FAR_PLANE;
  
  private Matrix4f projectionMatrix;
  
  private Fbo fbo;
  
  public Camera(float FOV, float NEAR_PLANE, float FAR_PLANE) {
    this.FOV = FOV;
    this.NEAR_PLANE = NEAR_PLANE;
    this.FAR_PLANE = FAR_PLANE;
    this.projectionMatrix = Maths.createProjectionMatrix(FOV, NEAR_PLANE, FAR_PLANE);
    
    int[] size = DisplayManager.getFrameBufferSize();
    this.fbo = new Fbo(size[0], size[1], size[0], size[1], 0, 0, Fbo.DEPTH_RENDER_BUFFER);
  }
  
  public void setFBO(Fbo _fbo) {
    fbo.cleanUp();
    this.fbo = _fbo;
  }
  
  public Fbo swapFBO(Fbo _fbo) {
    Fbo oldFbo = this.fbo;
    this.fbo = _fbo;
    return oldFbo;
  }
  
  public void swapBackFBO(Fbo _fbo) {
    this.fbo = _fbo;
  }
  
  public void bindFBO() {
    this.fbo.bindFrameBuffer();
  }
  
  public void unbindFBO() {
    this.fbo.unbindFrameBuffer();
  }
  
  public void doPostProcessing() {
    PostProcessor.doPostProcessing(fbo.getColorTexture());
  }
  
  public Vector3f getPosition() {
    return position;
  }
  
  public void setPosition(Vector3f newPos) {
    this.position = newPos;
  }
  
  public void increasePosition(float dx, float dy, float dz) {
    this.position.x += dx;
    this.position.y += dy;
    this.position.z += dz;
  }
  
  public void increasePosition(Vector3f dPos) {
    this.position.add(dPos);
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
  
  public float getNEAR_PLANE() {
    return NEAR_PLANE;
  }
  
  public float getFAR_PLANE() {
    return FAR_PLANE;
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
  
  public void reflectY(float reflectY) {
    float distanceToMove = 2 * (position.y - reflectY);
    setPosition(new Vector3f(position.x, position.y - distanceToMove, position.z));
    setPitch(-pitch);
  }
  
  public void cleanUpFBO() {
    fbo.cleanUp();
  }
}
