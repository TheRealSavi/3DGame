package screenRenderers;

import renderEngine.ShaderProgram;

public class SplitScreenShader extends ShaderProgram {
  
  private static final String VERTEX_FILE = "src/screenRenderers/splitScreenVertex.glsl";
  private static final String FRAGMENT_FILE = "src/screenRenderers/splitScreenFragment.glsl";
  
  private int location_player1texture;
  private int location_player2texture;
  
  public SplitScreenShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_player1texture = getUniformLocation("player1texture");
    location_player2texture = getUniformLocation("player2texture");
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
  }
  
  public void connectTextureUnits() {
    super.loadInt(location_player1texture, 0);
    super.loadInt(location_player2texture, 1);
  }
  
}
