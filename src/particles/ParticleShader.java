package particles;

import entities.Camera;
import org.joml.Matrix4f;
import renderEngine.ShaderProgram;
import toolBox.Maths;

public class ParticleShader extends ShaderProgram {
  
  private static final String VERTEX_FILE = "src/particles/particleVShader.glsl";
  private static final String FRAGMENT_FILE = "src/particles/particleFShader.glsl";
  
  private int location_modelMatrix;
  private int location_projectionMatrix;
  private int location_viewMatrix;
  
  public ParticleShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_modelMatrix = super.getUniformLocation("modelMatrix");
    location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    location_viewMatrix = super.getUniformLocation("viewMatrix");
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
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
