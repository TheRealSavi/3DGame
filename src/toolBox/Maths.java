package toolBox;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.DisplayManager;

public class Maths {
  
  public static Matrix4f createProjectionMatrix(float FOV, float NEAR_PLANE, float FAR_PLANE) {
    float aspectRatio = (float)DisplayManager.getWidth() / (float)DisplayManager.getHeight();
    float y_scale = (float)((1f / Math.tan(Math.toRadians(FOV / 2f))));
    float x_scale = y_scale / aspectRatio;
    float frustum_length = FAR_PLANE - NEAR_PLANE;
    
    Matrix4f projectionMatrix = new Matrix4f();
    projectionMatrix.m00(x_scale);
    projectionMatrix.m11(y_scale);
    projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
    projectionMatrix.m23(-1f);
    projectionMatrix.m32(-((2 * FAR_PLANE * NEAR_PLANE) / frustum_length));
    projectionMatrix.m33(0f);
    return projectionMatrix;
  }
  
  public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
    Matrix4f matrix = new Matrix4f();
    //matrix.setIdentity();
    matrix.translate(translation.x, translation.y, 0);
    matrix.scale(new Vector3f(scale.x, scale.y, 1f));
    return matrix;
  }
  
  public static Matrix4f createModelMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
    Matrix4f matrix = new Matrix4f();
    //matrix.setIdentity;
    matrix.translate(translation);
    matrix.rotate((float)Math.toRadians(rx), new Vector3f(1, 0, 0));
    matrix.rotate((float)Math.toRadians(ry), new Vector3f(0, 1, 0));
    matrix.rotate((float)Math.toRadians(rz), new Vector3f(0, 0, 1));
    matrix.scale(scale);
    return matrix;
    
  }
  
  public static Matrix4f createViewMatrix(Camera camera) {
    Matrix4f viewMatrix = new Matrix4f();
    //viewMatrix.setIdentity;
    viewMatrix.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0));
    viewMatrix.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
    viewMatrix.rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1));
    Vector3f cameraPos = camera.getPosition();
    Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    viewMatrix.translate(negativeCameraPos);
    return viewMatrix;
  }
  
}
