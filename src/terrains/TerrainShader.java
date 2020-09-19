package terrains;

import entities.Camera;
import lights.DirectionalLight;
import lights.PointLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderEngine.ShaderProgram;
import toolBox.Maths;

import java.util.List;

public class TerrainShader extends ShaderProgram {
  
  private static final int MAX_LIGHTS = 4;
  
  private static final String VERTEX_FILE = "src/terrains/terrainVertexShader.glsl";
  private static final String FRAGMENT_FILE = "src/terrains/terrainFragmentShader.glsl";
  
  private int location_modelMatrix;
  private int location_projectionMatrix;
  private int location_viewMatrix;
  private int location_shineDamper;
  private int location_reflectivity;
  private int location_clippingPlane;
  private int location_fogDensity;
  private int location_backgroundTexture;
  private int location_rTexture;
  private int location_gTexture;
  private int location_bTexture;
  private int location_blendMap;
  private int location_cameraPosition;
  
  private int[] location_pointLightPositions;
  private int[] location_pointLightColors;
  private int[] location_pointLightAttenuations;
  
  private int[] location_directionalLightDirections;
  private int[] location_directionalLightColors;
  
  public TerrainShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_modelMatrix = super.getUniformLocation("modelMatrix");
    location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    location_viewMatrix = super.getUniformLocation("viewMatrix");
    location_shineDamper = super.getUniformLocation("shineDamper");
    location_reflectivity = super.getUniformLocation("reflectivity");
    location_fogDensity = super.getUniformLocation("fogDensity");
    location_clippingPlane = super.getUniformLocation("clippingPlane");
    location_backgroundTexture = super.getUniformLocation("backgroundTexture");
    location_rTexture = super.getUniformLocation("rTexture");
    location_gTexture = super.getUniformLocation("gTexture");
    location_bTexture = super.getUniformLocation("bTexture");
    location_blendMap = super.getUniformLocation("blendMap");
    location_cameraPosition = getUniformLocation("cameraPosition");
    
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
    super.bindAttribute(0, "position");
    super.bindAttribute(1, "textureCoords");
    super.bindAttribute(2, "normal");
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
  
  public void loadFogDensity(float fogDensity) {
    super.loadFloat(location_fogDensity, fogDensity);
  }
  
  public void loadCameraPosition(Vector3f cameraPosition) {
    super.loadVector(location_cameraPosition, cameraPosition);
  }
  
  public void loadTextureUnits() {
    super.loadInt(location_backgroundTexture, 0);
    super.loadInt(location_rTexture, 1);
    super.loadInt(location_gTexture, 2);
    super.loadInt(location_bTexture, 3);
    super.loadInt(location_blendMap, 4);
  }
  
  public void loadClippingPlane(Vector4f clippingPlane) {
    super.loadVector(location_clippingPlane, clippingPlane);
  }
  
  public void loadShineVariables(float damper, float reflectivity) {
    super.loadFloat(location_shineDamper, damper);
    super.loadFloat(location_reflectivity, reflectivity);
  }
  
  public void loadModelMatrix(Matrix4f matrix) {
    super.loadMatrix(location_modelMatrix, matrix);
  }
  
  public void loadProjectionMatrix(Matrix4f projection) {
    super.loadMatrix(location_projectionMatrix, projection);
  }
  
  public void loadViewMatrix(Camera camera) {
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    super.loadMatrix(location_viewMatrix, viewMatrix);
  }
}
