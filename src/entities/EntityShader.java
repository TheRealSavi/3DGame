package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderEngine.ShaderProgram;
import toolBox.Maths;

import java.util.List;

public class EntityShader extends ShaderProgram {
  
  private static final int MAX_LIGHTS = 4;
  
  private static final String VERTEX_FILE = "src/entities/vertexShader.glsl";
  private static final String FRAGMENT_FILE = "src/entities/fragmentShader.glsl";
  
  private int location_transformationMatrix;
  private int location_projectionMatrix;
  private int location_viewMatrix;
  private int location_shineDamper;
  private int location_reflectivity;
  private int location_useFakeLighting;
  //private int location_skyColor;
  private int[] location_lightPosition;
  private int[] location_lightColor;
  private int[] location_attenuation;
  
  public EntityShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    location_viewMatrix = super.getUniformLocation("viewMatrix");
    location_shineDamper = super.getUniformLocation("shineDamper");
    location_reflectivity = super.getUniformLocation("reflectivity");
    location_useFakeLighting = super.getUniformLocation("useFakeLighting");
    //location_skyColor = super.getUniformLocation("skyColor");
    
    location_lightPosition = new int[MAX_LIGHTS];
    location_lightColor = new int[MAX_LIGHTS];
    location_attenuation = new int[MAX_LIGHTS];
    for (int i = 0; i < MAX_LIGHTS; i++) {
      location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
      location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
      location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
    }
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
    super.bindAttribute(1, "textureCoords");
    super.bindAttribute(2, "normal");
  }
  
  //public void loadSkyColor(Vector3f skyColor) {
  //  super.loadVector(location_skyColor, skyColor);
  //}
  
  public void loadFakeLightingVariable(boolean useFakeLighting) {
    super.loadBoolean(location_useFakeLighting, useFakeLighting);
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
  
  public void loadTransformationMatrix(Matrix4f transform) {
    super.loadMatrix(location_transformationMatrix, transform);
  }
  
  public void loadProjectionMatrix(Matrix4f projection) {
    super.loadMatrix(location_projectionMatrix, projection);
  }
  
  public void loadViewMatrix(Camera camera) {
    Matrix4f viewMatrix = Maths.createViewMatrix(camera);
    super.loadMatrix(location_viewMatrix, viewMatrix);
  }
  
}
