package screenRenderers;

import renderEngine.ShaderProgram;

public class SingleScreenShader extends ShaderProgram {
  
  private static final String VERTEX_FILE = "src/screenRenderers/singleScreenVertex.glsl";
  private static final String FRAGMENT_FILE = "src/screenRenderers/singleScreenFragment.glsl";
  
  private int location_player1texture;
  
  public SingleScreenShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }
  
  @Override
  protected void getAllUniformLocations() {
    location_player1texture = getUniformLocation("player1texture");
  }
  
  @Override
  protected void bindAttributes() {
    super.bindAttribute(0, "position");
  }
  
  public void connectTextureUnits() {
    super.loadInt(location_player1texture, 0);
  }
  
}
