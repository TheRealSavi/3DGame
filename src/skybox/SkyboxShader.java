package skybox;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.ShaderProgram;
import toolBox.Maths;

public class SkyboxShader extends ShaderProgram {
  
  private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.glsl";
  private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.glsl";
  
  private static final float ROTATE_SPEED = 0.5f;
  private static float rotation = 0;
  
  private int location_projectionMatrix;
  private int location_viewMatrix;
  //private int location_fogColor;
  private int location_cubeMap;
  private int location_cubeMap2;
  private int location_blendFactor;
  
  public SkyboxShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    location_viewMatrix = super.getUniformLocation("viewMatrix");
    //location_fogColor = super.getUniformLocation("fogColor");
    location_cubeMap = super.getUniformLocation("cubeMap");
    location_cubeMap2 = super.getUniformLocation("cubeMap2");
    location_blendFactor = super.getUniformLocation("blendFactor");
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
  }
  
  public void loadProjectionMatrix(Matrix4f matrix) {
    super.loadMatrix(location_projectionMatrix, matrix);
  }
  
  public void loadViewMatrix(Camera camera) {
    Matrix4f matrix = Maths.createViewMatrix(camera);
    matrix.m30(0);
    matrix.m31(0);
    matrix.m32(0);
    rotation += ROTATE_SPEED * DisplayManager.getDeltaTime();
    matrix.rotate((float)Math.toRadians(rotation), new Vector3f(0, 1, 0));
    super.loadMatrix(location_viewMatrix, matrix);
  }
  
  //public void loadFogColor(Vector3f fogColor) {
  //  super.loadVector(location_fogColor, fogColor);
  //}
  
  public void loadBlendFactor(float blendFactor) {
    super.loadFloat(location_blendFactor, blendFactor);
  }
  
  public void connectTextureUnits() {
    super.loadInt(location_cubeMap, 0);
    super.loadInt(location_cubeMap2, 1);
  }
  
}
