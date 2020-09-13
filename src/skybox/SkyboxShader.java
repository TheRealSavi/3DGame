package skybox;

import entities.Camera;
import lights.DirectionalLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.ShaderProgram;
import toolBox.Maths;

public class SkyboxShader extends ShaderProgram {
  
  private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.glsl";
  private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.glsl";
  
  private static float rotation = 0;
  private static final float ROTATE_SPEED = 50f;
  
  private int location_projectionMatrix;
  private int location_viewMatrix;
  private int location_cubeMap;
  private int location_cubeMap2;
  private int location_blendFactor;
  private int location_cameraPosition;
  private int location_directionalLightDirection;
  private int location_fogDensity;
  
  public SkyboxShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    location_viewMatrix = super.getUniformLocation("viewMatrix");
    location_cubeMap = super.getUniformLocation("cubeMap");
    location_cubeMap2 = super.getUniformLocation("cubeMap2");
    location_blendFactor = super.getUniformLocation("blendFactor");
    location_cameraPosition = super.getUniformLocation("cameraPosition");
    location_directionalLightDirection = super.getUniformLocation("directionalLightDirection");
    location_fogDensity = super.getUniformLocation("fogDensity");
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
  }
  
  public void loadFogDensity(float fogDensity) {
    super.loadFloat(location_fogDensity, fogDensity);
  }
  
  public void loadCameraPosition(Vector3f cameraPosition) {
    super.loadVector(location_cameraPosition, cameraPosition);
  }
  
  public void loadDirectionalLight(DirectionalLight directionalLight) {
    super.loadVector(location_directionalLightDirection, directionalLight.getDirection());
  }
  
  public void loadProjectionMatrix(Matrix4f matrix) {
    super.loadMatrix(location_projectionMatrix, matrix);
  }
  
  public void loadViewMatrix(Camera camera) {
    Matrix4f matrix = Maths.createViewMatrix(camera);
    matrix.m30(0);
    matrix.m31(0);
    matrix.m32(0);
    matrix.rotate((float)Math.toRadians(rotation), new Vector3f(0, 1, 0));
    super.loadMatrix(location_viewMatrix, matrix);
  }
  
  public void rotateSkybox() {
    rotation += ROTATE_SPEED * DisplayManager.getDeltaTime();
  }
  
  public void loadBlendFactor(float blendFactor) {
    super.loadFloat(location_blendFactor, blendFactor);
  }
  
  public void connectTextureUnits() {
    super.loadInt(location_cubeMap, 0);
    super.loadInt(location_cubeMap2, 1);
  }
  
}
