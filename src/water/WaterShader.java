package water;

import entities.Camera;
import entities.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderEngine.ShaderProgram;
import toolBox.Maths;

import java.util.List;

public class WaterShader extends ShaderProgram {
  
  private static final int MAX_LIGHTS = 4;
  
  private final static String VERTEX_FILE = "src/water/waterVertex.glsl";
  private final static String FRAGMENT_FILE = "src/water/waterFragment.glsl";
  
  private int location_projectionMatrix;
  private int location_viewMatrix;
  private int location_modelMatrix;
  private int location_reflectionTexture;
  private int location_refractionTexture;
  private int location_distortionMap;
  private int location_moveFactor;
  private int location_cameraPosition;
  private int location_shineDamper;
  private int location_reflectivity;
  private int[] location_lightPosition;
  private int[] location_lightColor;
  private int[] location_attenuation;
  
  public WaterShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void bindAttributes() {
    bindAttribute(0, "position");
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_projectionMatrix = getUniformLocation("projectionMatrix");
    location_viewMatrix = getUniformLocation("viewMatrix");
    location_modelMatrix = getUniformLocation("modelMatrix");
    location_reflectionTexture = getUniformLocation("reflectionTexture");
    location_refractionTexture = getUniformLocation("refractionTexture");
    location_distortionMap = getUniformLocation("distortionMap");
    location_moveFactor = getUniformLocation("moveFactor");
    location_cameraPosition = getUniformLocation("cameraPosition");
    location_shineDamper = super.getUniformLocation("shineDamper");
    location_reflectivity = super.getUniformLocation("reflectivity");
    location_lightPosition = new int[MAX_LIGHTS];
    location_lightColor = new int[MAX_LIGHTS];
    location_attenuation = new int[MAX_LIGHTS];
    for (int i = 0; i < MAX_LIGHTS; i++) {
      location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
      location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
      location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
    }
  }
  
  public void loadShineVariables(float damper, float reflectivity) {
    super.loadFloat(location_shineDamper, damper);
    super.loadFloat(location_reflectivity, reflectivity);
  }
  
  public void loadLights(List<Light> lights) {
    for (int i = 0; i < MAX_LIGHTS; i++) {
      if (i < lights.size()) {
        super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
        super.loadVector(location_lightColor[i], lights.get(i).getColor());
        super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
      } else {
        super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
        super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
        super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
      }
    }
  }
  
  public void loadCameraPosition(Vector3f cameraPosition) {
    super.loadVector(location_cameraPosition, cameraPosition);
  }
  
  public void loadMoveFactor(float moveFactor) {
    super.loadFloat(location_moveFactor, moveFactor);
  }
  
  public void connectTextureUnits() {
    super.loadInt(location_reflectionTexture, 0);
    super.loadInt(location_refractionTexture, 1);
    super.loadInt(location_distortionMap, 2);
  }
  
  public void loadProjectionMatrix(Matrix4f projection) {
    super.loadMatrix(location_projectionMatrix, projection);
  }
  
  public void loadViewMatrix(Camera camera) {
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    super.loadMatrix(location_viewMatrix, viewMatrix);
  }
  
  public void loadModelMatrix(Matrix4f modelMatrix) {
    super.loadMatrix(location_modelMatrix, modelMatrix);
  }
  
}
