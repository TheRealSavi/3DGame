package water;

import entities.Camera;
import lights.DirectionalLight;
import lights.PointLight;
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
  private int location_fogDensity;
  private int location_reflectivity;
  private int location_depthMap;
  private int location_cameraNearPlane;
  private int location_cameraFarPlane;
  
  private int[] location_pointLightPositions;
  private int[] location_pointLightColors;
  private int[] location_pointLightAttenuations;
  
  private int[] location_directionalLightDirections;
  private int[] location_directionalLightColors;
  
  public WaterShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_projectionMatrix = getUniformLocation("projectionMatrix");
    location_viewMatrix = getUniformLocation("viewMatrix");
    location_modelMatrix = getUniformLocation("modelMatrix");
    location_reflectionTexture = getUniformLocation("reflectionTexture");
    location_refractionTexture = getUniformLocation("refractionTexture");
    location_distortionMap = getUniformLocation("distortionMap");
    location_fogDensity = getUniformLocation("fogDensity");
    location_moveFactor = getUniformLocation("moveFactor");
    location_cameraPosition = getUniformLocation("cameraPosition");
    location_shineDamper = super.getUniformLocation("shineDamper");
    location_reflectivity = super.getUniformLocation("reflectivity");
    location_depthMap = super.getUniformLocation("depthMap");
    location_cameraNearPlane = super.getUniformLocation("cameraNearPlane");
    location_cameraFarPlane = super.getUniformLocation("cameraFarPlane");
    
    location_pointLightPositions = new int[MAX_LIGHTS];
    location_pointLightColors = new int[MAX_LIGHTS];
    location_pointLightAttenuations = new int[MAX_LIGHTS];
    
    for (int i = 0; i < MAX_LIGHTS; i++) {
      location_pointLightPositions[i] = super.getUniformLocation("pointLightPositions[" + i + "]");
      location_pointLightColors[i] = super.getUniformLocation("pointLightColors[" + i + "]");
      location_pointLightAttenuations[i] = super.getUniformLocation("pointLightAttenuations[" + i + "]");
    }
  
    location_directionalLightDirections = new int[MAX_LIGHTS];
    location_directionalLightColors = new int[MAX_LIGHTS];
  
    for (int i = 0; i < MAX_LIGHTS; i++) {
      location_directionalLightDirections[i] = super.getUniformLocation("directionalLightDirections[" + i + "]");
      location_directionalLightColors[i] = super.getUniformLocation("directionalLightColors[" + i + "]");
    }
  }
  
  @Override
  protected void bindAttributes() {
    bindAttribute(0, "position");
  }
  
  public void loadPointLights(List<PointLight> pointLights) {
    for (int i = 0; i < MAX_LIGHTS; i++) {
      if (i < pointLights.size()) {
        super.loadVector(location_pointLightPositions[i], pointLights.get(i).getPosition());
        super.loadVector(location_pointLightColors[i], pointLights.get(i).getColor());
        super.loadVector(location_pointLightAttenuations[i], pointLights.get(i).getAttenuation());
      } else {
        super.loadVector(location_pointLightPositions[i], new Vector3f(0, 0, 0));
        super.loadVector(location_pointLightColors[i], new Vector3f(0, 0, 0));
        super.loadVector(location_pointLightAttenuations[i], new Vector3f(1, 0, 0));
      }
    }
  }
  
  public void loadDirectionalLights(List<DirectionalLight> directionalLights) {
    for (int i = 0; i < MAX_LIGHTS; i++) {
      if (i < directionalLights.size()) {
        super.loadVector(location_directionalLightDirections[i], directionalLights.get(i).getDirection());
        super.loadVector(location_directionalLightColors[i], directionalLights.get(i).getColor());
      } else {
        super.loadVector(location_directionalLightDirections[i], new Vector3f(0, 0, 0));
        super.loadVector(location_directionalLightColors[i], new Vector3f(0, 0, 0));
      }
    }
  }
  
  public void loadShineVariables(float damper, float reflectivity) {
    super.loadFloat(location_shineDamper, damper);
    super.loadFloat(location_reflectivity, reflectivity);
  }
  
  public void loadFogDensity(float fogDensity) {
    super.loadFloat(location_fogDensity, fogDensity);
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
    super.loadInt(location_depthMap, 3);
  }
  
  public void loadProjectionMatrix(Matrix4f projection) {
    super.loadMatrix(location_projectionMatrix, projection);
  }
  
  public void loadViewMatrix(Camera camera) {
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    super.loadMatrix(location_viewMatrix, viewMatrix);
    
    super.loadFloat(location_cameraNearPlane, camera.getNEAR_PLANE());
    super.loadFloat(location_cameraFarPlane, camera.getFAR_PLANE());
  }
  
  public void loadModelMatrix(Matrix4f matrix) {
    super.loadMatrix(location_modelMatrix, matrix);
  }
  
}
