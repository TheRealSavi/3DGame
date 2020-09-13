package toolBox;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderEngine.DisplayManager;

public class MousePicker {
  
  public static Vector3f createMouseRay(Camera camera) {
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    Matrix4f projectionMatrix = Maths.createProjectionMatrix(camera.getFOV(), camera.getNEAR_PLANE(), camera.getFAR_PLANE());
    
    //get viewport space
    Vector2f viewportSpace = new Vector2f(DisplayManager.getWidth() / 2f, DisplayManager.getHeight() / 2f);
    
    //convert to normalized device space
    Vector2f normalizedDeviceSpace = new Vector2f((2f * viewportSpace.x) / DisplayManager.getWidth() - 1f, (2f * viewportSpace.y) / DisplayManager.getHeight() -1f);
  
    //convert to clip space (just add a -1 in the z so that it faces inward)
    Vector4f clipSpace = new Vector4f(normalizedDeviceSpace.x, normalizedDeviceSpace.y, -1f, 1f);
    
    //convert to camera space using the inverse projection matrix
    Matrix4f inverseProjectionMatrix = new Matrix4f();
    projectionMatrix.invert(inverseProjectionMatrix);
    
    Vector4f cameraSpace = new Vector4f();
    inverseProjectionMatrix.transform(clipSpace, cameraSpace);
    
    cameraSpace.z = -1f;
    cameraSpace.w = 0f;
    
    //convert to a direction in world space using the inverse view matrix
    Matrix4f inverseViewMatrix = new Matrix4f();
    viewMatrix.invert(inverseViewMatrix);
    
    Vector4f rayIntoWorld = new Vector4f();
    inverseViewMatrix.transform(cameraSpace, rayIntoWorld);
    
    Vector3f mouseRay = new Vector3f(rayIntoWorld.x, rayIntoWorld.y, rayIntoWorld.z);
    
    Vector3f mouseRayNormalized = new Vector3f();
    mouseRay.normalize(mouseRayNormalized);
    
    return  mouseRay;
  }

}
