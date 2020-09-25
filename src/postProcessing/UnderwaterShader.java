package postProcessing;

import renderEngine.ShaderProgram;

public class UnderwaterShader extends ShaderProgram {
  
  private static final String VERTEX_FILE = "src/postProcessing/underwaterVertex.glsl";
  private static final String FRAGMENT_FILE = "src/postProcessing/underwaterFragment.glsl";
  
  private int location_waterHeight;
  private int location_cameraHeight;
  private int location_distortionMap;
  private int location_moveFactor;
  
  public UnderwaterShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_waterHeight = getUniformLocation("waterHeight");
    location_cameraHeight = getUniformLocation("cameraHeight");
    location_distortionMap = getUniformLocation("distortionMap");
    location_moveFactor = getUniformLocation("moveFactor");
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
    super.loadInt(location_distortionMap, 1);
  }
  
  public void loadMoveFactor(float move) {
    super.loadFloat(location_moveFactor, move);
  }
  
  public void loadWaterHeight(int height) {
    super.loadInt(location_waterHeight, height);
  }
  
  public void loadCameraHeight(int height) {
    super.loadInt(location_cameraHeight, height);
  }
  
}
